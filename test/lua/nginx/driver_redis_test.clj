(ns lua.nginx.driver-redis-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [xt.lang.base-notify :as notify]
            [lib.redis.bench :as bench]))

(l/script- :lua
  {:runtime :basic
   :config  {:program :resty}
   :require [[xt.sys.conn-redis :as redis]
             [xt.lang.base-lib :as k]
             [lua.nginx.driver-redis :as lua-driver]]})

(fact:global
 {:setup    [(bench/start-redis-array [17000])
             (l/rt:restart)]
  :teardown [(l/rt:stop)
             (bench/stop-redis-array [17000])]})

^{:refer lua.nginx.driver-redis/connect-constructor :added "4.0"}
(fact "creates a xt.sys compatible constructor"
  ^:hidden
  
  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   (redis/exec conn "ping" [] {}))
  => "PONG")
