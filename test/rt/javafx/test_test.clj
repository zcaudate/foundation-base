(ns rt.javafx.test-test
  (:use code.test)
  (:require [rt.javafx.test :refer :all]))

^{:refer rt.javafx.test/start-test :added "4.0"}
(fact "starts the test server")

^{:refer rt.javafx.test/stop-test :added "4.0"}
(fact "stops the test server")
