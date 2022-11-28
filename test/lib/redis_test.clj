^{:no-test true}
(ns lib.redis-test
  (:use [code.test :exclude [run]])
  (:require [lib.redis.bench :as bench]
            [lib.redis.event :as event]
            [lib.redis :as r]
            [net.resp.connection :as conn]
            [net.resp.wire :as wire]
            [std.concurrent :as cc]
            [std.lib :as h])
  (:refer-clojure :exclude [read]))

(fact:global
 {:setup [(bench/start-redis-array [17000])]
  :component
  {|client|   {:create   (r/client-create {:port 17000})
               :setup    h/start
               :teardown h/stop}}
  :teardown [(bench/stop-redis-array [17000])]})

^{:refer lib.redis/client-steps :added "3.0"}
(fact "clients steps for start up and shutdown")

^{:refer lib.redis/client-string :added "4.0"}
(fact "creates a cliet string")

^{:refer lib.redis/client-start :added "4.0"}
(fact "starts the client")

^{:refer lib.redis/client-create :added "3.0"}
(fact "creates a redis client"
  ^:hidden
  
  (r/client-create {:id "localhost"
                    :port 17000})
  => map?)

(comment

  (./import)
  (h/tracked:all [:redis]))
