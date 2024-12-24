(ns rt.redis-test
  (:use code.test)
  (:require [rt.redis :refer :all]
            [kmi.redis :as redis])
  (:refer-clojure :exclude [compile]))

^{:refer rt.redis/generate-script :added "4.0"}
(fact "generates a script given a pointer"
  ^:hidden
  
  (generate-script redis/cas-set)
  => (std.string/|
      "local function cas_set(key,old,new)"
      "  local val = redis.call('GET',key)"
      "  if (val == old) or ((val == false) and ((old == ''))) then"
      "    if new == '' then"
      "      redis.call('DEL',key)"
      "      return {'OK'}"
      "    else"
      "      local ret = redis.call('SET',key,new)"
      "      return {'OK'}"
      "    end"
      "  else"
      "    return {'NEW',val}"
      "  end"
      "end"
      ""
      "return cas_set(KEYS[1],ARGV[1],ARGV[2])"))

^{:refer rt.redis/test:req :added "4.0"}
(fact "does a request on a single test connection")

^{:refer rt.redis/test:invoke :added "4.0"}
(fact "does a script call on a single test connection")
