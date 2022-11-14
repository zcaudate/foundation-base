(ns lib.redis.bench-test
  (:use code.test)
  (:require [lib.redis.bench :refer :all]))

^{:refer lib.redis.bench/all-redis-ports :added "4.0"}
(fact "gets all active redis ports")

^{:refer lib.redis.bench/config-to-args :added "4.0"}
(fact "convert config map to args"

  (config-to-args {:port 21001
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
(fact "starts a redis array")

^{:refer lib.redis.bench/stop-redis-array :added "4.0"}
(fact "stops a redis array")
