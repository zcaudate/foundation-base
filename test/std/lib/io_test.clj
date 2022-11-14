(ns std.lib.io-test
  (:use code.test)
  (:require [std.lib.io :refer :all]))

^{:refer std.lib.io/charset:default :added "3.0"}
(fact "returns the default charset"

  (charset:default)
  => "UTF-8")

^{:refer std.lib.io/charset:list :added "3.0"}
(comment "returns the list of available charset"

  (charset-list)
  => ("Big5" "Big5-HKSCS" ... "x-windows-iso2022jp"))

^{:refer std.lib.io/charset :added "3.0"}
(comment "constructs a charset object from a string"
  (charset "UTF-8")
  => java.nio.charset.Charset)

^{:refer std.lib.io/input-stream? :added "3.0"}
(fact "checks if object is an input-stream")

^{:refer std.lib.io/output-stream? :added "3.0"}
(fact "checks if object is an output-stream")

^{:refer std.lib.io/reader? :added "3.0"}
(fact "checks that object is a reader")

^{:refer std.lib.io/writer? :added "3.0"}
(fact "checks that object is a writer")
