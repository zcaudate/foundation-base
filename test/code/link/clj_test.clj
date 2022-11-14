(ns code.link.clj-test
  (:use code.test)
  (:require [code.link.clj :refer :all]
            [clojure.java.io :as io]))

^{:refer code.link.clj/get-namespaces :added "3.0"}
(fact "gets the namespaces of a clojure s declaration"

  (get-namespaces '(:require repack.util.array
                             [repack.util.data]) [:use :require])
  => '(repack.util.array repack.util.data)

  (get-namespaces '(:require [repack.util.array :refer :all])
                  [:use :require])
  => '(repack.util.array)

  (get-namespaces '(:require [repack.util
                              [array :as array]
                              data]) [:use :require])
  => '(repack.util.array repack.util.data))

^{:refer code.link.clj/get-imports :added "3.0"}
(fact "gets the class imports of a clojure ns declaration"

  (get-imports '(:import java.lang.String
                         java.lang.Class))
  => '(java.lang.String java.lang.Class)

  (get-imports '(:import [java.lang String Class]))
  => '(java.lang.String java.lang.Class))

^{:refer code.link.clj/get-genclass :added "3.0"}
(fact "gets the gen-class of a clojure ns declaration"

  (get-genclass 'hello '[(:gen-class :name im.chit.hello.MyClass)])
  => '[im.chit.hello.MyClass]

  (get-genclass 'hello '[(:import im.chit.hello.MyClass)])
  => nil)

^{:refer code.link.clj/get-defclass :added "3.0"}
(fact "gets all the defclass and deftype definitions in a set of forms"

  (get-defclass 'hello '[(deftype Record [])
                         (defrecord Database [])])
  => '(hello.Record hello.Database))
