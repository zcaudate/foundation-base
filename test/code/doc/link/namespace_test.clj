(ns code.doc.link.namespace-test
  (:use code.test)
  (:require [code.doc.link.namespace :refer :all]))

^{:refer code.doc.link.namespace/link-namespaces :added "3.0"}
(fact "links the current namespace to the elements")
