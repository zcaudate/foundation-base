(ns std.lang.model.spec-c-test
  (:use code.test)
  (:require [std.lang.model.spec-c :refer :all]))

^{:refer std.lang.model.spec-c/tf-define :added "4.0"}
(fact "not sure if this is needed (due to defmacro) but may be good for source to source"
  ^:hidden
  
  (tf-define '(define A 1))
  = '(:- "#define" A 1)

  (tf-define '(define A [a b] (+ a b)))
  => '(:- "#define" (:% A (quote (a b))) (+ a b)))
