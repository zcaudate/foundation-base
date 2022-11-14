(ns script.yaml-test
  (:use code.test)
  (:require [script.yaml :refer :all])
  (:refer-clojure :exclude [read]))

^{:refer script.yaml/make-dumper-options :added "3.0"}
(fact "creates encoding options")

^{:refer script.yaml/make-yaml :added "3.0"}
(fact "Make a yaml encoder/decoder with some given options.")

^{:refer script.yaml/mark :added "3.0"}
(fact  "Mark some data with start and end positions.")

^{:refer script.yaml/marked? :added "3.0"}
(fact "Let us know whether this piece of data is marked with source positions.")

^{:refer script.yaml/unmark :added "3.0"}
(fact "Strip the source information from this piece of data, if it exists.")

^{:refer script.yaml/write :added "3.0"}
(fact "writes map to yaml")

^{:refer script.yaml/read :added "3.0"}
(fact "reads map from yaml")
