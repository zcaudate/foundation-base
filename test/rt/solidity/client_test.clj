(ns rt.solidity.client-test
  (:use code.test)
  (:require [rt.solidity.client :as client]
            [rt.solidity.compile-common :as compile-common]
            [rt.solidity.compile-node :as compile-node]
            [std.lang :as l]
            [std.lib :as h]
            [rt.solidity.env-ganache :as env]))

(l/script- :solidity
  {:runtime :web3
   :config  {:mode :clean}
   :require [[rt.solidity :as s]]})

(defn.sol ^{:- [:pure :public]
            :static/returns [:string :memory]}
  test:hello []
  (return "HELLO WORLD"))

(fact:global
 {:setup    [(l/rt:restart)
             (env/start-ganache-server)]
  :teardown [(l/rt:stop)
             (env/stop-ganache-server)]})

^{:refer rt.solidity.client/check-node-connection :added "4.0"}
(fact "checks that the node connection is present"
  ^:hidden
  
  (client/check-node-connection
   (l/rt :solidity))
  => (comp not nil?))

^{:refer rt.solidity.client/contract-fn-name :added "4.0"}
(fact "gets the name of a pointer"
  ^:hidden
  
  (client/contract-fn-name test:hello)
  => "test__hello")

^{:refer rt.solidity.client/create-web3-node :added "4.0"}
(fact "creates the node runtime")

^{:refer rt.solidity.client/start-web3 :added "4.0"}
(fact "starts the solidity rt")

^{:refer rt.solidity.client/stop-web3 :added "4.0"}
(fact "stops the solidity rt")

^{:refer rt.solidity.client/raw-eval-web3 :added "4.0"}
(fact "disables raw-eval for solidity")

^{:refer rt.solidity.client/invoke-ptr-web3-check :added "4.0"
  :setup [(s/rt:deploy-ptr test:hello (l/rt :solidity))
          (def +contract-address+
            (compile-common/get-contract-address
             (compile-node/rt-get-id)))]}
(fact "checks that arguments are correct"
  ^:hidden
  
  (client/invoke-ptr-web3-check (compile-node/rt-get-contract)
                                "test__hello"
                                [1])
  => (throws)

  (client/invoke-ptr-web3-check (compile-node/rt-get-contract)
                                "test__hello"
                                [])
  => false)

^{:refer rt.solidity.client/invoke-ptr-web3-call :added "4.0"
  :setup [(s/rt:deploy-ptr test:hello (l/rt :solidity))
          (def +contract-address+
            (compile-common/get-contract-address
             (compile-node/rt-get-id)))]}
(fact "invokes a deployed method"
  ^:hidden

  (client/invoke-ptr-web3-call (l/rt :solidity)
                               test:hello
                               [])
  => "HELLO WORLD"

  (= (compile-common/get-contract-address
      (:id (:node (l/rt :solidity))))
     +contract-address+)
  => true)

^{:refer rt.solidity.client/invoke-ptr-web3 :added "4.0"
  :setup [(def +contract-address+
            (compile-common/get-contract-address
             (compile-node/rt-get-id)))]}
(fact "invokes the runtime, deploying the contract if not available"
  ^:hidden
  
  (client/invoke-ptr-web3 (l/rt :solidity)
                          test:hello
                          [])
  => "HELLO WORLD"

  (def +contract-address1+
    (compile-common/get-contract-address
     (compile-node/rt-get-id)))
  
  (not= +contract-address1+
        +contract-address+)
  => true
  
  (compile-common/with:clean [false]
    (client/invoke-ptr-web3 (l/rt :solidity)
                            test:hello
                            []))
  => "HELLO WORLD"

  (= +contract-address1+
     (compile-common/get-contract-address
      (compile-node/rt-get-id)))
  => true)

^{:refer rt.solidity.client/rt-web3-string :added "4.0"}
(fact "gets the runtime string")

^{:refer rt.solidity.client/rt-web3:create :added "4.0"}
(fact "creates a runtime")

^{:refer rt.solidity.client/rt-web3 :added "4.0"}
(fact "creates an starts a runtime")

(comment
  (rt.solidity.env-ganache/stop-ganache-server)
  (rt.solidity.env-ganache/rt:start-ganache-server)
  )
