(ns code.doc.engine-test
  (:use code.test)
  (:require [code.doc.engine :refer :all]))

^{:refer code.doc.engine/wrap-hidden :added "3.0"}
(fact "helper function to not process elements with the `:hidden` tag")

^{:refer code.doc.engine/engine :added "3.0"}
(fact "dynamically loads the templating engine for publishing"

  (engine "winterfell"))
