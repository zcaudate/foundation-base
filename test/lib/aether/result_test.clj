(ns lib.aether.result-test
  (:use code.test)
  (:require [lib.aether.result :refer :all]
            [std.string :as str]))

^{:refer lib.aether.result/clojure-core? :added "3.0"}
(fact "checks if artifact represents clojure.core"

  (clojure-core? '[org.clojure/clojure "1.2.0"])
  => true)

^{:refer lib.aether.result/prioritise :added "3.0"}
(fact "gives the higher version library more priority"

  (prioritise '[[a "3.0"]
                [a "1.2"]
                [a "1.1"]]
              :coord)
  => '[[a/a "3.0"]])

^{:refer lib.aether.result/print-tree :added "3.0"}
(fact "prints a tree structure"

  (-> (print-tree '[[a "1.1"]
                    [[b "1.1"]
                     [[c "1.1"]
                      [d "1.1"]]]])
      (with-out-str)) ^:hidden
  => string?)

^{:refer lib.aether.result/dependency-graph :added "3.0"}
(fact "creates a dependency graph for the results")

^{:refer lib.aether.result/flatten-tree :added "3.0"}
(fact "converts a tree structure into a vector"

  (flatten-tree '[[a "1.1"]
                  [[b "1.1"]
                   [[c "1.1"]
                    [d "1.1"]]]])
  => '[[a "1.1"] [b "1.1"] [c "1.1"] [d "1.1"]])

^{:refer lib.aether.result/summary :added "3.0"}
(fact "creates a summary for the different types of results")

^{:refer lib.aether.result/return :added "3.0"}
(fact "returns a summary of install and deploy results")

^{:refer lib.aether.result/return-deps :added "3.0"}
(fact "returns a summary of resolve and collect results")

(comment
  (def res (lib.aether/install-artifact
            '[hara/hara.stuff "2.4.10"]
            {:artifacts [{:file "project.clj"
                          :extension "project"}
                         {:file "README.md"
                          :extension "readme"}]}))

  (def res (lib.aether/collect-dependencies
            '[hara.class.enum]))

  (lib.aether/collect-dependencies '[[im.chit/hara.class.enum "2.4.8"]
                                     [im.chit/hara.class "2.4.8"]]
                                   {:return :resolved
                                    :type :coord})

  (lib.aether/resolve-dependencies '[[im.chit/hara.class.enum "2.4.8"]
                                     [im.chit/hara.class "2.4.8"]]))
