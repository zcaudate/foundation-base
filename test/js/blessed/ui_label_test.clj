(ns js.blessed.ui-label-test
  (:use code.test)
  (:require [js.blessed.ui-label :refer :all]))

^{:refer js.blessed.ui-label/ToggleLabel :added "4.0"}
(fact "toggle label `red`/`green`")

^{:refer js.blessed.ui-label/ActionLabel :added "4.0"}
(fact "action label with color")

^{:refer js.blessed.ui-label/EntryLabel :added "4.0"}
(fact "entry label for records")
