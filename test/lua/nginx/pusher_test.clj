(ns lua.nginx.pusher-test
  (:use code.test)
  (:require [std.lib :as h]
            [std.lang :as l]
            [xt.lang.base-notify :as notify]))

(l/script- :lua
  {:runtime :basic
   :config {:program :resty}
   :require [[lua.nginx.ws-client :as ws-client]
             [lua.nginx :as n]
             [xt.lang.event-log :as event-log]
             [xt.lang.base-lib :as k]
             [xt.lang.base-repl :as repl]
             [lua.nginx.pusher :as pusher]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer lua.nginx.pusher/make-pusher-defaults :added "4.0"}
(fact "makes pusher defaults"
  ^:hidden
  
  (pusher/make-pusher-defaults)
  => {"protocol" "7",
      "version" "4.0.1",
      "client" "lua-pusher",
      "scheme" "wss"})

^{:refer lua.nginx.pusher/make-pusher-url :added "4.0"}
(fact "makes a pusher url"
  ^:hidden

  (pusher/make-pusher-url
   (k/obj-assign
    {:host "pusher.chain.so"
     :app-key "e9f5cc20074501ca7395"
     :port 443}
    (pusher/make-pusher-defaults)))
  => "wss://pusher.chain.so:443/app/e9f5cc20074501ca7395?client=lua-pusher&version=4.0.1&protocol=7")

^{:refer lua.nginx.pusher/make-pusher-raw :added "4.0"}
(fact "makes a pusher record"
  ^:hidden
  
  (pusher/make-pusher-raw
   {:host "pusher.chain.so"
    :app-key "e9f5cc20074501ca7395"})
  => (contains-in
      {"::" "pusher",
       "history" {"::" "event.log",},
       "active" false,
       
       "options"
       {"protocol" "7",
        "host" "pusher.chain.so",
        "version" "4.0.1",
        "client" "lua-pusher",
        "scheme" "wss",
        "app_key" "e9f5cc20074501ca7395"}}))

^{:refer lua.nginx.pusher/make-pusher-connect :added "4.0"
  :setup    [(l/rt:restart)]}
(fact "creates a pusher connection object"
  ^:hidden

  (notify/wait-on :lua
    (var p (pusher/make-pusher-raw
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (var conn (pusher/make-pusher-connect p))
    (repl/notify (k/obj-omit conn ["ws"])))
  => (contains-in
      {"threads" {},
       "last_received" integer?
       "status" "connected",
       "timeout" false,
       "socket_id" string?}))

^{:refer lua.nginx.pusher/make-pusher-prep :added "4.0"
  :setup [(l/rt:restart)]}
(fact "prepares the pusher"
  ^:hidden
  
  (notify/wait-on :lua
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (repl/notify (k/set-in p ["connection" "ws"] nil)))
  => (contains-in
      {"::" "pusher",
       "connection"
       {"threads" {},
        "last_received" integer?
        "status" "connected",
        "timeout" false,
        "socket_id" string?},
       "history" map?,
       "active"  true,
       "options" map?}))

^{:refer lua.nginx.pusher/send-event-raw :added "4.0"
  :setup [(l/rt:restart)]}
(fact "sends a pusher event"
  ^:hidden

  (notify/wait-on :lua
    (var ws (ws-client/new))
    (var '[ok err] (ws-client/connect ws (pusher/make-pusher-url
                                          (k/obj-assign
                                           {:host "pusher.chain.so"
                                            :port 443
                                            :app-key "e9f5cc20074501ca7395"}
                                           (pusher/make-pusher-defaults)))
                                      {:ssl_verify false}))
    (var '[data0 typ0 err] (ws-client/recv-frame ws))
    (var '[bytes1 err] (pusher/send-event-raw
                        ws "ping" {}))
    (var '[data1 typ1 err] (ws-client/recv-frame ws))
    (repl/notify [[(k/js-decode data0) typ0 err]
                  [bytes1]
                  [(k/js-decode data1) typ1 err]]))
  => (contains-in
      [[{"event" "pusher:connection_established",
         "data" string?}
        "text"]
       [41]
       [{"event" "pusher:pong", "data" "{}"} "text"]]))

^{:refer lua.nginx.pusher/send-event :added "4.0"}
(fact "sends pusher event on connection")

^{:refer lua.nginx.pusher/log-history :added "4.0"}
(fact "logs pusher history")

^{:refer lua.nginx.pusher/start-heartbeat-loop-fn :added "4.0"}
(fact "performs a heartbeat loop step"
  ^:hidden

  ;;
  ;; CORRECT STATE
  ;;
  (do (l/rt:restart)
      (notify/wait-on :lua
        (var p (pusher/make-pusher-raw
                {:host "pusher.chain.so"
                 :port 443
                 :app-key "e9f5cc20074501ca7395"}))
        (var conn (pusher/make-pusher-connect p))
        (k/set-key p "active" true)
        (repl/notify (pusher/start-heartbeat-loop-fn p conn))))
  => [true {"data" 41 "type" "sent" "thread" "heartbeat"}]


  ;;
  ;; ASYNC RECIEVE STATES
  ;;
  (notify/wait-on [:lua 5000]
   (var p (pusher/make-pusher-raw
           {:host "pusher.chain.so"
            :port 443
            :app-key "e9f5cc20074501ca7395"}))
   (var conn (pusher/make-pusher-connect p))
   (k/set-key p  "active" true)
   (n/thread-spawn
    (fn []
      (pusher/start-heartbeat-loop-fn p conn)))
   (n/thread-spawn
    (fn []
      (var '[data typ err] (ws-client/recv-frame (. conn ws)))
      (repl/notify [data typ]))))
  => ["{\"event\":\"pusher:pong\",\"data\":\"{}\"}" "text"]

  
  ;;
  ;; ERROR STATES
  ;;
  (do (l/rt:restart)
      (notify/wait-on :lua
        (var p (pusher/make-pusher-raw
                {:host "pusher.chain.so"
                 :port 443
                 :app-key "e9f5cc20074501ca7395"}))
        (var conn (pusher/make-pusher-connect p))
        (repl/notify (pusher/start-heartbeat-loop-fn p conn))))
  => [false {"message" "not active" "type" "error" "thread" "heartbeat"}]

  (do (l/rt:restart)
      (notify/wait-on :lua
        (var p (pusher/make-pusher-raw
                {:host "pusher.chain.so"
                 :port 443
                 :app-key "e9f5cc20074501ca7395"}))
        (var conn (pusher/make-pusher-connect p))
        (k/set-key p  "active" true)
        (k/set-key conn "timeout" true)
        (repl/notify (pusher/start-heartbeat-loop-fn p conn))))
  => [false {"message" "connection timeout" "type" "error"  "thread" "heartbeat"}])

^{:refer lua.nginx.pusher/start-heartbeat-loop :added "4.0"
  :setup [(l/rt:restart)]}
(fact "starts a heartbeat loop"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-raw
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (var conn (pusher/make-pusher-connect p))
    (k/set-key p  "active" true)
    (n/thread-spawn
     (fn []
       (k/set-in conn ["last_received"] (- (k/now-ms)
                                           (* 70 1000)))
       (pusher/start-heartbeat-loop p conn {:wait-loop 0.1
                                            :no-init true})))
    (n/thread-spawn
     (fn []
       (var '[data typ err] (ws-client/recv-frame (. conn ws)))
       (repl/notify [data typ]))))
  => ["{\"event\":\"pusher:pong\",\"data\":\"{}\"}" "text"])

^{:refer lua.nginx.pusher/start-heartbeat :added "4.0"
  :setup [(l/rt:restart)]}
(fact "starts the heartbeat thread"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-in p ["connection" "last_received"] (- (k/now-ms)
                                                  (* 70 1000)))
    (pusher/start-heartbeat p {:no-init true})

    (var '[data typ err] (ws-client/recv-frame (k/get-in p ["connection" "ws"])))
    (repl/notify [data typ]))
  => ["{\"event\":\"pusher:pong\",\"data\":\"{}\"}" "text"]

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-in p ["connection" "last_received"] (- (k/now-ms)
                                                  (* 70 1000)))
    (pusher/start-heartbeat p {:no-init true})
    (n/thread-spawn
     (fn []
       (var '[data typ err] (ws-client/recv-frame (k/get-in p ["connection" "ws"])))
       (repl/notify [data typ]))))
  => ["{\"event\":\"pusher:pong\",\"data\":\"{}\"}"
      "text"])

^{:refer lua.nginx.pusher/update-last-received :added "4.0"}
(fact "updates last received frame")

^{:refer lua.nginx.pusher/start-listener-loop-fn :added "4.0"
  :setup [(l/rt:restart)]}
(fact "performs a listener loop step"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-in p ["connection" "last_received"] (- (k/now-ms)
                                                  (* 70 1000)))
    (pusher/start-heartbeat p {:no-init true})
    (repl/notify
     (pusher/start-listener-loop-fn p (. p connection))))
  => [true {"event" "pusher:pong", "type" "event", "data" {}}])

^{:refer lua.nginx.pusher/start-listener-loop :added "4.0"
  :setup [(l/rt:restart)]}
(fact "performs the start listener loop"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-in p ["connection" "last_received"] (- (k/now-ms)
                                                  (* 70 1000)))
    (pusher/start-heartbeat p {:no-init true})
    (event-log/add-listener (. p history)
                            "test"
                            (fn [id data t]
                              (repl/notify [id data t])))
    (n/thread-spawn
     (fn []
       (pusher/start-listener-loop p (. p connection)))))
  => (contains-in
      ["test"
       {"event" "pusher:pong", "type" "event", "data" {}}
       number?]))

^{:refer lua.nginx.pusher/start-listener :added "4.0"
  :setup [(l/rt:restart)]}
(fact "starts the listener loop"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-prep
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-in p ["connection" "last_received"] (- (k/now-ms)
                                                  (* 70 1000)))
    (pusher/start-heartbeat p {})
    (pusher/start-listener  p)
    (event-log/add-listener (. p history)
                            "test"
                            (fn [id data t]
                              (repl/notify [id data t]))))
  => (contains-in
      ["test"
       {"event" "pusher:pong", "type" "event", "data" {}}
       number?]))

^{:refer lua.nginx.pusher/start-main-loop-init :added "4.0"
  :setup [(l/rt:restart)]}
(fact "initialises the connection, heartbeat"
  ^:hidden
  
  (notify/wait-on :lua
    (var p (pusher/make-pusher-raw
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-key p "active" true)
    (pusher/start-main-loop-init p {:wait-ping 10})
    (n/sleep 0.5)
    (repl/notify (event-log/get-tail (. p history)
                                     100)))
  => (contains-in
      [{"event" "pusher:pong", "type" "event", "data" {}}
       {"event" "thread:start-heartbeat"}
       {"event" "thread:start-listener"}]))

^{:refer lua.nginx.pusher/start-main-loop :added "4.0"
  :setup [(l/rt:restart)]}
(fact "starts the pusher main loop"
  ^:hidden
  
  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-raw
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-key p "active" true)
    (n/thread-spawn
     (fn []
       (pusher/start-main-loop p {:wait-ping 10})))
    (n/sleep 3)
    (repl/notify (event-log/get-tail (. p history)
                                     100)))
  => (contains-in
      [{"event" "pusher:pong", "type" "event", "data" {}}
       {"event" "thread:start-heartbeat"}
       {"event" "thread:start-listener"}]))


^{:refer lua.nginx.pusher/start-main :added "4.0"
  :setup [(l/rt:restart)]}
(fact "starts the main pusher thread"
  ^:hidden

  (notify/wait-on [:lua 5000]
    (var p (pusher/make-pusher-raw
            {:host "pusher.chain.so"
             :port 443
             :app-key "e9f5cc20074501ca7395"}))
    (k/set-key p "active" true)
    (pusher/start-main p {:wait-ping 10})
    (n/thread-spawn
     (fn []
       (n/sleep 3)
       (repl/notify (event-log/get-tail (. p history)
                                        100)))))
  => (contains-in
      [{"event" "pusher:pong" "type" "event", "data" {}}
       {"event" "thread:start-heartbeat"}
       {"event" "thread:start-listener"}
       {"event" "thread:start-main"}]))

^{:refer lua.nginx.pusher/pusher-activate :added "4.0"}
(fact "activates pusher flag")

^{:refer lua.nginx.pusher/pusher-deactivate :added "4.0"}
(fact "deactivates pusher flag")

^{:refer lua.nginx.pusher/pusher-add-listener :added "4.0"}
(fact "adds a listener to pusher")


(comment
  
  (notify/wait-on [:lua 5000]
    (:= q (pusher/make-pusher-raw
          {:host "pusher.chain.so"
           :port 443
           :app-key "e9f5cc20074501ca7395"}))
    (k/set-key q "active" true)
    (pusher/start-main q {})
    (n/thread-spawn
     (fn []
       (n/sleep 3)
       (repl/notify (event-log/get-tail (. q history)
                                        100))))))

(comment

  (!.lua
   (pusher/send-event p "subscribe" {:channel "blockchain_update_doge"}))
  
  (!.lua
   (event-log/get-tail (. p history)
                       2))
  
  (!.lua
   (event-log/get-count (. p history)))
  
  (!.lua
   (event-log/get-count (. q history)))
  
  (!.lua
   (event-log/get-tail (. p history)
                       5))
  
  (!.lua
   (event-log/get-tail (. q history)
                       2))
  
  (!.lua
   (var '[ok res] (pcall (fn []
                           (return 1))))
   [ok res])
  
  (comment
    
    
    
    ["resty.http" :as ngxhttp]
    (!.lua
     (:= ngxhttp (require "resty.http")))
    
    (!.lua
     (local conn (ngxhttp.new))
     (local '[res err]
            (. conn
               (request-uri "https://www.yahoo.com"
                            {:ssl_verify false})))
     res.status)

    (!.lua
     (local conn (ngxhttp.new))
     (local '[res err]
            (. conn
               (request-uri "https://www.yahoo.com"
                            {})))
     [res err])))
