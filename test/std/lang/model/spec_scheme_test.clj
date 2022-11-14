(ns std.lang.model.spec-scheme-test
  (:use code.test)
  (:require [std.lang.model.spec-scheme :refer :all]))

^{:refer std.lang.model.spec-scheme/emit-scheme :added "4.0"}
(fact "emits code into scheme schema")
