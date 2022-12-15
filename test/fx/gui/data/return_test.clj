(ns fx.gui.data.return-test
  (:use code.test)
  (:require [fx.gui.data.return :refer :all]))

^{:refer fx.gui.data.return/process-output :added "3.0"}
(fact "processes output bytes from process")