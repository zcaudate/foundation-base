(ns rt.solidity.compile-node-test
  (:use code.test)
  (:require [rt.solidity.client :as client]
            [rt.solidity.compile-common :as compile-common]
            [rt.solidity.compile-node :as compile-node]
            [rt.solidity.env-ganache :as env]
            [web3.lib.example-erc20 :as example-erc20]
            [std.lang :as l]
            [std.lib :as h]))

(l/script- :solidity
  {:runtime :web3
   :config  {:mode :clean}
   :require [[rt.solidity :as s]]})

(defn.sol ^{:- [:pure :internal]
            :static/returns [:string :memory]}
  test:hello []
  (return "HELLO WORLD"))

(fact:global
 {:setup    [(l/rt:restart)
             (env/start-ganache-server)]
  :teardown [(l/rt:stop)
             (env/stop-ganache-server)]})

^{:refer rt.solidity.compile-node/rt-get-id :added "4.0"}
(fact "gets the rt node id"
  ^:hidden
  
  (compile-node/rt-get-id)
  => string?)

^{:refer rt.solidity.compile-node/rt-get-contract-address :added "4.0"
  :setup [(s/rt:deploy-ptr test:hello)]}
(fact "gets the current contract address"
  ^:hidden
  
  (compile-node/rt-get-contract-address)
  => string?)

^{:refer rt.solidity.compile-node/rt-get-contract :added "4.0"
  :setup [(s/rt:deploy example-erc20/+default-contract+)]}
(fact "gets the current contract"
  ^:hidden
  
  (set (keys (compile-node/rt-get-contract)))
  => #{:abi :address :args :bytecode :code :id :sha :type} )

^{:refer rt.solidity.compile-node/rt-get-caller-address :added "4.0"}
(fact "gets the caller address"
  ^:hidden

  (compile-node/rt-get-caller-address)
  => "0x94E3361495BD110114AC0B6E35ED75E77E6A6CFA")

^{:refer rt.solidity.compile-node/rt-get-caller-private-key :added "4.0"}
(fact "gets the caller private-key"
  ^:hidden

  (compile-node/rt-get-caller-private-key)
  => "6f1313062db38875fb01ee52682cbf6a8420e92bfbc578c5d4fdc0a32c50266f")

^{:refer rt.solidity.compile-node/rt-get-node :added "4.0"}
(fact "gets the node runtime"
  ^:hidden

  (compile-node/rt-get-node)
  => map?)

^{:refer rt.solidity.compile-node/rt-get-address :added "4.0"}
(fact "gets the address of the signer"
  ^:hidden
  
  (compile-node/rt-get-address)
  => "0x94e3361495bD110114ac0b6e35Ed75E77E6a6cFA")

^{:refer rt.solidity.compile-node/rt:node-get-balance :added "4.0"}
(fact "gets the current balance"
  ^:hidden
  
  (compile-node/rt:node-get-balance)
  => number?

  (s/with:caller-address [(last env/+default-addresses+)]
    (compile-node/rt:node-get-balance))
  => number?)

^{:refer rt.solidity.compile-node/rt:node-ping :added "4.0"}
(fact "pings the node"
  ^:hidden

  (compile-node/rt:node-ping)
  => "pong")

^{:refer rt.solidity.compile-node/rt:node-eval :added "4.0"}
(fact "evaluates a form in the node runtime"
  ^:hidden

  (compile-node/rt:node-eval '(+ 1 2 3))
  => 6)

^{:refer rt.solidity.compile-node/rt:node-past-events :added "4.0"}
(fact "gets past events"

  #_#_#_(compile-node/rt:node-past-events)
  => vector?)

^{:refer rt.solidity.compile-node/with:measure :added "4.0"}
(fact "measures balance change before and after call")
