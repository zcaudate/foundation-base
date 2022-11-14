(ns std.lang.base.emit-common-macro-test
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

^{:refer std.lang.base.emit-common/emit-macro.-> :adopt true :added "4.0"}
(fact "emit for macros structures"

  (emit-common-loop '(-> A
                        B
                        C)
                   +grammer+
                   {})
  => "(C (B A))"

  (emit-common '(-> A
                    B
                    C)
               +grammer+
               {})
  => "C(B(A))"
  

  (emit-common-loop '(-> (+ 1 2)
                        (F (+ 3 4))
                        (G (+ 5 6)))
                   +grammer+
                   {})
  => "(G (F (+ 1 2) (+ 3 4)) (+ 5 6))"

  (emit-common '(-> (+ 1 2)
                    (F (+ 3 4))
                    (G (+ 5 6)))
               +grammer+
               {})
  => "G(F(1 + 2,3 + 4),5 + 6)")

^{:refer std.lang.base.emit-common/emit-macro.->> :adopt true :added "4.0"}
(fact "emit for macros structures"

  (emit-common-loop '(->> A
                         B
                         C)
                   +grammer+
                   {})
  => "(C (B A))"

  (emit-common '(->> A
                     B
                     C)
               +grammer+
               {})
  => "C(B(A))"

  (emit-common-loop '(->> (+ 1 2)
                         (F (+ 3 4))
                         (G (+ 5 6)))
                   +grammer+
                   {})
  => "(G (+ 5 6) (F (+ 3 4) (+ 1 2)))"

  (emit-common '(->> (+ 1 2)
                     (F (+ 3 4))
                     (G (+ 5 6)))
               +grammer+
               {})
  => "G(5 + 6,F(3 + 4,1 + 2))")


^{:refer std.lang.base.emit-common/emit-macro.xor :adopt true :added "4.0"}
(fact "emit for macros structures"

  (emit-common-loop '(xor A B)
                   +grammer+
                   {})
  => "(:? A B (not B))"
  
  (emit-common '(xor A B)
               +grammer+
               {})
  => "A ? B : !B")
