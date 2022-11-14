(ns code.doc.theme.stark-test
  (:use code.test)
  (:require [code.doc.theme.stark :refer :all]))

^{:refer code.doc.theme.stark/render-top-level :added "3.0"}
(fact "renders the top-level (cover page) for the stark theme")

^{:refer code.doc.theme.stark/render-article :added "3.0"}
(fact "renders the individual page for the stark theme")

^{:refer code.doc.theme.stark/render-outline :added "3.0"}
(fact "renders the navigation outline for the stark theme")
