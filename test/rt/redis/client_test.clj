(ns rt.redis.client-test
  (:use [code.test :exclude [run]])
  (:require [lib.redis.bench :as bench]
            [lib.redis.event :as event]
            [rt.redis.client :as r]
            [net.resp.connection :as conn]
            [net.resp.wire :as wire]
            [std.concurrent :as cc]
            [std.lib :as h])
  (:refer-clojure :exclude [read]))

(fact:global
 {:setup [(bench/start-redis-array [17001])]
  :component
  {|client|   {:create   (r/client:create {:port 17001})
               :setup    h/start
               :teardown h/stop}}
  :teardown [(bench/stop-redis-array [17001])]})

^{:refer std.lang.base.runtime-h/wrap-start :adopt true :added "3.0"
  :use [|client|]}
(fact "install setup steps for keys" ^:hidden

  (-> ((h/wrap-start identity [{:key :events  :start event/start:events-redis}])
       (assoc |client| :reset true :events event/+default+))
      ((comp event/events-string event/config:get)))
  => "h$tlgx")

^{:refer rt.redis.client/client-steps :added "3.0"}
(fact "clients steps for start up and shutdown")

^{:refer rt.redis.client/client-start :added "4.0"}
(fact "starts the client")

^{:refer rt.redis.client/client:create :added "3.0"}
(fact "creates a redis client"

  (r/client:create {:id "localhost"
                    :port 17001})
  => r/client?)

^{:refer rt.redis.client/client :added "3.0"
  :use [|client|]}
(fact "creates and starts a redis client" ^:hidden
  (cc/pool:with-resource [conn (:pool |client|)]

                         (->> (conn/connection:request-bulk conn [["SET" "A" "0"]
                                                                  ["INCR" "A"]
                                                                  ["INCR" "A"]
                                                                  ["INCR" "A"]])
                              (map h/string)))
  => ["OK" "1" "2" "3"])

^{:refer rt.redis.client/test:client :added "3.0"}
(fact "creates a test client on docker")

^{:refer rt.redis.client/invoke-ptr-redis :added "4.0"}
(fact "invokes the pointer in the redis context")

^{:refer rt.redis.client/client? :added "3.0"}
(fact "checks that instance is a client")

(comment

  (./import)
  (h/tracked:all [:redis]))
