(ns std.make-test
  (:use code.test)
  (:require [std.make :as make]))

^{:refer std.make/gh:dwim-init :added "4.0"}
(fact "prepares the initial project commit")

^{:refer std.make/gh:dwim-push :added "4.0"}
(fact "prepares the project push")
