(ns std.lang.base.emit-fn-op-test
  (:use code.test)
  (:require [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.emit-fn :as fn]
            [std.lang.base.grammer :as grammer]
            [std.lib :as h]))

(def +reserved+
  (-> (grammer/build)
      (grammer/to-reserved)))

(def +grammer+
  (grammer/grammer :test +reserved+ helper/+default+))

^{:refer std.lang.base.emit-block/test-fn-emit.fn :adopt true :added "4.0"}
(fact "emit do*"

  (fn/test-fn-loop '(fn [] (return (+ a 1)))
                   +grammer+
                   {})
  => (std.string/|
      "function (){"
      "  (return (+ a 1));"
      "}")

  (fn/test-fn-loop '(fn:> (+ a 1))
                   +grammer+
                   {})
  => "(fn [] (return (+ a 1)))"

  (fn/test-fn-emit '(fn [] (return (+ a 1)))
                   +grammer+
                   {})
  => (std.string/|
      "function (){"
      "  return a + 1;"
      "}")  

  (fn/test-fn-emit '(fn:> (+ a 1))
                         +grammer+
                         {})
  => (std.string/|
      "function (){"
      "  return a + 1;"
      "}"))

^{:refer std.lang.base.emit-block/test-fn-emit.name :adopt true :added "4.0"}
(fact "emit do*"

  (fn/test-fn-loop '(fn hello [a := 1
                                     b := (+ 1 2)]
                            (return (+ a 1)))
                         +grammer+
                         {})
  => (std.string/|
      "function hello(a = 1,b = (+ 1 2)){"
      "  (return (+ a 1));"
      "}")

  (fn/test-fn-emit '(fn hello [a := 1
                                     b := (+ 1 2)]
                        (return (+ a 1)))
                     +grammer+
                     {})
  
  => (std.string/| "function hello(a = 1,b = 1 + 2){"
                   "  return a + 1;"
                   "}"))

