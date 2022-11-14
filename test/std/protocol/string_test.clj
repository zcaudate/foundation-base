(ns std.protocol.string-test
  (:use code.test)
  (:require [std.protocol.string :refer :all]))

^{:refer std.protocol.string/-from-string :added "3.0"}
(comment "common method for extending string-like objects")

^{:refer std.protocol.string/-path-separator :added "3.0"}
(fact "common method for finding the path separator for a given data type")
