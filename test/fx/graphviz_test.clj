(ns fx.graphviz-test
  (:use code.test)
  (:require [fx.graphviz :refer :all]
            [std.string :as str]
            [std.lib :as h]))

^{:refer fx.graphviz/graphviz:create :added "3.0"}
(fact "creates a graphviz instance")

^{:refer fx.graphviz/get-graphviz :added "3.0"}
(fact "gets the current graphviz instance")

^{:refer fx.graphviz/generate-script :added "4.0"}
(fact "creates the graphviz dot file")

^{:refer fx.graphviz/graph :added "3.0"}
(fact "graph a set of points"
  ^:hidden

  (def html-node {:id "html" :color "blue"
                  :label [:TABLE {:BORDER 0}
                          [:TR [:TD "hic"]
                           [:TD {:BORDER 1} "cup"]]]})
  
  (def nodes [:a :b :c :d :e html-node])
  
  (def edges [[:a :b] [:a :c]
              [:c :d] [:d :e]
              [:a :c {:label "another" :style :dashed}]
              [:a :html]])

  (comment
    (-> (graph {:nodes nodes
                :edges edges
                :style {:node {:shape :box}
                        :node->id (fn [n] (if (keyword? n) (name n) (:id n)))
                        :node->descriptor (fn [n] (when-not (keyword? n) n))}}
               {:title nil})
        :dot-str)
    => (std.string/|
        "graph {"
        ""
        "graph[dpi=100, rankdir=TP, fontsize=12]"
        "node[shape=box]"
        "\"a\""
        "\"b\""
        "\"c\""
        "\"d\""
        "\"e\""
        "\"html\"[id=\"html\", color=\"blue\", label=<<TABLE BORDER=\"0\">"
        "  <TR>"
        "    <TD>hic</TD>"
        "    <TD BORDER=\"1\">cup</TD>"
        "  </TR>"
        "</TABLE>>]"
        ""
        "\"a\" -- \"b\""
        "\"a\" -- \"c\""
        "\"c\" -- \"d\""
        "\"d\" -- \"e\""
        "\"a\" -- \"c\"[label=\"another\", style=dashed]"
        "\"a\" -- \"html\""
        "}"
        "")))

(comment

  (./import)

  (h/string (:output))

  (def dot (graph->dot nodes edges {:node {:shape :box}
                                    :node->id (fn [n] (if (keyword? n) (name n) (:id n)))
                                    :node->descriptor (fn [n] (when-not (keyword? n) n))}))

  (def -s- (dot->svg dot)))
