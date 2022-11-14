(ns lib.redis-test
  (:use [code.test :exclude [run]])
  (:require [lib.redis.bench :as bench]
            [lib.redis.client :as r]
            [lib.redis.event :as event]
            [net.resp.connection :as conn]
            [net.resp.wire :as wire]
            [std.concurrent :as cc]
            [std.lib :as h])
  (:refer-clojure :exclude [read]))

(fact:global
 {:setup [(bench/start-redis-array [17000])]
  :component
  {|client|   {:create   (r/client:create {:port 17000})
               :setup    h/start
               :teardown h/stop}}
  :teardown [(bench/stop-redis-array [17000])]})

^{:refer std.lib/wrap-start :adopt true :added "3.0"
  :use [|client|]}
(fact "install setup steps for keys" ^:hidden

  (-> ((h/wrap-start identity [{:key :events  :start event/start:events-redis}])
       (assoc |client| :reset true :events event/+default+))
      ((comp event/events-string event/config:get)))
  => "h$tlgx")

^{:refer lib.redis/client-steps :added "3.0"}
(fact "clients steps for start up and shutdown")

^{:refer lib.redis/client-start :added "4.0"}
(fact "starts the client")

^{:refer lib.redis/client-create :added "3.0"}
(fact "creates a redis client"

  (r/client-create {:id "localhost"
                    :port 17000})
  => map?)


(comment

  (./import)
  (h/tracked:all [:redis]))
