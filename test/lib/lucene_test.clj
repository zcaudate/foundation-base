(ns lib.lucene-test
  (:use code.test)
  (:require [lib.lucene :refer :all]))

^{:refer lib.lucene/lucene :added "3.0"}
(fact "constructs a lucene engine"

  (lucene {:store :memory
           :template {:album {:analyzer {:type :standard}
                              :type  {:id {:stored false}}}}})
  => lib.lucene.LuceneSearch)
