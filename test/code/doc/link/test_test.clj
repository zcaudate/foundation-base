(ns code.doc.link.test-test
  (:use code.test)
  (:require [code.doc.link.test :refer :all]))

^{:refer code.doc.link.test/failed-tests :added "3.0"}
(fact "creates a link to all the failed tests in the project")

^{:refer code.doc.link.test/link-tests :added "3.0"}
(fact "creates a link to all the passed tests in the project")
