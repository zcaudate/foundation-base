^{:no-test true}
(ns net.http
  (:require [net.http.client :as client]
	    [net.http.websocket :as ws]
            [std.object :as obj]
            [std.lib :as h])
  (:refer-clojure :exclude [get]))

(h/intern-in client/request
             client/get
             client/post
             client/put
             client/patch
             client/head
             client/delete
             client/remote
             client/mock-endpoint
             client/stream-lines
             client/http-client
             client/http-request
             client/+http-response-handler+
	     
	     ws/websocket
	     ws/send!
             ws/close!
             ws/abort!)

(defn event-stream
  "creates a data-stream for checking errors"
  {:added "4.0"}
  ([url & [opts]]
   (let [events (atom [])
         lines  (-> (client/get url
                                (h/merge-nested
                                 opts
                                 {:headers {"Accept" "text/event-stream"}
                                :as :lines}))
                    :body)
         foreach-fn (obj/query-instance lines ["forEach" :#])
         thread (future
                  (foreach-fn
                   lines
                   (h/fn:consumer [e]
                     (let [out (re-find #"data: (.*)" e)]
                       (if out
                         (swap! events conj (nth out 1)))))))]
     {:events events
      :lines lines
      :thread thread})))
