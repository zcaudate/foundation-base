(ns rt.solidity.compile-common-test
  (:use code.test)
  (:require [rt.solidity.compile-common :refer :all]))

^{:refer rt.solidity.compile-common/clear-compiled :added "4.0"}
(fact "clears all compiled entries")

^{:refer rt.solidity.compile-common/with:open-methods :added "4.0"}
(fact "forces the open methods flag")

^{:refer rt.solidity.compile-common/with:closed-methods :added "4.0"}
(fact "turns off the open methods flag")

^{:refer rt.solidity.compile-common/with:suppress-errors :added "4.0"}
(fact "suppresses printing of errors")

^{:refer rt.solidity.compile-common/with:stringify :added "4.0"}
(fact "stringifies the output")

^{:refer rt.solidity.compile-common/with:temp :added "4.0"}
(fact "deploy and run temp contract")

^{:refer rt.solidity.compile-common/with:clean :added "4.0"}
(fact "with clean flag")

^{:refer rt.solidity.compile-common/with:url :added "4.0"}
(fact "overrides api url")

^{:refer rt.solidity.compile-common/with:caller-address :added "4.0"}
(fact "overrides caller address")

^{:refer rt.solidity.compile-common/with:caller-private-key :added "4.0"}
(fact "overrides caller private key")

^{:refer rt.solidity.compile-common/with:caller-payment :added "4.0"}
(fact "overrides the caller payment")

^{:refer rt.solidity.compile-common/with:contract-address :added "4.0"}
(fact "overrides contract address")

^{:refer rt.solidity.compile-common/with:gas-limit :added "4.0"}
(fact "sets the gas limit")

^{:refer rt.solidity.compile-common/with:params :added "4.0"}
(fact "overrides all parameters")

^{:refer rt.solidity.compile-common/get-rt-settings :added "4.0"}
(fact "gets saves rt settings")

^{:refer rt.solidity.compile-common/set-rt-settings :added "4.0"}
(fact "sets rt settings")

^{:refer rt.solidity.compile-common/update-rt-settings :added "4.0"}
(fact "updates rt settings")

^{:refer rt.solidity.compile-common/get-caller-address :added "4.0"}
(fact "gets the caller address")

^{:refer rt.solidity.compile-common/get-caller-private-key :added "4.0"}
(fact "gets the caller private key")

^{:refer rt.solidity.compile-common/get-contract-address :added "4.0"}
(fact "gets the contract address")

^{:refer rt.solidity.compile-common/get-url :added "4.0"}
(fact "get sthe url for a rt")
