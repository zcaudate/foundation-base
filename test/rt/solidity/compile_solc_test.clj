(ns rt.solidity.compile-solc-test
  (:use code.test)
  (:require [rt.solidity.client :as client]
            [rt.solidity.compile-common :as compile-common]
            [rt.solidity.compile-solc :as compile]
            [rt.solidity.env-ganache :as env]
            [web3.lib.example-erc20 :as example-erc20]
            [std.lang :as l]
            [std.lib :as h]))

(l/script- :solidity
  {:config  {:mode :clean}
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

^{:refer rt.solidity.compile-solc/compile-base-emit :added "4.0"}
(fact "emits solidity given entries and interfaces"
  ^:hidden
  
  (compile/compile-base-emit
   [@test:hello]
   [])
  => [(std.string/|
       "function test__hello() pure internal returns(string memory) {"
       "  return \"HELLO WORLD\";"
       "}") ""]
  
  (compile/compile-base-emit
   [@example-erc20/get-account-balance]
   [@example-erc20/IERC20])
  => [(std.string/|
       "function get_account_balance(address token_address) external view returns(uint) {"
       "  IERC20 erc20 = IERC20(token_address);"
       "  return erc20.balanceOf(msg.sender);"
       "}")
      (std.string/|
       "interface IERC20 {"
       "  function transfer(address to,uint value) external returns(bool);"
       "  function balanceOf(address owner) external view returns(uint);"
       "}")])

^{:refer rt.solidity.compile-solc/compile-base-code :added "4.0"}
(fact "compiles base code"
  ^:hidden

  (compile/compile-base-code "function test__hello() pure public returns(string memory) {\n  return \"HELLO WORLD\";\n}"
                             {})
  => (std.string/|
      "// SPDX-License-Identifier: GPL-3.0"
      "pragma solidity >=0.7.0 <0.9.0;"
      ""
      "contract Temp {"
      "  function test__hello() pure public returns(string memory) {"
      "    return \"HELLO WORLD\";"
      "  }"
      "}"))

^{:refer rt.solidity.compile-solc/compile-ptr-prep-open-method :added "4.0"}
(fact "opens up a solidity method"
  ^:hidden
  ()
  
  )

^{:refer rt.solidity.compile-solc/compile-ptr-prep :added "4.0"}
(fact "exports a ptr"
  ^:hidden

  (compile/compile-ptr-prep test:hello)
  => [(std.string/|
       "function test__hello() pure public returns(string memory) {"
       "  return \"HELLO WORLD\";"
       "}")
      {:name "Test", :file "test.sol", :prefix ""}]

  (compile/compile-ptr-prep example-erc20/get-account-balance)
  => [(std.string/|
       "function get_account_balance(address token_address) external view returns(uint) {"
       "  IERC20 erc20 = IERC20(token_address);"
       "  return erc20.balanceOf(msg.sender);"
       "}")
      {:name "Test", :file "test.sol",
       :prefix (std.string/|
                "interface IERC20 {"
                "  function transfer(address to,uint value) external returns(bool);"
                "  function balanceOf(address owner) external view returns(uint);"
                "}")}])

^{:refer rt.solidity.compile-solc/compile-ptr-code :added "4.0"}
(fact "compiles the pointer to code"
  ^:hidden

  (compile/compile-ptr-code test:hello)
  => (std.string/|
      "// SPDX-License-Identifier: GPL-3.0"
      "pragma solidity >=0.7.0 <0.9.0;"
      ""
      "contract Test {"
      "  function test__hello() pure public returns(string memory) {"
      "    return \"HELLO WORLD\";"
      "  }"
      "}")

  (compile/compile-ptr-code example-erc20/get-account-balance)
  => (std.string/|
      "// SPDX-License-Identifier: GPL-3.0"
      "pragma solidity >=0.7.0 <0.9.0;"
      ""
      "interface IERC20 {"
      "  function transfer(address to,uint value) external returns(bool);"
      "  function balanceOf(address owner) external view returns(uint);"
      "}"
      ""
      "contract Test {"
      "  function get_account_balance(address token_address) external view returns(uint) {"
      "    IERC20 erc20 = IERC20(token_address);"
      "    return erc20.balanceOf(msg.sender);"
      "  }"
      "}"))

^{:refer rt.solidity.compile-solc/compile-module-prep :added "4.0"}
(fact "preps a namespace or map for emit"
  ^:hidden

  (compile-common/with:open-methods
   (compile/compile-module-prep nil))
  => ["function test__hello() pure public returns(string memory) {\n  return \"HELLO WORLD\";\n}"
      {:ns 'rt.solidity.compile-solc-test,
       :name "Test",
       :file "test.sol",
       :prefix ""}]

  (compile/compile-module-prep example-erc20/+default-contract+)
  => vector?)

^{:refer rt.solidity.compile-solc/compile-module-code :added "4.0"}
(fact "compiles the contract code"
  ^:hidden

  (compile-common/with:open-methods
   (compile/compile-module-code nil))
  => (std.string/|
      "// SPDX-License-Identifier: GPL-3.0"
      "pragma solidity >=0.7.0 <0.9.0;"
      ""
      "contract Test {"
      "  function test__hello() pure public returns(string memory) {"
      "    return \"HELLO WORLD\";"
      "  }"
      "}")

  (compile/compile-module-code example-erc20/+default-contract+)
  => string?)

^{:refer rt.solidity.compile-solc/compile-single-sol :added "4.0"}
(fact "compiles a solidity contract"
  ^:hidden
  
  (std.make/with:mock-compile
   (compile/compile-single-sol
    {:main   example-erc20/+default-contract+
     :root   ".build"
     :target "contracts"}))
  => (contains-in [".build/contracts/ExampleSample.sol"
                   string?]))

^{:refer rt.solidity.compile-solc/compile-all-sol :added "4.0"}
(fact "compiles multiple solidity contracts"
  ^:hidden
  
  (std.make/with:mock-compile
   (compile/compile-all-sol
    {:main   [example-erc20/+default-contract+]
     :root   ".build"
     :target "contracts"})))

^{:refer rt.solidity.compile-solc/compile-rt-prep :added "4.0"}
(fact "creates a runtime"
  ^:hidden
  
  (def +rt+
    (compile/compile-rt-prep))
  
  (h/stop +rt+))

^{:refer rt.solidity.compile-solc/compile-rt-eval :added "4.0"
  :setup    [(def +rt+
               (compile/compile-rt-prep))]
  :teardown [(h/stop +rt+)]}
(fact "evals form in the runtime"
  ^:hidden

  (compile/compile-rt-eval +rt+ '(+ 1 2 3))
  => 6

  (compile/compile-rt-eval +rt+ '(js.core/future
                                   (return (+ 1 2 3))))
  => 6)

^{:refer rt.solidity.compile-solc/compile-rt-abi :added "4.0"
  :setup    [(def +rt+
               (compile/compile-rt-prep))]
  :teardown [(h/stop +rt+)]}
(fact "compiles the contract-abi"
  ^:hidden

  (h/map-vals
   (fn [m]
     {:bytecode (get-in m ["evm" "bytecode" "object"])
      :abi      (get-in m ["abi"])})
   (compile/compile-rt-abi
    +rt+
    (compile-common/with:open-methods
     (compile/compile-module-code nil))
    "Test.sol"))
  => (contains-in
      {"Test" {:bytecode string?
               :abi
               [{"name" "test__hello"
                 "stateMutability" "pure"
                 "outputs"
                 [{"internalType" "string"
                   "name" ""
                   "type" "string"}]
                 "type" "function"
                 "inputs" []}]}})

  (h/map-vals
   (fn [m]
     {:bytecode (get-in m ["evm" "bytecode" "object"])
      :abi      (get-in m ["abi"])})
   (compile/compile-rt-abi
    +rt+
    (compile/compile-module-code example-erc20/+default-contract+)
    "ExampleSample.sol"))
  => (contains-in
      {"ExampleSample"
       {:bytecode string?,
        :abi vector?},
       "IERC20"
       {:bytecode "",
        :abi [{"name" "balanceOf",
               "stateMutability" "view",
               "outputs"
               [{"internalType" "uint256", "name" "", "type" "uint256"}],
               "type" "function",
               "inputs"
               [{"internalType" "address",
                 "name" "owner",
                 "type" "address"}]}
              {"name" "transfer",
               "stateMutability" "nonpayable",
               "outputs" [{"internalType" "bool", "name" "", "type" "bool"}],
               "type" "function",
               "inputs"
               [{"internalType" "address", "name" "to", "type" "address"}
                {"internalType" "uint256",
                 "name" "value",
                 "type" "uint256"}]}]}}))

^{:refer rt.solidity.compile-solc/compile-all-abi :added "4.0"}
(fact "compiles the abis"
  ^:hidden
  
  (std.make/with:mock-compile
   (compile/compile-all-abi
    {:main   [example-erc20/+default-contract+]
     :root   ".build"
     :target "interfaces"}))
  => (contains-in
      {:files 2
       :status :changed
       :written [[".build/interfaces/ExampleSample.json"
                  string?]]}))

^{:refer rt.solidity.compile-solc/create-base-entry :added "4.0"
  :setup    [(def +rt+
               (compile/compile-rt-prep))]
  :teardown [(h/stop +rt+)]}
(fact "creates either a pointer or module entry"
  ^:hidden
  
  (compile/create-base-entry
   +rt+
   compile/compile-ptr-prep
   test:hello
   [:entry (l/sym-full test:hello)]
   "Test")
  => map?

  (compile/create-base-entry
   +rt+
   compile/compile-module-prep
   web3.lib.example-erc20/+default-contract+
   [:module 'web3.lib.example-erc20 false]
   "ExampleSample")
  => map?)

^{:refer rt.solidity.compile-solc/create-pointer-entry :added "4.0"
  :setup    [(def +rt+
               (compile/compile-rt-prep))]
  :teardown [(h/stop +rt+)]}
(fact "creates a pointer entry"
  ^:hidden
  
  (into {} (compile/create-pointer-entry +rt+ test:hello))
  => (contains-in
      {:type :entry,
       :id 'rt.solidity.compile-solc-test/test:hello,
       :abi
       [{"name" "test__hello",
         "stateMutability" "pure",
         "outputs"
         [{"internalType" "string", "name" "", "type" "string"}],
         "type" "function",
         "inputs" []}],
       :sha "87ba3b447279a072128f5e502187a87277622024",
       :bytecode string?
       :code (std.string/|
              "// SPDX-License-Identifier: GPL-3.0"
              "pragma solidity >=0.7.0 <0.9.0;"
              ""
              "contract Test {"
              "  function test__hello() pure public returns(string memory) {"
              "    return \"HELLO WORLD\";"
              "  }"
              "}")})

  (into {} (compile/create-pointer-entry +rt+ example-erc20/get-account-balance))
  => (contains-in
      {:type :entry,
       :id 'web3.lib.example-erc20/get-account-balance,
       :abi
       [{"name" "get_account_balance",
         "stateMutability" "view",
         "outputs"
         [{"internalType" "uint256", "name" "", "type" "uint256"}],
         "type" "function",
         "inputs"
         [{"internalType" "address",
           "name" "token_address",
           "type" "address"}]}],
       :sha "6973de12220e048040db82d12ca6d0b58023b8a9",
       :bytecode string?
       :code (std.string/|
              "// SPDX-License-Identifier: GPL-3.0"
              "pragma solidity >=0.7.0 <0.9.0;"
              ""
              "interface IERC20 {"
              "  function transfer(address to,uint value) external returns(bool);"
              "  function balanceOf(address owner) external view returns(uint);"
              "}"
              ""
              "contract Test {"
              "  function get_account_balance(address token_address) external view returns(uint) {"
              "    IERC20 erc20 = IERC20(token_address);"
              "    return erc20.balanceOf(msg.sender);"
              "  }"
              "}")}))

^{:refer rt.solidity.compile-solc/create-module-entry :added "4.0"}
(fact "creates a compiled module contract entry"
  ^:hidden
  
  (into {} (compile/create-module-entry +rt+ example-erc20/+default-contract+))
  => (contains-in
      {:type :module,
       :id 'web3.lib.example-erc20,
       :abi vector?,
       :sha string?
       :bytecode string?,
       :code string?}))

^{:refer rt.solidity.compile-solc/create-file-entry :added "4.0"
  :setup    [(def +rt+
               (compile/compile-rt-prep))]
  :teardown [(h/stop +rt+)]}
(comment "creates a file entry"
  
  (compile/create-file-entry +rt+
                             {:name "USDT.sol"
                              :file "resources/assets/rt.solidity/example/USDT.sol"}))
