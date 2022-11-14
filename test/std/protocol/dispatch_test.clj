(ns std.protocol.dispatch-test
  (:use code.test)
  (:require [std.protocol.dispatch :refer :all]))

^{:refer std.protocol.dispatch/-create :added "3.0"}
(fact "creates an executor")
