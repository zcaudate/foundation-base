(ns std.lang.base.emit-fn-test
  (:use code.test)
  (:require [std.lang.base.emit-fn :refer :all]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.grammer :as grammer]
            [std.lib :as h]))

(def +reserved+
  (-> (grammer/build)
      (grammer/to-reserved)))

(def +grammer+
  (grammer/grammer :test +reserved+ helper/+default+))

^{:refer std.lang.base.emit-fn/emit-input-default :added "3.0"}
(fact "create input arg strings"
  ^:hidden

  (-> (helper/emit-typed-args '(:int i := 9, :const :int j := 10)
                              +grammer+)
      first
      (emit-input-default "=" {} {}))
  => "int i = 9")

^{:refer std.lang.base.emit-fn/emit-hint-type :added "4.0"}
(fact "emits the return type")

^{:refer std.lang.base.emit-fn/emit-def-type :added "4.0"}
(fact "emits the def type")

^{:refer std.lang.base.emit-fn/emit-fn-type :added "3.0"}
(fact "returns the function type"
  ^:hidden
  
  (emit-fn-type (with-meta 'hello {:- [:char]}) nil
                +grammer+
                {})
  => "char"

  (emit-fn-type nil "function"
                +grammer+
                {})
  => "function")

^{:refer std.lang.base.emit-fn/emit-fn-block :added "4.0"}
(fact "gets the block options for a given grammer"
  ^:hidden
  
  (emit-fn-block :default +grammer+)
  => {:raw "function",
      :args {:start "(", :end ")", :space ""},
      :body {:start "{", :end "}"}})

^{:refer std.lang.base.emit-fn/emit-fn-preamble :added "4.0"}
(fact "constructs the function preamble"
  ^:hidden
  
  (emit-fn-preamble [:defn 'sym '[:int i 9, :const :int j 10]]
                    (emit-fn-block :default +grammer+)
                    +grammer+
                    {})
  => "sym(int i = 9,const int j = 10)")

^{:refer std.lang.base.emit-fn/emit-fn :added "3.0"}
(fact "emits a function template"
  ^:hidden

  (binding [common/*emit-fn* test-fn-emit]
    (emit-fn :function
             '[function sym [:int i 9, :const :int j 10]
               (for [(:= i 0) (< i j) (:++ i)]
                 (return i))]
             +grammer+
             {}))
  => (std.string/|
      "function sym(int i = 9,const int j = 10){"
      "  for(i = 0, i < j, ++i){"
      "    return i;"
      "  }"
      "}"))

^{:refer std.lang.base.emit-fn/test-fn-loop :added "4.0"}
(fact "add blocks, fn, var and const to emit")

^{:refer std.lang.base.emit-fn/test-fn-emit :added "4.0"}
(fact  "add blocks, fn, var and const to emit")


