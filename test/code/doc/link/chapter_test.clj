(ns code.doc.link.chapter-test
  (:use code.test)
  (:require [code.doc.link.chapter :refer :all]))

^{:refer code.doc.link.chapter/link-chapters :added "3.0"}
(fact "links each chapter to each of the elements")
