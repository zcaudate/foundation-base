(ns std.lang.base.emit-common-op-test
  (:use code.test)
  (:require [std.lang.base.emit-common :as common :refer :all]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.grammer :as grammer]
            [std.lib :as h]))

(def +reserved+
  (-> (grammer/build)
      (grammer/to-reserved)))

(def +grammer+
  (grammer/grammer :test +reserved+ helper/+default+))

^{:refer std.lang.base.emit-common/emit-common-loop :adopt true :added "4.0"}
(fact "emit raw ops"

  [:discard]
  (emit-common-loop '(\- A "B" :C)
                   +grammer+
                   {})
  => ""

  [:free]
  (emit-common-loop '(:- A "B" :C)
                   +grammer+
                   {})
  => "A B C"
  
  [:comment]
  (emit-common-loop '(:# (+ 1 2) 2 3 4 5)
                   +grammer+
                   {})
  => "// (+ 1 2) 2 3 4 5"

  [:token]
  (emit-common-loop '(super)
                   +grammer+
                   {})
  => "super()"

  (emit-common-loop '(super)
                   (assoc-in +grammer+ [:reserved 'super :emit] :token)
                   {})
  => "super"
  
  [:pre]
  (emit-common-loop '(not A)
                   +grammer+
                   {})
  => "!A"
  
  (emit-common-loop '(not (+ 1 1))
                   +grammer+
                   {})
  => "!((+ 1 1))"

  [:post]
  (emit-common-loop '(factorial (+ 1 1))
                   (assoc-in +grammer+
                             [:reserved 'factorial]
                             '{:op :factorial, :symbol #{factorial}, :emit :post, :raw "!"})
                   {})
  => "((+ 1 1))!"

  [:infix]
  (emit-common-loop '(+ (+ 1 1))
                 +grammer+
                 {})
  => "((+ 1 1))"

  [:infix-]
  (emit-common-loop '(- (+ 1 1))
                 +grammer+
                 {})
  => "-((+ 1 1))"

  (emit-common-loop '(- (+ 1 1)
                     1)
                 +grammer+
                 {})
  => "((+ 1 1)) - 1"

  [:infix*]
  (emit-common-loop '(/ (+ 1 1))
                 +grammer+
                 {})
  => "1 / ((+ 1 1))"
  
  (emit-common-loop '(/ (+ 1 1)
                       1)
                 +grammer+
                 {})
  => "((+ 1 1)) / 1"

  [:infix-if]
  (emit-common-loop '(:? (+ 1 2) B)
                  +grammer+
                  {})
  => "((+ 1 2)) ? B : nil"
  
  (emit-common-loop '(:? (+ 1 2) B)
                    +grammer+
                    {})
  => "((+ 1 2)) ? B : nil"
   
  (emit-common-loop '(:? (+ 1 2) B C)
                 +grammer+
                 {})
  => "((+ 1 2)) ? B : C"
   
  (emit-common-loop '(:? (+ 1 2) B C)
                 +grammer+
                 {})
  => "((+ 1 2)) ? B : C"

  (emit-common-loop '(:? (+ 1 2) B :else C)
                 +grammer+
                 {})
  => "((+ 1 2)) ? B : C"

  (emit-common-loop '(:? (+ 1 2) B C D)
                 +grammer+
                 {})
  => (throws)

  (emit-common-loop '(:? (+ 1 2) B
                      C D
                      :else E)
                 +grammer+
                 {})
  => "((+ 1 2)) ? B : ((:? C D E))"

  [:bi]
  (emit-common-loop '(== (+ 1 2) B)
                  +grammer+
                  {})
  => "((+ 1 2)) == B"

  (emit-common-loop '(== (+ 1 2) B C)
                  +grammer+
                  {})
  => (throws)

  [:between]
  (emit-common-loop '(:to (+ 1 2) B)
                   (assoc-in +grammer+
                           [:reserved :to]
                           {:op :to :symbol #{:to}  :emit :between  :raw ".."})
                 {})
  => "((+ 1 2))..B"
  
  [:assign]
  (emit-common-loop '(:= A (+ 1 2))
                   +grammer+
                   {})
  => "A = ((+ 1 2))"

  [:invoke]
  (emit-common-loop '(:hello (+ 1 2))
                   (assoc-in +grammer+
                             [:reserved :hello]
                             {:op :hello :symbol #{:hello} :emit :invoke  :raw "HELLO"})
                   
                   {})
  => "HELLO((+ 1 2))"

  [:new]
  (emit-common-loop '(new Array (+ 1 2) 2 3)
                   +grammer+
                   
                   {})
  => "new Array((+ 1 2),2,3)"

  [:index]
  (emit-common-loop '(. A B)
                   +grammer+
                   {})
  => "A.B"

  (emit-common-loop '(. (+ 1 2) B)
                   +grammer+
                   {})
  => "((+ 1 2)).B"
  
  (emit-common-loop '(. A [B])
                   +grammer+
                   
                   {})
  => "A[B]"

  (emit-common-loop '(. A (hello "world"))
                   +grammer+
                   
                   {})
  => "A.hello(\"world\")"
  
  (emit-common-loop '(. (+ A B) [A])
                   +grammer+
                   
                   {})
  => "((+ A B))[A]"

  [:return]
  (emit-common-loop '(return (+ 1 2) A)
                   +grammer+
                   {})
  => "return (+ 1 2), A"
  
  [:decorate]
  (emit-common-loop '(!:decorate [opts] (+ 1 2 3))
                   +grammer+
                   {})
  => "(+ 1 2 3)"

  [:with-global]
  (emit-common-loop '(!:G HELLO)
                   (assoc-in +grammer+ [:token :symbol :global]
                             (fn [sym _ _]
                               (list '. 'GLOBAL [(str sym)])))
                   {})
  => "(. GLOBAL [\"HELLO\"])"
  
  [:throw]
  (emit-common-loop '(this)
                   +grammer+
                   {})
  => (throws))


^{:refer std.lang.base.emit-common/emit-common :adopt true :added "4.0"}
(fact "emit main raw"

  [:discard]
  (emit-common '(\- A "B" :C)
               +grammer+
               {})
  => ""

  [:free]
  (emit-common '(:- (+ 1 (+ 2 3)) "B" :C)
               +grammer+
               {})
  => "1 + (2 + 3) B C"
  
  [:comment]
  (emit-common '(:# (+ 1 2) 2 3 4 5)
               +grammer+
               {})
  => "// 1 + 2 2 3 4 5"

  
  [:token]
  (emit-common '(super)
               (assoc-in +grammer+ [:reserved 'super :emit] :token)
               {})
  => "super"
  
  [:pre]
  (emit-common '(not A)
               +grammer+
               {})
  => "!A"
  
  (emit-common '(not (+ 1 1))
               +grammer+
               {})
  => "!(1 + 1)"

  [:post]
  (emit-common '(factorial (+ 1 1))
               (assoc-in +grammer+
                         [:reserved 'factorial]
                         '{:op :factorial, :symbol #{factorial}, :emit :post, :raw "!"})
               {})
  => "(1 + 1)!"

  [:infix]
  (emit-common '(+ (+ 1 1))
               +grammer+
               {})
  => "(1 + 1)"

  [:infix-]
  (emit-common '(- (+ 1 1))
               +grammer+
               {})
  => "-(1 + 1)"

  (emit-common '(- (+ 1 1)
                   1)
               +grammer+
               {})
  => "(1 + 1) - 1"

  [:infix*]
  (emit-common '(/ (+ 1 1))
               +grammer+
               {})
  => "1 / (1 + 1)"
  
  (emit-common '(/ (+ 1 1)
                   1)
               +grammer+
               {})
  => "(1 + 1) / 1"

  [:infix-if]
  (emit-common '(:? (+ 1 2) B)
               +grammer+
               {})
  => "(1 + 2) ? B : nil"
  
  (emit-common '(:? (+ 1 2) B)
               +grammer+
               {})
  => "(1 + 2) ? B : nil"
  
  (emit-common '(:? (+ 1 2) B C)
               +grammer+
               {})
  => "(1 + 2) ? B : C"
  
  (emit-common '(:? (+ 1 2) B C)
               +grammer+
               {})
  => "(1 + 2) ? B : C"

  (emit-common '(:? (+ 1 2) B :else C)
               +grammer+
               {})
  => "(1 + 2) ? B : C"

  (emit-common '(:? (+ 1 2) B C D)
               +grammer+
               {})
  => (throws)

  (emit-common '(:? (+ 1 2) B
                    C D
                    :else E)
               +grammer+
               {})
  => "(1 + 2) ? B : (C ? D : E)"

  [:bi]
  (emit-common '(== (+ 1 2) B)
               +grammer+
               {})
  => "(1 + 2) == B"

  (emit-common '(== (+ 1 2) B C)
               +grammer+
               {})
  => (throws)

  [:between]
  (emit-common '(:to (+ 1 2) B)
               (assoc-in +grammer+
                         [:reserved :to]
                         {:op :to :symbol #{:to}  :emit :between  :raw ".."})
               {})
  => "(1 + 2)..B"
  
  [:assign]
  (emit-common '(:= A (+ 1 2))
               +grammer+
               {})
  => "A = (1 + 2)"

  [:invoke]
  (emit-common '(:hello (+ 1 2))
               (assoc-in +grammer+
                         [:reserved :hello]
                         {:op :hello :symbol #{:hello} :emit :invoke  :raw "HELLO"})
               
               {})
  => "HELLO(1 + 2)"

  [:new]
  (emit-common '(new Array (+ 1 2) 2 3)
               +grammer+
               
               {})
  => "new Array(1 + 2,2,3)"

  [:index]
  (emit-common '(. A B)
               +grammer+
               {})
  => "A.B"

  (emit-common '(. (+ 1 2) B)
               +grammer+
               {})
  => "(1 + 2).B"
  
  (emit-common '(. A [B])
               +grammer+
               
               {})
  => "A[B]"

  (emit-common '(. A (hello "world"))
               +grammer+
               
               {})
  => "A.hello(\"world\")"
  
  (emit-common '(. (+ A B) [A])
               +grammer+
               
               {})
  => "(A + B)[A]"
  
  [:return]
  (emit-common '(return (+ 1 2) A)
               +grammer+
               {})
  => "return 1 + 2, A"

  [:decorate]
  (emit-common '(!:decorate [opts] (+ 1 2 3))
               +grammer+
               {})
  => "1 + 2 + 3"

  [:with-global]
  (emit-common '(!:G HELLO)
               (assoc-in +grammer+ [:token :symbol :global]
                         (fn [sym _ _]
                           (list '. 'GLOBAL [(str sym)])))
               {})
  => "GLOBAL[\"HELLO\"]"
  
  
  [:throw]
  (emit-common '(this)
               +grammer+
               {})
  => (throws))
