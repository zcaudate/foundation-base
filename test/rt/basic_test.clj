(ns rt.basic-test
  (:use code.test)
  (:require [rt.basic :refer :all]))

^{:refer rt.basic/clean-relay :added "4.0"}
(fact "cleans the relay on the server")
