(ns code.doc.link.reference-test
  (:use code.test)
  (:require [code.doc.link.reference :refer :all]))

^{:refer code.doc.link.reference/link-references :added "3.0"}
(fact "links references when working with specific source and test code")
