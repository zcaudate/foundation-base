(ns math.infix.core-test
  (:use code.test)
  (:require [math.infix.core :refer :all]))

^{:refer math.infix.core/resolve-alias :added "3.0"}
(fact  "Attempt to resolve any aliases: if not found just return the original term")

^{:refer math.infix.core/rewrite :added "3.0"}
(fact "Recursively rewrites the infix-expr as a prefix expression, according to
   the operator precedence rules")
