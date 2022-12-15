(ns rt.javafx.harness-test
  (:use code.test)
  (:require [rt.javafx.harness :refer :all]))

^{:refer rt.javafx.harness/TestComponent :added "4.0"}
(fact "creates a test component")

^{:refer rt.javafx.harness/hasRoot :added "4.0"}
(fact "checks that there is a root div")

^{:refer rt.javafx.harness/teardownRoot :added "4.0"}
(fact "removes the root div")

^{:refer rt.javafx.harness/setupRoot :added "4.0"}
(fact "creates the root div")

^{:refer rt.javafx.harness/resetRoot :added "4.0"}
(fact "does teardown and setup")

^{:refer rt.javafx.harness/Harness :added "4.0"}
(fact "creates the root harness")

^{:refer rt.javafx.harness/attachRoot :added "4.0"}
(fact "attaches the root div")

^{:refer rt.javafx.harness/attachTestComponent :added "4.0"}
(fact "attaches the test component")