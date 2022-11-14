(ns code.query.traverse-test
  (:use code.test)
  (:require [code.query.block :as nav]
            [code.query.traverse :refer :all]))

#_(defn source [pos]
    (-> pos :source nav/value))

^{:refer code.query.traverse/pattern-zip :added "3.0"}
(fact "creates a clojure.zip pattern")

^{:refer code.query.traverse/wrap-meta :added "3.0"}
(fact "helper for traversing meta tags")

^{:refer code.query.traverse/wrap-delete-next :added "3.0"}
(fact "wrapper for deleting next element in the zip")

^{:refer code.query.traverse/traverse-delete-form :added "3.0"}
(fact "traversing deletion form")

^{:refer code.query.traverse/traverse-delete-node :added "3.0"}
(fact "traversing deletion node")

^{:refer code.query.traverse/traverse-delete-level :added "3.0"}
(fact "traversing deletion level")

^{:refer code.query.traverse/prep-insert-pattern :added "3.0"}
(fact "helper for insertion")

^{:refer code.query.traverse/wrap-insert-next :added "3.0"}
(fact "wrapper for insertion")

^{:refer code.query.traverse/traverse-insert-form :added "3.0"}
(fact "traversing insertion form")

^{:refer code.query.traverse/traverse-insert-node :added "3.0"}
(fact "traversing insertion node")

^{:refer code.query.traverse/traverse-insert-level :added "3.0"}
(fact "traversing insertion level")

^{:refer code.query.traverse/wrap-cursor-next :added "3.0"}
(fact "wrapper for locating cursor")

^{:refer code.query.traverse/traverse-cursor-form :added "3.0"}
(fact "traversing cursor form")

^{:refer code.query.traverse/traverse-cursor-level :added "3.0"}
(fact "traversing cursor level")

^{:refer code.query.traverse/count-elements :added "3.0"}
(fact "counting elements")

^{:refer code.query.traverse/traverse :added "3.0"}
(fact "basic traverse functions"
  (source
   (traverse (nav/parse-string "^:a (+ () 2 3)")
             '(+ () 2 3)))
  => '(+ () 2 3)

  (source
   (traverse (nav/parse-string "^:a (hello)")
             '(hello)))
  => '(hello)
  ^:hidden
  (source
   (traverse (nav/parse-string "^:a (hello)")
             '(^:- hello)))
  => ()

  (source
   (traverse (nav/parse-string "(hello)")
             '(^:- hello)))
  => ()

  (source
   (traverse (nav/parse-string "((hello))")
             '((^:- hello))))
  => '(())

  ;; Insertions
  (source
   (traverse (nav/parse-string "()")
             '(^:+ hello)))
  => '(hello)

  (source
   (traverse (nav/parse-string "(())")
             '((^:+ hello))))
  => '((hello)))

^{:refer code.query.traverse/source :added "3.0"}
(fact "retrives the source of a traverse"

  (source
   (traverse (nav/parse-string "()")
             '(^:+ hello)))
  => '(hello))

(fact "more advanced traverse functions"
  (source
   (traverse (nav/parse-string "(defn hello)")
             '(defn ^{:? true :% true} symbol? ^:+ [])))
  => '(defn hello [])

  (source
   (traverse (nav/parse-string "(defn hello)")
             '(defn ^{:? true :% true :- true} symbol? ^:+ [])))
  => '(defn [])

  (source
   (traverse (nav/parse-string "(defn hello)")
             '(defn ^{:? true :% true :- true} symbol? | ^:+ [])))
  => []

  (source
   (traverse (nav/parse-string "(defn hello \"world\" {:a 1} [])")
             '(defn ^:% symbol?
                ^{:? true :% true :- true} string?
                ^{:? true :% true :- true} map?
                ^:% vector? & _)))
  => '(defn hello [])

  (source
   (traverse (nav/parse-string "(defn hello [] (+ 1 1))")
             '(defn _ _ (+ | 1 & _))))
  => 1

  (source
   (traverse (nav/parse-string "(defn hello [] (+ 1 1))")
             '(#{defn} | & _)))
  => 'hello

  (source
   (traverse (nav/parse-string "(fact \"hello world\")")
             '(fact | & _)))
  => "hello world")
