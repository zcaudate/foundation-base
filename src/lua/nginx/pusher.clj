(ns lua.nginx.pusher
  (:require [std.lib :as h]
            [std.lang :as l]))

(l/script :lua
  {:require [[lua.nginx.ws-client :as ws-client]
             [lua.nginx :as n]
             [xt.lang.event-log :as event-log]
             [xt.lang.base-lib :as k]]
   :export [MODULE]})

(def.lua DEFAULT_WAIT_MAIN 1)

(def.lua DEFAULT_WAIT_LOOP 5)

(def.lua DEFAULT_WAIT_PING 60)

(def.lua DEFAULT_WAIT_TIMEOUT 90)

(defn.lua make-pusher-defaults
  "makes pusher defaults"
  {:added "4.0"}
  []
  (return
   {:protocol "7"
    :client "lua-pusher"
    :version "4.0.1"
    :scheme "wss"}))

(defn.lua make-pusher-url
  "makes a pusher url"
  {:added "4.0"}
  [options]
  (var #{scheme
         host
         port
         client
         version
         protocol
         app-key} options)
  (return (k/cat scheme "://" host (:? port
                                       (k/cat ":" port)
                                       "")
                 "/app/" app-key
                 "?client=" client "&version=" version "&protocol=" protocol)))

(defn.lua make-pusher-raw
  "makes a pusher record"
  {:added "4.0"}
  [options init-fn]
  (return
   {"::" "pusher"
    :active  false
    :history (event-log/new-log)
    :init    init-fn
    :options (k/obj-assign (-/make-pusher-defaults)
                            options)}))

(defn.lua make-pusher-connect
  "creates a pusher connection object"
  {:added "4.0"}
  [pusher]
  (var #{options} pusher)
  (var ws (ws-client/new))
  (var url (-/make-pusher-url options))
  (var '[ok err] (ws-client/connect ws url {:ssl_verify false}))
  (when err
    (k/err "CONNECT ERROR" {:message err :url url}))
  (var '[data typ err] (ws-client/recv-frame ws))
  (when err
    (k/err "HANDSHAKE ERROR" {:message err}))
  (when (not= typ "text")
    (k/err "PROTOCOL ERROR" {:message [typ data]}))
  
  (var handshake (k/js-decode data))
  (var #{event data} handshake)
  (when (not= event "pusher:connection_established")
    (k/err "INITIALISATION ERROR" {:message [event data]}))
  (var socket-id (k/get-key (k/js-decode data) "socket_id"))
  (var connection
       {:ws ws
        :last-received (k/now-ms)
        :status "connected"
        :socket-id socket-id
        :timeout false
        :threads {}})
  (return connection))

(defn.lua make-pusher-prep
  "prepares the pusher"
  {:added "4.0"}
  [options]
  (var p (-/make-pusher-raw
          options))
  (k/set-key p "active" true)
  (var conn (-/make-pusher-connect p))
  (k/set-key p "connection" conn)
  (return p))

(defn.lua send-event-raw
  "sends a pusher event"
  {:added "4.0"}
  [ws event data no-encode]
  (return
   (ws-client/send-text ws (k/js-encode {:event (k/cat "pusher:" event)
                                         :data  (:? no-encode
                                                    data
                                                    (k/js-encode data))}))))


(defn.lua send-event
  [pusher event data no-encode]
  (var ws (k/get-in pusher ["connection" "ws"]))
  (return (-/send-event-raw ws event data no-encode)))

(defn.lua log-history
  [pusher log]
  (return
   (event-log/queue-entry (. pusher history)
                          log
                          k/random
                          (fn:> [e] (k/obj-assign e {:t (k/now-ms)}))
                          (n/now))))

;;
;; In the pusher api, when pusher is started
;;
;; - 3 threads are created
;;
;;   - heartbeat   * sends a ping
;;   - listener    * listens for messages on the channel
;;   - main        * main control thread, checking status on
;;                   heartbeat and listener threads, conforming
;;                   to active flag
;;

(defn.lua start-heartbeat-loop-fn
  "performs a heartbeat loop step"
  {:added "4.0"}
  [pusher connection]
  (var #{active} pusher)
  (var #{last-received
         status
         timeout
         ws} connection)
  (when (not active)
    (return [false {:type "error"
                    :message "not active"
                    :thread "heartbeat"}]))
  (when timeout
    (return [false {:type "error"
                    :message "connection timeout"
                    :thread "heartbeat"}]))
  (var '[bytes err] (-/send-event-raw ws "ping" {}))
  (cond err
        (return [false {:type "error"
                        :message "event error"
                        :thread "heartbeat"}])
        
        :else
        (return [true {:type "sent"
                       :data bytes
                       :thread "heartbeat"}])))

(defn.lua start-heartbeat-loop
  "starts a heartbeat loop"
  {:added "4.0"}
  [pusher connection options]
  (var #{wait-loop
         wait-ping
         wait-timeout
         no-init} options)
  (var [cont out]
       (:? no-init
           [true]
           (-/start-heartbeat-loop-fn pusher connection)))
  (when (not no-init)
    (n/sleep (or wait-loop -/DEFAULT_WAIT_LOOP)))

  (var last-elapsed)
  (while cont
    (var #{last-received} connection)
    (:= last-elapsed (- (k/now-ms)
                         last-received))
    (cond (. connection timeout)
          (break)
          
          (> last-elapsed (* (or wait-timeout -/DEFAULT_WAIT_TIMEOUT)
                             1000))
          (break)

          (> last-elapsed (* (or wait-ping -/DEFAULT_WAIT_PING)
                             1000))
          (do (:= '[cont out] (k/unpack (-/start-heartbeat-loop-fn pusher connection)))
              (-/log-history pusher (k/obj-assign
                                     {:event "thread:ping-heartbeat"
                                      :last-elapsed last-elapsed}
                                     out))))
    
    (n/sleep (or wait-loop -/DEFAULT_WAIT_LOOP)))
  (-/log-history pusher (k/obj-assign
                         {:event "thread:stop-heartbeat"
                          :last-elapsed last-elapsed}
                         out))
  (var #{threads} connection)
  (k/del-key threads "heartbeat")
  (return connection))

(defn.lua start-heartbeat
  "starts the heartbeat thread"
  {:added "4.0"}
  [pusher options]
  (var #{connection} pusher)
  (-/log-history pusher {:event "thread:start-heartbeat"})
  (var thread (n/thread-spawn
               (fn []
                 (return
                  (-/start-heartbeat-loop pusher connection options)))))
  (k/set-in connection ["threads" "heartbeat"] thread)
  (return thread))

;;
;; start listener  
;;

(defn.lua update-last-received
  "updates last received frame"
  {:added "4.0"}
  [pusher]
  (return
   (k/set-in pusher
             ["connection" "last_received"]
             (k/now-ms))))

(defn.lua start-listener-loop-fn
  "performs a listener loop step"
  {:added "4.0"}
  [pusher connection]
  (var #{active} pusher)
  (var #{timeout
         ws} connection)
  (when (not active)
    (return [false {:type "error"
                    :message "not active"
                    :thread "listener"}]))
  (when timeout  
    (return [false {:type "error"
                    :message "connection timeout"
                    :thread "listener"}]))
  (var '[data typ err] (ws-client/recv-frame ws))

  (when err
    (return [true {:type "timeout"
                   :message err
                   :thread "listener"}]))
  (when timeout  
    (return [false {:type "error"
                    :message "connection timeout"
                    :thread "listener"}]))
  
  (cond (== typ "ping")
        (do (var '[bytes err] (ws-client/send-pong data))
            (if err
              (return [false {:type "error"
                              :message err}])
              (return [true  {:type "ping"}])))
        
        (== typ "pong")
        (do (-/update-last-received pusher)
            (return [true  {:type "pong"}]))
        
        (or (== typ "text")
            (== typ "binary"))
        (do (var '[ok m] (pcall (fn []
                                  (var out (k/js-decode data))
                                  (var #{event data channel} out)
                                  (return
                                   {:type "event"
                                    :event event
                                    :channel channel
                                    :data (k/js-decode data)}))))
            (cond (not ok)
                  (return [false {:type "error"
                                  :message m}])

                  :else
                  (do (-/update-last-received pusher)
                      (return [true  m]))))
        
        (== typ "close")
        (return [false {:type "close"}])

        :else
        (return [true {:type "other"}])))

(defn.lua start-listener-loop
  "performs the start listener loop"
  {:added "4.0"}
  [pusher connection]
  (while (k/get-key pusher "active")
    (var [cont out] (-/start-listener-loop-fn pusher connection))
    (var #{type} out)
    (when (not= type "timeout")
      (-/log-history pusher out))
    (when (not cont)
      (break))
    (n/sleep 0.1))
  (-/log-history pusher {:event "thread:stop-listener"})
  (var #{threads} connection)
  (k/del-key threads "listener")
  (return connection))

(defn.lua start-listener
  "starts the listener loop"
  {:added "4.0"}
  [pusher]
  (var #{connection} pusher)
  (-/log-history pusher {:event "thread:start-listener"})
  (var thread (n/thread-spawn
               (fn []
                 (return
                  (-/start-listener-loop pusher connection)))))
  (k/set-in connection ["threads" "listener"] thread)
  (return thread))

;;
;; start main thread
;;

(defn.lua start-main-loop-init
  [pusher options]
  (var #{init} pusher)
  (var conn)
  (while true
    (var '[ok out] (pcall (fn [] (return (-/make-pusher-connect pusher)))))
    (when ok
      (n/log! "CONNECTION ESTABLISED" {:input (. pusher options)})
      (:= conn out)
      (break))
    (n/log! "RETRYING CONNECTION" {:message out
                                   :input (. pusher options)})
    (n/sleep 5))
  (k/set-key pusher "connection" conn)
  (-/start-listener  pusher)
  (-/start-heartbeat pusher options)
  (when init
    (init pusher))
  (return pusher))

(defn.lua start-main-loop
  "starts the pusher main loop"
  {:added "4.0"}
  [pusher options]
  (var #{wait-main} options)
  (while true
    (var #{active} pusher)
    (when (not active)
      (break))
    (when (not (. pusher connection))
      (-/start-main-loop-init pusher options))
    (var #{timeout} (. pusher connection))
    (when timeout
      (when (. pusher connection ws)
        (ws-client/send-close (. pusher connection ws)))
      (-/start-main-loop-init pusher options))
    (n/sleep (or wait-main -/DEFAULT_WAIT_MAIN)))
  (k/del-key pusher "main")
  (-/log-history pusher {:event "thread:stop-main"})
  (return pusher))

(defn.lua start-main
  "starts the main pusher thread"
  {:added "4.0"}
  [pusher options]
  (-/log-history pusher {:event "thread:start-main"})
  (var thread (n/thread-spawn
               (fn []
                 (return
                  (-/start-main-loop pusher options)))))
  (k/set-in pusher ["main"] thread)
  (return thread))

(defn.lua pusher-activate
  "activates pusher flag"
  {:added "4.0"}
  [pusher options]
  (when (not (. pusher active))
    (k/set-in pusher ["active"] true)
    (-/start-main-loop-init pusher options)
    (-/start-main pusher options))
  (return pusher))

(defn.lua pusher-deactivate
  "deactivates pusher flag"
  {:added "4.0"}
  [pusher]
  (when (. pusher active)
    (k/set-in pusher ["active"] false)
    (k/set-in pusher ["connection"] nil))
  (return pusher))

(defn.lua pusher-add-listener
  [pusher listener-id callback meta]
  (var #{history} pusher)
  (return (event-log/add-listener history
                                  listener-id
                                  callback
                                  meta)))

(def.lua MODULE (!:module))
