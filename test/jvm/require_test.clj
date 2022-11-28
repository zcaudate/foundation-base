(ns jvm.require-test
  (:use code.test)
  (:require [jvm.require :refer :all]))

^{:refer jvm.require/force-require :added "4.0"}
(fact "a more flexible require, reloading namespaces if errored")
