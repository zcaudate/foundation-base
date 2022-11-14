(ns rt.solidity-test
  (:use code.test)
  (:require [rt.solidity.client :as client]
            [rt.solidity.compile-common :as compile-common]
            [rt.solidity.env-ganache :as env]
            [std.lang :as l]
            [std.lib :as h]))

(l/script- :solidity
  {:runtime :web3
   :require [[rt.solidity :as s]]
   :static  {:contract ["Hello"]}})

(defn.sol ^{:- [:pure :public]
            :static/returns [:string :memory]}
  test:hello []
  (return "HELLO WORLD"))

(defn.sol ^{:- [:pure :public]
            :static/returns [:string :memory]}
  test:hello1 []
  (return "HELLO WORLD 1"))

(defn.sol ^{:- [:pure :public]
            :static/returns [:string :memory]}
  test:hello2 []
  (return "HELLO WORLD 2"))

(defn.sol ^{:- [:pure :private]
            :static/returns [:string :memory]}
  test:hello3 []
  (return "HELLO WORLD 3"))

(fact:global
 {:setup [(env/start-ganache-server)
          (l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer rt.solidity/exec-rt-web3 :added "4.0"}
(fact "helper function for executing a command via node")

^{:refer rt.solidity/rt:print :added "4.0"}
(comment "prints out the contract"
  ^:hidden
  
  (s/rt:print test:hello))

^{:refer rt.solidity/rt:deploy-ptr :added "4.0"}
(fact "deploys a ptr a contract"
  ^:hidden
  
  (s/rt:deploy-ptr test:hello)
  => (contains-in
      {"contractAddress" string?})
  
  (test:hello)
  => "HELLO WORLD"

  (do (s/rt:deploy-ptr test:hello1)
      (test:hello1))
  => "HELLO WORLD 1")

^{:refer rt.solidity/rt:deploy :added "4.0"}
(fact "deploys current namespace as contract"
  ^:hidden
  
  (s/rt:deploy)
  => (contains-in
      {"contractAddress" string?})

  (test:hello)
  => "HELLO WORLD"

  (test:hello1)
  => "HELLO WORLD 1"

  (test:hello2)
  => "HELLO WORLD 2"

  (do (s/with:open-methods
       (s/rt:deploy))
      (s/with:open-methods
       (test:hello3)))
  => "HELLO WORLD 3")

^{:refer rt.solidity/rt:contract :added "4.0"}
(fact "gets the contract"
  ^:hidden

  (into {} (s/rt:contract test:hello))
  => (contains-in
      {:type :entry,
       :id 'rt.solidity-test/test:hello,
       :abi
       [{"name" "test__hello",
         "stateMutability" "pure",
         "outputs"
         [{"internalType" "string", "name" "", "type" "string"}],
         "type" "function",
         "inputs" []}],
       :sha "87ba3b447279a072128f5e502187a87277622024",
       :bytecode string?,
       :code string?,
       :args []}))

^{:refer rt.solidity/rt:bytecode-size :added "4.0"}
(fact "gets the bytecode size"
  ^:hidden
  
  (s/rt:bytecode-size test:hello)
  => number?)

(comment
  (s/rt-get-contract-address)
  (s/rt-ge))
