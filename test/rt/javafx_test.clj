(ns rt.javafx-test
  (:use code.test)
  (:require [std.lang :as l]
            [rt.javafx :as fx]))

(l/script- :js
  {:runtime :javafx
   :require [[xt.lang.base-lib :as k]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer rt.javafx/start-javafx :added "4.0"}
(fact "starts the javafx")

^{:refer rt.javafx/stop-javafx :added "4.0"}
(fact "stops the javafx")

^{:refer rt.javafx/raw-eval-javafx :added "4.0"}
(fact "evaluates an expression in the runtime"
  ^:hidden
  
  (fx/raw-eval-javafx (l/rt :js)
                      "1 + 1")
  => "2")

^{:refer rt.javafx/invoke-ptr-javafx :added "4.0"}
(fact "evaluates an expression in the runtime"
  ^:hidden

  (fx/invoke-ptr-javafx (l/rt :js)
                        k/add 
                        [1 2])
  => 3)

^{:refer rt.javafx/rt-javafx:create :added "4.0"}
(fact "creates the javafx")

^{:refer rt.javafx/rt-javafx :added "4.0"}
(fact "creates and starts the javafx")

(comment

  (l/script- :js
    {;:runtime :graal
     ;:config  {:dev {:print true}}
     :require []})
  
  (into {}(lang/rt :js))
   
  (def.js hello (+ 1 3))
  
  (def.js hello (+ 1 2))
  
  (def.js hello {:a 1 :b 2})

  (defn.js add [x y] (return (+ x y)))

  (add 1 2)

  (!.js (+ -/hello -/hello))
  (!.js (:- var varpp = 4))
  (!.js (var varp 3)
        (var varp 3))
  (!.js varpp)
  (!.js hello)
  (!.js
   1
   2))
