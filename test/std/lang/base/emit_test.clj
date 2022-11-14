(ns std.lang.base.emit-test
  (:use code.test)
  (:require [std.lang.base.emit :as emit :refer :all]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.grammer :as grammer]
            [std.lang.base.emit-prep-test :as prep]
            [std.lang.base.book-entry :as entry]
            [std.lib :as h]))

(def +reserved+
  (-> (grammer/build)
      (grammer/to-reserved)))

(def +grammer+
  (grammer/grammer :test +reserved+ helper/+default+))

^{:refer std.lang.base.emit/default-grammer :added "4.0"}
(fact "returns the default grammer"

  (emit/default-grammer)
  => map?)

^{:refer std.lang.base.emit/emit-main-loop :added "4.0"}
(fact "creates the raw emit"

  (emit/emit-main-loop '(not (+ 1 2 3))
                      +grammer+
                      {})
  => "!((+ 1 2 3))")

^{:refer std.lang.base.emit/emit-main :added "4.0"}
(fact "creates the raw emit with loop"

  (emit/emit-main '(not (+ 1 2 3))
                  +grammer+
                  {})
  => "!(1 + 2 + 3)")

^{:refer std.lang.base.emit/emit :added "4.0"}
(fact "emits form to output string"
  ^:hidden
  
  (emit/emit '(+ 1 2 3)
             @+test-grammer+
             nil
             {})
  => "1 + 2 + 3")

^{:refer std.lang.base.emit/with:emit :added "4.0"}
(fact "binds the top-level emit function to common/*emit-fn*"

  (emit/with:emit
   (common/*emit-fn* '(not (+ 1 2 3))
                     +grammer+
                     {}))
  => "!(1 + 2 + 3)")

^{:refer std.lang.base.emit/prep-options :added "4.0"}
(fact "prepares the options for processing"
  ^:hidden

  (prep-options {})
  => vector?)

^{:refer std.lang.base.emit/prep-form :added "4.0"}
(fact "prepares the form"
  ^:hidden
  
  (prep-form :raw '(+ 1 2 3) nil nil {})
  => '[(+ 1 2 3)]

  (prep-form :input '(+ @1 2 3) nil nil {})
  => '[(+ (!:eval 1) 2 3)]
  
  (prep-form :staging '(+ @1 2 3) nil nil {})
  => '[(+ (!:eval 1) 2 3) #{}])
