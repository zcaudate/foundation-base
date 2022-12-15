(ns fx.gui.search-test
  (:use code.test)
  (:require [fx.gui.search :refer :all]))

^{:refer fx.gui.search/find-child-by-id :added "3.0"}
(fact "find node given parent and id")

^{:refer fx.gui.search/find-by-id :added "3.0"}
(fact "find nested node given root and id")
