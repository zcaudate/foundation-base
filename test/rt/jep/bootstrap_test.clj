(ns rt.jep.bootstrap-test
  (:use code.test)
  (:require [rt.jep.bootstrap :refer :all]))

^{:refer rt.jep.bootstrap/bootstrap-code :added "3.0"}
(fact "creates the bootstrap code")

^{:refer rt.jep.bootstrap/jep-bootstrap :added "3.0"}
(fact "returns the jep runtime"

  (jep-bootstrap)
  => (any string?
          throws))

^{:refer rt.jep.bootstrap/init-paths :added "3.0"}
(fact "sets the path of the jep interpreter")
