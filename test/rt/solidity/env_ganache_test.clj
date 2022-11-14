(ns rt.solidity.env-ganache-test
  (:use code.test)
  (:require [rt.solidity.env-ganache :refer :all]))

^{:refer rt.solidity.env-ganache/start-ganache-server :added "4.0"}
(fact "starts the ganache service")

^{:refer rt.solidity.env-ganache/stop-ganache-server :added "4.0"}
(fact "stops the ganache service")
