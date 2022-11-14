(ns js.react.ext-box-test
  (:use code.test)
  (:require [js.react.ext-box :refer :all]))

^{:refer js.react.ext-box/makeBox :added "4.0"}
(fact "creates a box for react")

^{:refer js.react.ext-box/listenBox :added "4.0"}
(fact "listens to the box out")

^{:refer js.react.ext-box/useBox :added "4.0"}
(fact "getters and setters for the box")
