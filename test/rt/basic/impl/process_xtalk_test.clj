(ns rt.basic.impl.process-xtalk-test
  (:use code.test)
  (:require [rt.basic.impl.process-xtalk :refer :all]))

^{:refer rt.basic.impl.process-xtalk/read-output :added "4.0"}
(fact "read output for scheme")

^{:refer rt.basic.impl.process-xtalk/transform-form :added "4.0"}
(fact "transforms output from shell")
