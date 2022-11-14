(ns jvm.tool-test
  (:use code.test)
  (:require [jvm.tool :refer :all]))

^{:refer jvm.tool/hotkey-set :added "4.0"}
(fact "set the hotkey function")
