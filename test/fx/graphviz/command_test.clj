(ns fx.graphviz.command-test
  (:use code.test)
  (:require [fx.graphviz.command :refer :all]))

^{:refer fx.graphviz.command/map-edges :added "3.0"}
(fact "helper function for mapping edges")

^{:refer fx.graphviz.command/graph->dot :added "3.0"}
(fact "converts a graph to dot notation")