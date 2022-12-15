(ns fx.gui.bind-test
  (:use code.test)
  (:require [fx.gui.bind :refer :all :as bind]
            [fx.gui.observable :as obs])
  (:refer-clojure :exclude [reduce
                            map
                            concat
                            filter
                            and
                            or]))

^{:refer fx.gui.bind/concat :added "3.0"}
(fact "returns an observable concatentation of observable lists"

  (bind/concat (obs/obs-array 1 2 3)
               (obs/obs-array 4 5 6))
  => [1 2 3 4 5 6])

^{:refer fx.gui.bind/map :added "3.0"}
(fact "creates observable array using map"

  (def -arr1- (obs/obs-array 0 1 2 3 4 5 6))

  (def -out1- (bind/map #(* 2 %) -arr1-))

  -out1- => [0 2 4 6 8 10 12] ^:hidden

  (doto -arr1- (.remove 1) (.remove 3) (.add 8) (.add 9))

  -out1- => [0 4 8 10 12 16 18])

^{:refer fx.gui.bind/reduce :added "3.0"}
(fact "creates an observable object"

  (.get (bind/reduce + 0 (obs/obs-array 0 1 2 3 4 5 6)))
  => 21)

^{:refer fx.gui.bind/reduce-obs :added "3.0"}
(fact "creates an observable object from array of observables"

  (.get (bind/reduce-obs +
                         0
                         (obs/obs-array (obs/obs-int 1)
                                        (obs/obs-int 2)
                                        (obs/obs-int 3)
                                        (obs/obs-int 4))))
  => 10)

^{:refer fx.gui.bind/or :added "3.0"}
(fact "creates an or observable bool"

  (.get (bind/or (obs/obs-bool false)
                 (obs/obs-bool true)))
  => true

  (.get (bind/or (obs/obs-bool false)
                 (obs/obs-bool false)))
  => false)

^{:refer fx.gui.bind/and :added "3.0"}
(fact "creates an and observable bool"

  (.get (bind/and (obs/obs-bool true)
                  (obs/obs-bool true)))
  => true

  (.get (bind/and (obs/obs-bool false)
                  (obs/obs-bool true)))
  => false)

^{:refer fx.gui.bind/expr :added "3.0"}
(fact "creates an observable object from observables"

  (.get (bind/expr (fn [a b]
                     (+ (.get a) (.get b)))
                   (obs/obs-int 1)
                   (obs/obs-int 2)))
  => 3)

(comment
  (./import)
  (./scaffold)
  (./arrange))

(comment
  ^{:refer fx.gui.bind/filter :added "3.0"}
  (fact "creates observable array using filter"

    (def -arr0- (obs/obs-array 0 1 2 3 4 5 6 7 8))

    (def -out0- (->> -arr0-
                     (bind/filter even?)
                     (bind/filter #(= 0 (rem % 4)))))

    -out0- => [0 4 8]

    (doto -arr0- (.add 16) (.add 20))

    -out0- => [0 4 8 16 20])

  ^{:refer fx.gui.bind/bind-array :added "3.0"}
  (fact "binds a target array to a source array with a mapping function"

    (bind-array (obs/obs-array)
                (obs/obs-array 1 2 3)
                str)
    => ["1" "2" "3"]))