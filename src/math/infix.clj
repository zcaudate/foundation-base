(ns math.infix
  (:require [math.infix.core :as core])
  (:refer-clojure :exclude [= >]))

(defn infix-forms
  "helper function for infix macros"
  {:added "3.0"}
  ([exprs]
   (->> exprs (map core/resolve-alias) core/rewrite)))

;; Short alias for infix
(defmacro =
  "evaluates the infix expression
 
   (in/= 1 + 2 + 3 * 4)
   => 15
 
   (in/= 1 + (2 + 3) * 4)
   => 21"
  {:added "3.0"}
  ([& exprs]
   (infix-forms exprs)))

(defmacro >
  "creates the top level form
 
   (in/> 1 + (2 + 3) * 4)
   => '(+ 1 (* (+ 2 3) 4))"
  {:added "3.0"}
  ([& exprs]
   `(quote ~(infix-forms exprs))))
