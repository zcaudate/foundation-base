(ns code.doc.link.anchor-test
  (:use code.test)
  (:require [code.doc.link.anchor :refer :all]))

^{:refer code.doc.link.anchor/link-anchors-lu :added "3.0"}
(fact "adds anchor lookup table by name")

^{:refer code.doc.link.anchor/link-anchors :added "3.0"}
(fact "add anchors to the bundle"

  (-> (link-anchors {:anchors-lu
                     {"code.doc" {:by-tag {:a 1
                                           :b 2}}}}
                    "code.doc")
      :anchors)
  => {"code.doc" {:a 1, :b 2}})