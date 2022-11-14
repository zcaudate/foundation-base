(ns std.lib.context.pointer-test
  (:use code.test)
  (:require [std.lib.context.pointer :refer :all]))

^{:refer std.lib.context.pointer/pointer-deref :added "3.0"}
(fact "derefs a pointer")

^{:refer std.lib.context.pointer/pointer-default :added "4.0"}
(fact "function to get the pointer's context")

^{:refer std.lib.context.pointer/pointer? :added "3.0"}
(fact "checks that object is a pointer")

^{:refer std.lib.context.pointer/pointer :added "3.0"}
(fact "creates a pointer")
