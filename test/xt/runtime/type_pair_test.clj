(ns xt.runtime.type-pair-test
  (:use code.test)
  (:require [xt.runtime.type-pair :refer :all]))

^{:refer xt.runtime.type-pair/pair-new :added "4.0"}
(fact "creates a pair new")

^{:refer xt.runtime.type-pair/pair :added "4.0"}
(fact "creates a pair")
