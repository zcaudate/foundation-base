(ns rt.solidity.grammer-test
  (:use code.test)
  (:require [rt.solidity.grammer :as g]
            [std.lib :as h]
            [std.lang :as l]))

^{:refer rt.solidity.grammer/sol-util-types :added "4.0"}
(fact "format sol types"
  ^:hidden
  
  (g/sol-util-types :hello)
  => "hello")

^{:refer rt.solidity.grammer/sol-map-key :added "4.0"}
(fact "formats sol map key"
  ^:hidden

  (g/sol-map-key :hello g/+grammer+ {})
  => "hello")

^{:refer rt.solidity.grammer/sol-keyword-fn :added "4.0"}
(fact "no typecast, straight forward print"
  ^:hidden
  
  (g/sol-keyword-fn '(:int hello) g/+grammer+ {})
  => "(:- :int hello)")

^{:refer rt.solidity.grammer/sol-def :added "4.0"}
(fact "creates a definition string"
  ^:hidden
  
  (g/sol-def '(def ^{:- [:uint]} a 1) g/+grammer+ {})
  => "uint a = 1;")

^{:refer rt.solidity.grammer/sol-fn-elements :added "4.0"}
(fact "creates elements for function"
  ^:hidden

  (g/sol-fn-elements 'hello '[:uint b :uint c]
                     '(return (+ b c))
                     g/+grammer+
                     {})
  => ["" "hello(uint b,uint c)" "{\n  return;\n  (+ b c);\n}"])

^{:refer rt.solidity.grammer/sol-emit-returns :added "4.0"}
(fact "emits returns"
  ^:hidden
  
  (g/sol-emit-returns
   '(returns :uint)
   g/+grammer+
   {})
  => "(returns (:- :uint))")

^{:refer rt.solidity.grammer/sol-defn :added "4.0"}
(fact "creates def contstructor form"
  ^:hidden

  (g/sol-defn '(defn ^{:- [:pure]
                       :static/returns :uint}
                 hello [:uint b :uint c]
                 (return (+ b c)))
              g/+grammer+
              {})
  => "function hello(uint b,uint c) pure (returns (:- :uint)) {\n  (return (+ b c));\n}")

^{:refer rt.solidity.grammer/sol-defconstructor :added "4.0"}
(fact "creates the constructor"
  ^:hidden
  
  (g/sol-defconstructor '(defconstructor
                           __init__ [:uint b :uint c]
                           (return (+ b c)))
                        g/+grammer+
                        {})
  => "constructor(uint b,uint c) {\n  (return (+ b c));\n}")

^{:refer rt.solidity.grammer/sol-defevent :added "4.0"}
(fact "creates an event"
  ^:hidden

  (g/sol-defevent '(defevent LOG
                     [:address :indexed sender]
                     [:string message])
                  g/+grammer+
                  {})
  => "event LOG(address indexed sender,string message);")

^{:refer rt.solidity.grammer/sol-tf-var-ext :added "4.0"}
(fact "transforms a var"
  ^:hidden
  
  (g/sol-tf-var-ext
   '(var '((:uint i)
           (:uint j))
         '(1 2)))
  => '(:= (:- (quote ((:uint i) (:uint j)))) (quote (1 2)))

  (g/sol-tf-var-ext
   '(var (:uint i) hello))
  => '(:= (:- :uint i) hello))

^{:refer rt.solidity.grammer/sol-tf-mapping :added "4.0"}
(fact "transforms mapping call"
  ^:hidden
  
  (g/sol-tf-mapping
   '(:mapping [:address :uint]))
  => '(mapping (:- :address "=>" :uint)))

^{:refer rt.solidity.grammer/sol-defstruct :added "4.0"}
(fact "transforms a defstruct call"
  ^:hidden
  
  (g/sol-defstruct
   '(defstruct HELLO
      [:uint a]
      [:uint b]))
  => '(:% (:- "struct" HELLO) (:- "{\n") (\| (do (var :uint a) (var :uint b))) (:- "\n}")))

^{:refer rt.solidity.grammer/sol-defaddress :added "4.0"}
(fact "transforms a defaddress call"
  ^:hidden
  
  (g/sol-defaddress
   '(defaddress ^{:- [:public]} HELLO owner))
  => '(:% (:- "address" "public" HELLO owner) \;))

^{:refer rt.solidity.grammer/sol-defenum :added "4.0"}
(fact "transforms a defenum call"
  ^:hidden
  
  (g/sol-defenum
   '(defenum Types [A B C]))
  => '(:% (:- "enum" Types (:- "{") (quote [A B C]) (:- "}"))))

^{:refer rt.solidity.grammer/sol-defmapping :added "4.0"}
(fact "transforms a mapping call"
  ^:hidden
  
  (g/sol-defmapping
   '(defmapping Types [A B]))
  => '(:% (:- (:mapping [A B]) Types) \;))

^{:refer rt.solidity.grammer/sol-definterface :added "4.0"}
(fact "transforms a definterface call"
  ^:hidden

  (g/sol-definterface
   '(definterface.sol IERC20
      [^{:- [:external]
         :static/returns :bool}
       transfer [:address to
                 :uint value]
       ^{:- [:external :view]
         :static/returns :uint}
       balanceOf [:address owner]])
   g/+grammer+
   {})
  => (std.string/|
      "interface IERC20 {"
      "  function transfer(address to,uint value) external (returns (:- :bool));"
      "  function balanceOf(address owner) external view (returns (:- :uint));"
      "}"))
