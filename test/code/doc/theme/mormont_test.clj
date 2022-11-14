(ns code.doc.theme.mormont-test
  (:use code.test)
  (:require [code.doc.theme.mormont :refer :all]))

^{:refer code.doc.theme.mormont/render-top-level :added "3.0"}
(fact "renders the top-level (cover page) for the mormont theme")

^{:refer code.doc.theme.mormont/render-article :added "3.0"}
(fact "renders the individual page for the mormont theme")

^{:refer code.doc.theme.mormont/render-navigation :added "3.0"}
(fact "renders the navigation outline for the mormont theme")
