(ns net.resp.impl-test
  (:use code.test)
  (:require [net.resp.wire :as wire]
            [net.resp.connection :as conn]
            [net.resp.node :as node]
            [std.lib :as h]
            [std.concurrent.request :as req]
            [std.concurrent :as cc])
  (:refer-clojure :exclude [read]))

(fact:ns
 (:clone net.resp.connection-test))

^{:refer net.resp.wire/call :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "writes a command and waits for reply"

  (h/string (wire/call |conn| ["PING"]))
  => "PONG")

^{:refer net.resp.wire/read :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "reads from the socket"

  (wire/write |conn| ["PING"])
  (h/string (wire/read |conn|))
  => "PONG")

^{:refer net.resp.wire/write :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "writes without reading"

  (wire/write |conn| ["PING"])
  (wire/write |conn| ["PING"])

  (mapv h/string [(wire/read |conn|)
                  (wire/read |conn|)])
  => ["PONG" "PONG"])

^{:refer net.resp.wire/close :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "closes the connection"

  (wire/close |conn|)
  (wire/write |conn|)
  => (throws))

^{:refer std.concurrent.request/request-single :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "requests multiple commands"

  (-> (std.concurrent.request/request-single |conn| ["PING"])
      h/string)
  => "PONG")

^{:refer std.concurrent.request/request-bulk :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "requests multiple commands"

  (->> (std.concurrent.request/request-bulk |conn| [["PING"]
                                             ["PING"]])
       (map h/string))
  => ["PONG" "PONG"])

^{:refer std.concurrent.request/req:single :added "3.0"
  :use [|node| |conn|]  :adopt true}
(fact "Single execution"

  (req/req:single |conn| ["PING"] {})
  => "PONG"

  (req/req:single |conn| ["PING"] {:format :edn
                                   :deserialize true})
  => 'PONG

  (req/req:single |conn| ["PING"] {:chain [vector]})
  => ["PONG"])

^{:refer std.concurrent.request/req:single.meta :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "Single execution with meta on the command"

  (req/req:single |conn| ["PING"] nil)
  => "PONG"

  (req/req:single |conn| ["PING"] {:format :edn
                                   :deserialize true})
  => 'PONG

  (req/req:single |conn| ["PING"] {:chain [vector]})
  => ["PONG"])

^{:refer std.concurrent.request/req:single.async :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "Single Async Execution"

  @(req/req:single |conn| ["PING"] {:async true})
  => "PONG"

  @(req/req:single |conn| ["PING"] {:async true
                                    :format :edn
                                    :deserialize true})
  => 'PONG

  @(req/req:single |conn| ["PING"] {:async true
                                    :chain [vector]})
  => ["PONG"])

^{:refer std.concurrent.request/req:single.chain :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "Chained execution"

  (req/req:single |conn| ["PING"] {:chain   [read-string
                                             vector]})
  => '[PONG]

  @(req/req:single |conn| ["PING"] {:async true
                                    :chain   [read-string
                                              vector]})
  => '[PONG])

^{:refer std.concurrent.request/request-bulk :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "constructs a bulk request"

  (->> (std.concurrent.request/request-bulk |conn| [["ECHO" "1"]
                                             ["ECHO" "2"]
                                             ["ECHO" "3"]])
       (map h/string))
  => ["1" "2" "3"])

^{:refer std.concurrent.request/req :added "3.0"
  :use [|node| |conn|] :adopt true}
(fact "execution in bulked or normal mode" ^:hidden

  (cc/req |conn| ["ECHO" "OK"])
  => "OK"

  (cc/req |conn| ["ECHO" "OK"] {:string false})
  => "OK"

  (cc/req |conn| ["SET" "OK"] {:string false})
  => Throwable

  (cc/req |conn| ["ECHO" "OK"] {:async true})
  => h/future?)

^{:refer std.concurrent.request/bulk :added "3.0"
  :adopt true
  :use [|node| |conn|]}
(fact "runs multiple commands at once"

  (cc/bulk |conn|
          (fn []
            (cc/req |conn| ["ECHO" "OK"])
            (cc/req |conn| ["ECHO" "OK"])))
  => ["OK" "OK"]

  (cc/bulk |conn|
          (fn []
            (cc/req |conn| ["ECHO" "1"])
            (cc/req |conn| ["ECHO" "2"]))
          {:chain [(partial map h/parse-long)]})
  => [1 2])

^{:refer std.concurrent.request/transact :added "3.0"
  :adopt true
  :use [|node| |conn|]}
(fact "transact function for atomic operations"

  ;; Without Bulk
  (->> (cc/transact |conn|
                   (fn []
                     (cc/req |conn| ["PING"])
                     (cc/req |conn| ["PING"])))
       (mapv deref))
  => ["PONG" "PONG"]

  ;; With Bulk

  (-> (cc/bulk |conn|
              (fn []
                (cc/req |conn| ["PING"] {:async true})
                (cc/transact |conn|
                            (fn []
                              (cc/req |conn| ["PING"] {:async true})
                              (cc/req |conn| ["PING"] {:async true}))))))
  => ["PONG" "OK" "PONG" "PONG" ["PONG" "PONG"]]

  ;; Multiple in Bulk

  (cc/bulk |conn|
          (fn []
            (cc/transact |conn|
                        (fn []
                          (cc/req |conn| ["PING"] {:async true})
                          (cc/req |conn| ["PING"] {:async true})))
            (cc/transact |conn|
                        (fn []
                          (cc/req |conn| ["PING"] {:async true})
                          (cc/req |conn| ["PING"] {:async true})))))
  => ["OK" "PONG" "PONG" ["PONG" "PONG"] "OK" "PONG" "PONG" ["PONG" "PONG"]]

  ;; Bulk in Transaction

  (->> (cc/transact |conn|
                   (fn []
                     (cc/bulk |conn|
                             (fn []
                               (cc/req |conn| ["PING"] {:process vector})
                               (cc/req |conn| ["PING"])))
                     (cc/bulk |conn|
                             (fn []
                               (cc/req |conn| ["PING"] {:process vector})
                               (cc/req |conn| ["PING"])))))
       (map deref))
  =>  ["PONG" "PONG" "PONG" "PONG"]

  ;; Nested Transactions in Bulk

  (let [-p- (promise)]
    [(->> (cc/transact |conn|
                      (fn []
                        (cc/bulk |conn|
                                (fn []
                                  (deliver -p-
                                           (cc/transact |conn|
                                                       (fn []
                                                         (cc/req |conn| ["PING"])
                                                         (cc/req |conn| ["PING"]))))))
                        (cc/bulk |conn|
                                (fn []
                                  (cc/req |conn| ["PING"])
                                  (cc/req |conn| ["PING"])))))
          (map deref))
     (mapv deref @-p-)])
  => [["PONG" "PONG" "PONG" "PONG"]
      ["PONG" "PONG"]]

  ;; Nested Bulk in Transactions

  (cc/bulk |conn|
          (fn []
            (cc/transact |conn|
                        (fn []
                          (cc/bulk |conn|
                                  (fn []
                                    (cc/req |conn| ["PING"])
                                    (cc/req |conn| ["PING"])))))
            (cc/transact |conn|
                        (fn []
                          (cc/req |conn| ["PING"])
                          (cc/req |conn| ["PING"])))))
  =>  '["OK" ["PONG" "PONG"] ("PONG" "PONG")
        "OK" "PONG" "PONG" ("PONG" "PONG")])

^{:refer std.concurrent.request/bulk:transact :added "3.0"
  :adopt true
  :use [|node| |conn|]}
(fact "creates a single bulk transaction"

  (cc/bulk:transact |conn|
                   (fn []
                     (cc/req |conn| ["PING"])
                     (cc/req |conn| ["PING"])))
  =>  ["PONG" "PONG"])
