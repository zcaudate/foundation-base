(ns code.doc.link.stencil-test
  (:use code.test)
  (:require [code.doc.link.stencil :refer :all]))

^{:refer code.doc.link.stencil/transform-stencil :added "3.0"}
(fact "creates a link to the given tags")

^{:refer code.doc.link.stencil/link-stencil :added "3.0"}
(fact "creates links to all the other documents in the project")
