(ns lib.redis.extension-test
  (:use code.test)
  (:require [lib.redis.extension :refer :all]))

^{:refer lib.redis.extension/optional:set :added "4.0"}
(fact "optional parameters for `set` command"
  ^:hidden

  (optional:set {:expiry 10 :unit :ms :mode :exists} nil)
  => ["XX" "PX" 10])