(ns std.lang.model.spec-js.html-test
  (:use code.test)
  (:require [std.lang.model.spec-js.html :refer :all]
            [std.html :as html]))

^{:refer std.lang.model.spec-js.html/wrap-indent-inner :added "4.0"}
(fact "increase indentation in walk inner")

^{:refer std.lang.model.spec-js.html/wrap-indent-outer :added "4.0"}
(fact "decrese indentation in walk outer")

^{:refer std.lang.model.spec-js.html/prewalk-indent :added "4.0"}
(fact "preserves indentations")

^{:refer std.lang.model.spec-js.html/prepare-html :added "4.0"}
(fact "prepares the html, embedding any new scripts")

^{:refer std.lang.model.spec-js.html/emit-html :added "4.0"}
(fact "emits the html"
  ^:hidden
  
  (html/tree (emit-html [:a [:b [:c]]]
                        {} {}))
  => [:a [:b [:c]]])
