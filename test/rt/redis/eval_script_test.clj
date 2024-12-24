(ns rt.redis.eval-script-test
  (:use code.test)
  (:require [rt.redis.eval-script :refer :all]
            [rt.redis.client :as r]
            [lib.redis.bench :as bench]
            [kmi.redis :as redis]
            [std.concurrent :as cc]
            [std.lib :as h]
            [std.lang :as l]))

(fact:global
 {:setup [(bench/start-redis-array [17001])]
  :teardown [(bench/stop-redis-array [17001])]})

^{:refer rt.redis.eval-script/raw-compile-form :added "4.0"}
(fact "converts a ptr into a form"
  ^:hidden
  
  (raw-compile-form redis/call-fn)
  => '(return (kmi.redis/call-fn (unpack ARGV))))

^{:refer rt.redis.eval-script/raw-compile :added "4.0"}
(fact "compiles a function as body and sha"
  ^:hidden
  
  (raw-compile redis/call-fn)
  => {:body (std.string/|
             "local function call_fn(...)"
             "  return redis.call(...)"
             "end"
             ""
             "return call_fn(unpack(ARGV))")
      :sha "f47f4c2069fd6bf517d2da0d991a70fc959da1b4"}
  
  (raw-compile redis/key-export)
  => {:body (std.string/|
             "local key_getters = {"
             "  string={{'GET'},{}},"
             "  list={{'LRANGE'},{0,-1}},"
             "  hash={{'HGETALL'},{}},"
             "  set={{'MEMBERS'},{}},"
             "  zset={{'ZRANGE'},{0,-1,'WITHSCORES'}},"
             "  stream={{'XRANGE'},{'-','+'}}"
             "}"
             ""
             "local function key_export(key)"
             "  local t = redis.call('TYPE',key)['ok']"
             "  return {"
             "    t,"
             "    redis.call(unpack(key_getters[t][1]),key,unpack(key_getters[t][2]))"
             "  }"
             "end"
             ""
             "return key_export(KEYS[1])")
      :sha "7e1d53c2d8468b1ba3259a5712bdb86293eda1f1"})

^{:refer rt.redis.eval-script/raw-prep-in-fn :added "4.0"}
(fact "prepares the arguments for entry"
  ^:hidden
  
  (raw-prep-in-fn @redis/call-fn ["GET" "A:KEY"])
  => '[() ("GET" "A:KEY")]

  (raw-prep-in-fn @redis/scan-level ["A:KEY"])
  => '[("A:KEY") ()])

^{:refer rt.redis.eval-script/raw-prep-out-fn :added "4.0"}
(fact "prepares arguments out"
  ^:hidden
  
  (raw-prep-out-fn @redis/call-fn "hello")
  => "hello")

^{:refer rt.redis.eval-script/rt-install-fn :added "4.0"}
(fact "retries the function if not installed")

^{:refer rt.redis.eval-script/redis-invoke-sha :added "4.0"
  :setup    [(def -client- (r/client {:port 17001}))
             (cc/req -client- ["FLUSHDB"])
             (cc/req -client- ["SCRIPT" "FLUSH"])]
  :teardown [(h/stop -client-)]}
(fact "creates a sha call"

  (redis-invoke-sha -client-
                    redis/call-fn
                    ["PING"]
                    true)
  => (throws)

  
  (redis-invoke-sha -client-
                    redis/call-fn
                    ["PING"])
  => "PONG")
