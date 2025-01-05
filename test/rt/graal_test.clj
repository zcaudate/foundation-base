(ns rt.graal-test
  (:use code.test)
  (:require [rt.graal :refer :all]
            [std.lang :as  l]
            [std.lib :as h])
  (:import (org.graalvm.polyglot Context)))

(l/script- :js
  {:runtime :graal
   :require [[xt.lang.base-lib :as k]]})

(fact:global
 {:setup [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer rt.graal/eval-raw.test :adopt true :added "3.0"}
(fact "EVAL"

  (!.js
   (k/add 1 2))
  => 3
  
  (!.js (+ 1 2 3))
  => 6
  
  (!.js (+ "1" "2"))
  => 12

  
  (k/add 9 3)
  => 12

  (k/sub 3 9)
  => -6


  (comment
    (l/script- :python
      {:runtime :graal
       :require [[xt.lang.base-lib :as k]]})
    
    ;; PYTHON
    (str (h/p:rt-raw-eval (l/rt :python)
                          "globals()"))
    => string?
    
    (!.py 1 2 3 (+ 1 2 3))
    => 6
    
    (!.py {:a 1 :b 2})
    => {"a" 1, "b" 2}

    (!.py (:- :import json)
          (json.dumps true))
    => true
    
    (!.py
     (k/add 1 2))
    => 3))

^{:refer rt.graal/add-resource-path :added "3.0"
  :setup [(def +js+ (make-raw {:lang :js}))]}
(fact "adds resource path to context"

  (add-resource-path +js+ "assets"))

^{:refer rt.graal/add-system-path :added "3.0"
  :setup [(def +js+ (make-raw {:lang :js}))]}
(fact "adds system path to context"

  (add-system-path +js+ "."))

^{:refer rt.graal/make-raw :added "3.0"
  :setup [(def +js+ (make-raw {:lang :js}))]}
(fact "creates the base graal context"

  (str (.eval ^Context +js+ "js" "1 + 1"))
  => "2")

^{:refer rt.graal/close-raw :added "3.0"}
(fact "closes the base graal context")

^{:refer rt.graal/raw-lang :added "3.0"
  :setup [(def +js+ (make-raw {:lang :js}))]}
(fact "gets the language context"

  (raw-lang +js+)
  => :js)

^{:refer rt.graal/eval-raw :added "3.0"
  :setup [(def +js+ (make-raw {:lang :js}))]}
(fact "performs an exec expression"

  (str (eval-raw +js+ "1 + 1"))
  => "2")

^{:refer rt.graal/eval-graal :added "4.0"}
(fact "evals body in the runtime"

  (str (eval-graal (l/rt :js)
                   "1+1"))
  => "2")

^{:refer rt.graal/invoke-graal :added "4.0"}
(fact "invokes a pointer in the runtime"

  (invoke-graal (l/rt :js)
                k/sub
                [1 2])
  => -1)

^{:refer rt.graal/start-graal :added "3.0"}
(fact "starts the graal runtime")

^{:refer rt.graal/stop-graal :added "3.0"}
(fact "stops the graal runtime")

^{:refer rt.graal/rt-graal:create :added "4.0"}
(fact "creates a graal runtime"

  (h/-> (rt-graal:create {:lang :js})
        (h/start)
        (h/stop))
  => rt-graal?)

^{:refer rt.graal/rt-graal :added "3.0"}
(fact "creates and starts a graal runtime")

^{:refer rt.graal/rt-graal? :added "3.0"}
(fact "checks that object is a graal runtime")
