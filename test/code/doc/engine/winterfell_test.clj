(ns code.doc.engine.winterfell-test
  (:use code.test)
  (:require [code.doc.engine.winterfell :refer :all]))

^{:refer code.doc.engine.winterfell/page-element
  :added "3.0"}
(fact "seed function for rendering a page element")

^{:refer code.doc.engine.winterfell/render-chapter :added "3.0"}
(fact "seed function for rendering a chapter element")

^{:refer code.doc.engine.winterfell/nav-element :added "3.0"}
(fact "seed function for rendering a navigation element")