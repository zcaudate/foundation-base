(ns clj-kondo.hooks.std-lib
  (:require [clj-kondo.hooks-api :as api]))

(defn import-vars-hook
  "A hook to handle (import-vars some.ns/foo some.ns/bar) by generating
   (declare foo bar). That way clj-kondo knows these vars exist."
  [call]
  ;; `call` is a node representing (potemkin.namespaces/import-vars ...)
  (let [[_ & imported] (api/child-nodes call)  ; skip macro symbol itself
        ;; The import-vars macro can take a list of qualified symbols, options, etc.
        ;; We'll do a simple approach: for each symbol, emit (declare symbol).
        syms (->> imported
                  (map api/sexpr)
                  (filter symbol?))]   ; keep only symbols
    (api/list-node
     (cons (api/token-node 'do)
           (for [s syms]
             (api/list-node [(api/token-node 'declare)
                             (api/token-node s)]))))))