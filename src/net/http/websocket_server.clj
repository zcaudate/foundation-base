(ns net.http.websocket-server
  (:require [std.lib :as h]
            [std.lib.function :as fn]
            [std.concurrent :as cc]
            [net.http.websocket :as ws])
  (:import (hara.net.http WS)
           (java.net ServerSocket Socket)))

(defn run-basic-server
  "runs the basic wss server (unfinished)"
  {:added "4.0"}
  ([{:keys [port]} state ready on-message]
   (let [server    (ServerSocket. port)
         loop-fn   (fn []
                     (loop []
                       (when (not (.isClosed server))
                         (try
                           (let [socket     (.accept server)
                                 cleanup-fn (fn []
                                              (when-let [relay (:relay @state)]
                                                (h/stop relay)
                                                (h/stop (:attached relay))))
                                 _           (cleanup-fn)
                                 relay       (cc/relay {:type :socket
                                                        :port port
                                                        :attached socket
                                                        :options {:receive
                                                                  {:custom
                                                                   {:op :custom
                                                                    :handler (fn [raw _]
                                                                               (WS/readInputStream
                                                                                raw
                                                                                (fn/fn:consumer [s]
                                                                                  (on-message s))
                                                                                (fn/fn:consumer [_]
                                                                                  (.close socket)
                                                                                  (cleanup-fn))))}}}})
                                 _  (reset! state {:relay relay
                                                   :socket socket})
                                 _  (deliver ready true)])
                           (catch java.net.SocketException e))
                         (recur)))
                     (swap! state (fn [{:keys [relay ^Socket socket]}]
                                    (if relay (h/stop relay))
                                    (if socket (.close socket))
                                    nil)))
         thread   (cc/thread {:start true
                              :handler loop-fn})]
     {:instance server
      :thread thread})))

(defn handle-connection
  "handles the wss connection (unfinished)"
  {:added "4.0"}
  [^java.net.Socket socket {:keys [on-message
                                   on-close]}]
  (let [iraw (.getInputStream socket)
        oraw (.getOutputStream socket)
        _ (WS/doHandShakeToInitializeWebSocketConnection
           iraw
           oraw)
        _ (cc/relay {:type :socket
                     :attached socket})]))

(comment

  (defonce +server+ (java.net.ServerSocket.
                   (h/port:check-available 0)))

(def +f+ (future
           (let [socket (.accept +server+)]
             (handle-connection socket))))

(future-cancel +f+)
  @(cc/send +relay+ {:op :count
                     :direct true})


  (def +out+ (:output @(cc/send +relay+ {:op :string
                                         :direct true})))

  (h/stop +relay+)
  (h/stop (:attached +relay+))


  (.close (.getOutputStream (:attached +relay+)))
  (.write (.getOutputStream (:attached +relay+))
          (WebSocketProtocol/encode
           "Hello World"))

  (.write
   (:raw (:out @(:instance +relay+)))
   (WebSocketProtocol/encode
    "Hello World"))


  (def +ws+ @(ws/websocket (str "ws://localhost:"
                                (.getLocalPort +server+))
                           {:on-message (fn [& args]
                                          (h/prn args))
                            :on-close (fn [& args]
                                        (h/prn :closed))
                            :on-open  (fn [& args]
                                        (h/prn :opened))}))

  (ws/send! +ws+ (slurp "project.clj"))
  (ws/close! +ws+)



  )
