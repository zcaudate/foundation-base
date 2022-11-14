(ns std.protocol.wire-test
  (:use code.test)
  (:require [std.protocol.wire :refer :all]))

^{:refer std.protocol.wire/-as-input :added "3.0"}
(fact "convert object to input")

^{:refer std.protocol.wire/-serialize-bytes :added "3.0"}
(fact "convert object to bytes")

^{:refer std.protocol.wire/-deserialize-bytes :added "3.0"}
(fact "convert bytes to object")
