(ns code.doc.render.structure-test
  (:use code.test)
  (:require [code.doc.render.structure :refer :all]))

^{:refer code.doc.render.structure/inclusive :added "3.0"}
(fact "checks is a section is within another"

  (inclusive :article :chapter)
  => true

  (inclusive :chapter :section)
  => true

  (inclusive :section :chapter)
  => false)

^{:refer code.doc.render.structure/separate :added "3.0"}
(fact "separates elements into various structures")

^{:refer code.doc.render.structure/containify :added "3.0"}
(fact "puts a flat element structure into containers")

^{:refer code.doc.render.structure/mapify-unit :added "3.0"}
(fact "helper class for mapify")

^{:refer code.doc.render.structure/mapify :added "3.0"}
(fact "creates the hierarchical structure for a flat list of elements")

^{:refer code.doc.render.structure/structure :added "3.0"}
(fact "creates a structure for the article and its elements")
