(ns std.protocol.deps-test
  (:use code.test)
  (:require [std.protocol.deps :refer :all]))

^{:refer std.protocol.deps/-create :added "3.0"}
(fact "creates a context")
