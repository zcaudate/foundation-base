(ns code.manage.unit.walk-test
  (:use code.test)
  (:require [code.manage.unit.walk :refer :all]))

^{:refer code.manage.unit.walk/walk-string :added "3.0"}
(fact "helper function for file manipulation for string output")

^{:refer code.manage.unit.walk/walk-file :added "3.0"}
(fact "helper function for file manipulation used by import and purge")
