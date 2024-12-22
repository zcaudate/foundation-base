(ns net.resp.connection-test
  (:use [code.test])
  (:require [net.resp.connection :refer :all :as conn]
            [net.resp.node :as node]
            [std.lib :as h]
            [std.concurrent :as cc])
  (:refer-clojure :exclude [read]))

(fact:global
 {:component
  {|node|    {:create   nil
              :setup    (fn [_] (node/start-node nil 4456))
              :teardown node/stop-node}
   |conn|    {:create   {}
              :setup    (fn [_] (conn/connection {:port 4456}))
              :teardown conn/connection:close}}})

^{:refer net.resp.connection/input-array :added "3.0"}
(fact "protects against wrong inputs"

  (seq (input-array [1 2 3]))
  => '("1" "2" "3"))

^{:refer net.resp.connection/pipeline :added "3.0"
  :use [|node| |conn|]}
(fact "retrieves the connection pipeline" ^:hidden

  (pipeline |conn|)
  => pipeline?)

^{:refer net.resp.connection/pipeline? :added "3.0"
  :use [|node| |conn|]}
(fact "checks if object is instance of pipeline")

^{:refer net.resp.connection/pipeline:read :added "3.0"
  :use [|node| |conn|]}
(fact "reads from the pipeline" ^:hidden

  (pipeline:read (pipeline |conn|))
  => [])

^{:refer net.resp.connection/pipeline:write :added "3.0"
  :use [|node| |conn|]}
(fact "sends a request tot the pipeline" ^:hidden

  (h/-> (pipeline |conn|)
        (pipeline:write ["PING"])
        (pipeline:write ["ECHO" "1"])
        (pipeline:write ["ECHO" "2"])
        (pipeline:write ["ECHO" "3"])
        (pipeline:read)
        (map h/string %))
  => ["PONG" "1" "2" "3"])

^{:refer net.resp.connection/connection:read :added "3.0"
  :use [|node| |conn|]}
(fact "reads from the connection" ^:hidden

  @(h/future {:timeout 100}
             (connection:read |conn|))
  => (throws))

^{:refer net.resp.connection/connection:write :added "3.0"
  :use [|node| |conn|]}
(fact "writes to the connection" ^:hidden

  (doto |conn|
    (connection:write ["PING"])
    (connection:write ["PING"]))

  (h/string (connection:read |conn|))
  => "PONG"

  (h/string (connection:read |conn|))
  => "PONG")

^{:refer net.resp.connection/connection:value :added "3.0"}
(fact "writes a string value to the connection")

^{:refer net.resp.connection/connection:throw :added "3.0"}
(fact "writes an exception to the connection")

^{:refer net.resp.connection/connection:close :added "3.0"
  :use [|node| |conn|]}
(fact "closes the connection" ^:hidden

  (connection:close |conn|)
  => connection?)

^{:refer net.resp.connection/connection:request-single :added "3.0"
  :use [|node| |conn|]}
(fact "requests the connection command" ^:hidden

  (-> (connection:request-single |conn| ["PING"])
      (h/string))
  => "PONG")

^{:refer net.resp.connection/connection:process-single :added "3.0"}
(fact "processes output data" ^:hidden

  (connection:process-single nil
                             [(.getBytes (str {:a 1}))]
                             {:format :edn
                              :deserialize true})
  => '({:a 1}))

^{:refer net.resp.connection/connection:request-bulk :added "3.0"
  :use [|node| |conn|]}
(fact "sends a multi command to the connection" ^:hidden

  (->> (connection:request-bulk |conn|
                                [["PING"]
                                 ["ECHO" "1"]
                                 ["ECHO" "2"]
                                 ["ECHO" "3"]])
       (map h/string))
  => ["PONG" "1" "2" "3"])

^{:refer net.resp.connection/connection:process-bulk :added "3.0"}
(fact "processes the returned responses")

^{:refer net.resp.connection/connection:transact-start :added "3.0"}
(fact "command to start transaction"

  (connection:transact-start nil)
  => ["MULTI"])

^{:refer net.resp.connection/connection:transact-end :added "3.0"}
(fact "command to end transaction"

  (connection:transact-end nil)
  => ["EXEC"])

^{:refer net.resp.connection/connection:transact-combine :added "3.0"}
(fact "not valid for rdp protocol")

^{:refer net.resp.connection/connection:info :added "3.0"
  :use [|node| |conn|]}
(fact "outputs connection info" ^:hidden

  (connection:info |conn|)
  =>  (contains-in {:host [string?], :port vector?}))

^{:refer net.resp.connection/connection:started? :added "3.0"
  :use [|node| |conn|]}
(fact "checks that connection has started" ^:hidden

  (connection:started? |conn|)
  => true)

^{:refer net.resp.connection/connection:stopped? :added "3.0"
  :use [|node| |conn|]}
(fact "checks that connection has stopped" ^:hidden

  (connection:stopped? |conn|)
  => false)

^{:refer net.resp.connection/connection:health :added "3.0"
  :use [|node| |conn|]}
(fact "checks on the health of the connection"

  (connection:health |conn|)
  => {:status :ok})

^{:refer net.resp.connection/connection-string :added "3.0"
  :use [|node| |conn|]}
(fact "returns the string" ^:hidden

  (connection-string |conn|)
  ;;  #redis.socket {:host ["localhost/127.0.0.1"], :port [4456 56798]}
  => string?)

^{:refer net.resp.connection/connection? :added "3.0"
  :use [|node| |conn|]}
(fact "checks if instance is type connection"

  (connection? |conn|)
  => true)

^{:refer net.resp.connection/connection :added "3.0"
  :use [|node|]}
(fact "creates a connection"

  (def |c| (connection {:host "localhost"
                        :port 4456}))
  (connection? |c|)
  => true

  (connection:close |c|))

^{:refer net.resp.connection/test:config :added "3.0"}
(fact "creates a container and gets config"

  (test:config)
  => map?)

^{:refer net.resp.connection/test:connection :added "3.0"}
(comment "creates a test connection"
  
  (test:connection)
  => connection?)

^{:refer net.resp.connection/with-test:connection :added "3.0"
  :style/indent 1}
(fact "creates an runs statements using a test connection"

  (with-connection [conn {:port 17001}]
    (cc/bulk conn
            (fn []
              (cc/req conn ["FLUSHDB"])
              (cc/req conn ["SET" "TEST:A" 1])
              (cc/req conn ["KEYS" "*"]))
            {}))
  => ["OK" "OK" ["TEST:A"]])

^{:refer net.resp.connection/with-connection :added "3.0"}
(fact "creates a temporary connection and runs code"

  (with-connection [conn  {:port 17001}]
    (cc/bulk conn
            (fn []
              (cc/req conn ["FLUSHDB"])
              (cc/req conn ["SET" "TEST:A" 1])
              (cc/req conn ["KEYS" "*"]))
            {}))
  => ["OK" "OK" ["TEST:A"]])

