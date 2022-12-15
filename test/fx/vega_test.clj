(ns fx.vega-test
  (:use code.test)
  (:require [fx.vega :refer :all]
            [std.lang :as l]))

(l/script- :js
  {:runtime :graal
   :config  {:dev {:print true}}})


(comment

  (def.js hello (+ 1 3))

  (def.js hello (+ 1 2))

  (def.js hello {:a 1 :b 2})

  (defn.js add [x y] (return (+ x y)))

  (add 1 2)

  (!.js (+ -/hello -/hello))
  (!.js (:- var varpp = 4))
  (!.js (var* varp 3)
        (var* varp 3))
  (!.js varpp)
  (!.js hello)
  (!.js
   1
   2))

^{:refer fx.vega/vega:download :added "4.0"}
(fact "downloads vega assets from unpkg")

^{:refer fx.vega/vega:create :added "3.0"}
(fact "creates a new vega graal context")

^{:refer fx.vega/get-vega :added "3.0"}
(fact "gets current vega context"

  (get-vega))

^{:refer fx.vega/render :added "3.0"}
(fact "renders a vega image"

  (render {:$schema "https://vega.github.io/schema/vega-lite/v4.json",
           :width 400, :height 300, :description "A simple bar chart.",
           :data {:values [{:a "A", :b 28} {:a "B", :b 55}
                           {:a "C", :b 43} {:a "D", :b 91} {:a "E", :b 81}
                           {:a "F", :b 53} {:a "G", :b 19} {:a "H", :b 87} {:a "I", :b 52}]},
           :mark "bar", :encoding {:x {:field "a", :type "ordinal"},
                                   :y {:field "b", :type "quantitative"}}}
          {:type :png :title nil})
  => bytes?)
