(ns lib.redis.bench-test
  (:use code.test)
  (:require [lib.redis.bench :as bench]
            [std.lib :as h]))

^{:refer lib.redis.bench/all-redis-ports :added "4.0"}
(fact "gets all active redis ports"
  ^:hidden
  
  (bench/all-redis-ports)
  => map?)

^{:refer lib.redis.bench/config-to-args :added "4.0"}
(fact "convert config map to args"
  ^:hidden
  
  (bench/config-to-args {:port 21001
                         :appendonly true})
  => "port 21001\nappendonly yes")

^{:refer lib.redis.bench/start-redis-server :added "4.0"}
(fact "starts the redis server in a given directory")

^{:refer lib.redis.bench/stop-redis-server :added "4.0"}
(fact "stop the redis server")

^{:refer lib.redis.bench/bench-start :added "4.0"}
(fact "starts the bench")

^{:refer lib.redis.bench/bench-stop :added "4.0"}
(fact "stops the bench")

^{:refer lib.redis.bench/start-redis-array :added "4.0"}
(fact "starts a redis array"
  ^:hidden
  
  (bench/start-redis-array [17001])
  => (contains-in
      [{:type :array,
        :port 17001,
        :root "test-bench/redis/17001",
        :process java.lang.Process
        :thread java.util.concurrent.CompletableFuture}]))

^{:refer lib.redis.bench/stop-redis-array :added "4.0"}
(fact "stops a redis array"
  ^:hidden
  
  (bench/stop-redis-array [17001])
  => (any (contains-in
           [{:type :array,
             :port 17001,
             :root "test-bench/redis/17001",
             :process java.lang.Process
             :thread java.util.concurrent.CompletableFuture}])
          [nil]))

(comment
  (bench/start-redis-array [17001])
  
  (h/sh "redis-server"
        {:wait false
         :inherit true}))
