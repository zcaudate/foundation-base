(ns std.lang.base.emit-preprocess-test
  (:use code.test)
  (:require [std.lang.base.emit-preprocess :refer :all]
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

^{:refer std.lang.base.emit-preprocess/macro-form :added "4.0"}
(fact "gets the current macro form")

^{:refer std.lang.base.emit-preprocess/macro-opts :added "4.0"}
(fact "gets current macro-opts")

^{:refer std.lang.base.emit-preprocess/macro-grammer :added "4.0"}
(fact "gets the current grammer")

^{:refer std.lang.base.emit-preprocess/with:macro-opts :added "4.0"}
(fact "bind macro opts")

^{:refer std.lang.base.emit-preprocess/to-input-form :added "4.0"}
(fact "processes a form"
  
  (def hello 1)
  
  (to-input-form '(@! (+ 1 2 3)))
  => '(!:template (+ 1 2 3))
  
  (to-input-form '@#'hello)
  => '(!:deref (var std.lang.base.emit-preprocess-test/hello))
  
  (to-input-form '@(+ 1 2 3))
  => '(!:eval (+ 1 2 3))

  (to-input-form '(@.lua (do 1 2 3)))
  => '(!:lang {:lang :lua} (do 1 2 3)))

^{:refer std.lang.base.emit-preprocess/to-input :added "4.0"}
(fact "converts a form to input (extracting deref forms)"
  ^:hidden

  (to-input '(do (~! [1 2 3 4])))
  => (throws)
  
  (binding [*macro-splice* true]
    (to-input '(do (~! [1 2 3 4]))))
  => '(do 1 2 3 4))

^{:refer std.lang.base.emit-preprocess/get-fragment :added "4.0"}
(fact "gets the fragment given a symbol and modules"
  ^:hidden
  
  (get-fragment 'L.core/add
                (:modules prep/+book-min+)
                {:module {:id 'L.util
                          :link '{u L.core}}})
  => entry/book-entry?)

^{:refer std.lang.base.emit-preprocess/process-namespaced-resolve :added "4.0"}
(fact "resolves symbol in current namespace"
  ^:hidden
  
  (process-namespaced-resolve 'u/add
                              (:modules prep/+book-min+)
                              {:module   {:id 'L.util
                                          :link '{u L.core}}})
  => '[L.core add L.core/add]

  (process-namespaced-resolve 'u/UNKNOWN
                              (:modules prep/+book-min+)
                              {:module   {:id  'L.util
                                          :link '{u L.core}}})
  => '[L.core UNKNOWN L.core/UNKNOWN]
  
  (process-namespaced-resolve 'other/function
                              (:modules prep/+book-min+)
                              {:module   {:id 'L.util
                                          :link '{u L.core}}})
  => (throws))

^{:refer std.lang.base.emit-preprocess/process-namespaced-symbol :added "4.0"}
(fact "process namespaced symbols"
  ^:hidden
  
  (process-namespaced-symbol 'u/add
                             (:modules prep/+book-min+)
                             {:module   {:id 'L.util
                                         :link '{u L.core}}}
                             (volatile! #{}))
  => '(fn [x y] (return (+ x y)))
  
  (process-namespaced-symbol 'u/sub
                             (:modules prep/+book-min+)
                             {:module   {:id 'L.util
                                         :link '{u L.core}}}
                             (volatile! #{}))
  => '(fn [x y] (return (- x y)))

  (process-namespaced-symbol 'u/identity-fn
                             (:modules prep/+book-min+)
                             {:module   {:id 'L.util
                                         :link '{u L.core}}}
                             (volatile! #{}))
  => 'L.core/identity-fn

  (process-namespaced-symbol '-/hello
                             (:modules prep/+book-min+)
                             
                             {:entry {:id 'hello}
                              :module   {:id 'L.util
                                         :link '{u L.core
                                                 - L.util}}}
                             (volatile! #{}))
  => 'L.util/hello

  (process-namespaced-symbol 'u/UNKNOWN
                              (:modules prep/+book-min+)
                              {:module   {:id  'L.util
                                          :link '{u L.core}}}
                              (volatile! #{}))
  => (throws))

^{:refer std.lang.base.emit-preprocess/process-inline-assignment :added "4.0"}
(fact "prepares the form for inline assignment"
  ^:hidden
  
  (def +form+
    (process-inline-assignment '(var a := (u/identity-fn 1) :inline)
                               (:modules prep/+book-min+)
                               '{:module {:link {u L.core}}}
                               true))
  +form+
  => '(var a := (L.core/identity-fn 1))


  (meta (last +form+))
  => {:assign/inline true})

^{:refer std.lang.base.emit-preprocess/to-staging-form :added "4.0"}
(fact "different staging forms"
  ^:hidden
  
  (to-staging-form '(!:template (+ 1 2 3))
                   nil
                   (:modules prep/+book-min+)
                   '{:module {:link {u L.core}}}
                   identity)
  => 6

  @(to-staging-form '(!:eval (+ 1 2 3))
                    nil
                    (:modules prep/+book-min+)
                   '{:module {:link {u L.core}}}
                   identity)
  => '(!:eval (+ 1 2 3))

  (to-staging-form '(hello 1 2 3)
                   {:reserved {'hello {:type :template
                                       :macro (fn [[_ & args]]
                                                (cons '+ (concat args args)))}}}
                   (:modules prep/+book-min+)
                   '{:module {:link {u L.core}}}
                   identity)
  => '(+ 1 2 3 1 2 3)

  (to-staging-form '(hello 1 2 3)
                   {:reserved {'hello {:type :hard-link
                                       :raw 'world}}}
                   (:modules prep/+book-min+)
                   '{:module {:link {u L.core}}}
                   identity)
  => '(world 1 2 3))

^{:refer std.lang.base.emit-preprocess/to-staging :added "4.0"}
(fact "converts the stage"
  ^:hidden
  
  (to-staging '(u/add (u/identity-fn 1) 2)
              nil
              (:modules prep/+book-min+)
              '{:module {:link {u L.core}}})
  => '[(+ (L.core/identity-fn 1) 2)
       #{L.core/identity-fn}]
  
  (to-staging '(u/sub (u/add (u/identity-fn 1) 2)
                      (-/hello))
              nil
              (:modules prep/+book-min+)
              '{:entry {:id hello}
                :module {:id L.util
                         :link {u L.core}}})
  => '[(- (+ (L.core/identity-fn 1) 2)
          (L.util/hello))
       #{L.core/identity-fn}]

  ((juxt identity
         (comp meta last first))
   (to-staging '(var a := (u/identity-fn 1) :inline)
               {:reserved {'var {:emit :def-assign}}}
               (:modules prep/+book-min+)
               '{:module {:link {u L.core}}}))
  => '[[(var a := (L.core/identity-fn 1)) #{}]
       {:assign/inline true}])

^{:refer std.lang.base.emit-preprocess/to-resolve :added "4.0"}
(fact "resolves only the code symbols (no macroexpansion)"
  ^:hidden

  (to-resolve '(u/add (u/identity-fn 1) 2)
               nil
               (:modules prep/+book-min+)
               '{:module {:link {u L.core}}})
  => '(L.core/add (L.core/identity-fn 1) 2))
