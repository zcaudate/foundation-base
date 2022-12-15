(ns fx.uniorg-test
  (:use code.test)
  (:require [fx.uniorg :refer :all]))

^{:refer fx.uniorg/uniorg:create :added "4.0"}
(fact "creates a uniorg instance")

^{:refer fx.uniorg/get-uniorg :added "4.0"}
(fact "gets global uniorg object")

^{:refer fx.uniorg/org-extract-ast :added "4.0"}
(fact "extracts the org ast")

^{:refer fx.uniorg/org-extract-readme :added "4.0"}
(fact "extracts the org readme")
