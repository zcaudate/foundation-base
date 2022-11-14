(ns example.core-test
  (:use clojure.test)
  (:require [example.core :refer :all]))

^{:refer example.core/-foo- :added "3.0"}
(deftest -foo-test-
  (is (= 1 1)))
