(ns rt.basic.type-websocket-test
  (:use code.test)
  (:require [rt.basic.type-websocket :refer :all]))

^{:refer rt.basic.type-websocket/start-websocket :added "4.0"}
(fact "starts bench and server for websocket runtime")

^{:refer rt.basic.type-websocket/rt-websocket:create :added "4.0"}
(fact "creates a websocket runtime")

^{:refer rt.basic.type-websocket/rt-websocket :added "4.0"}
(fact "creates and start a websocket runtime")
