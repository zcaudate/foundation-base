(ns std.protocol.log-test
  (:use code.test)
  (:require [std.protocol.log :refer :all]))

^{:refer std.protocol.log/-create :added "3.0"}
(fact "creates a logger")
