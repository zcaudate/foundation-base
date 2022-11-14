(ns std.lang.base.runtime-wrap-test
  (:use code.test)
  (:require [std.lang.base.runtime-wrap :refer :all]))

^{:refer std.lang.base.runtime-wrap/wrap-start :added "4.0"}
(fact "install setup steps for rt keys")

^{:refer std.lang.base.runtime-wrap/wrap-stop :added "4.0"}
(fact "install teardown steps for rt keys")
