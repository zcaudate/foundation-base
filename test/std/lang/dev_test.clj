(ns std.lang.dev-test
  (:use code.test)
  (:require [std.lang.dev :refer :all]))

^{:refer std.lang.dev/reload-specs :added "4.0"}
(fact "reloads the specs")
