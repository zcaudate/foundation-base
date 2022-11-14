(ns rt.solidity.script.util-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [rt.solidity.env-ganache :as env]))

(l/script- :solidity
  {:runtime :web3
   :require [[rt.solidity :as s]]})

(fact:global
 {:setup    [(l/rt:restart)
             (env/start-ganache-server)]
  :teardown [(l/rt:stop)
             (env/stop-ganache-server)]})

^{:refer rt.solidity.script.util/ut:str-comp :added "4.0"}
(fact "compares two strings together"
  ^:hidden

  (s/with:temp
    (s/ut:str-comp "123"
                   "456"))
  => false

  (s/with:temp
    (s/ut:str-comp "123"
                   "123"))
  => true)
