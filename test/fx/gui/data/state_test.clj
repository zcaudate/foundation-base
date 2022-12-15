(ns fx.gui.data.state-test
  (:use code.test)
  (:require [fx.gui.data.state :refer :all]))

^{:refer fx.gui.data.state/output-fn :added "3.0"}
(fact "returns a function for processing various outputs")