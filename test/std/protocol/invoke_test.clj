(ns std.protocol.invoke-test
  (:use code.test)
  (:require [std.protocol.invoke :refer :all]
            [std.lib.invoke]))

^{:refer std.protocol.invoke/-invoke-intern :added "3.0"}
(fact "extendable function for loading invoke form constructors"

  (-invoke-intern :fn '-hello- nil '([x] x)) ^:hidden
  => '(def -hello- (clojure.core/fn -hello- [x] x)))

^{:refer std.protocol.invoke/-invoke-package :added "3.0"}
(fact "extendable function for loading invoke-intern types")

^{:refer std.protocol.invoke/-fn-body :added "3.0"}
(fact "multimethod for defining anonymous function body"

  (-fn-body :clojure '([x] x))
  => '(clojure.core/fn [x] x))

^{:refer std.protocol.invoke/-fn-package :added "3.0"}
(fact "extendable function for loading fn-body types")
