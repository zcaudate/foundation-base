(ns fx.gui.observable-test
  (:use code.test)
  (:require [fx.gui.observable :refer :all :as obs])
  (:refer-clojure :exclude [reduce
                            map
                            concat
                            filter
                            and
                            or]))

^{:refer fx.gui.observable/obs-array :added "3.0"}
(fact "creates an observable array"

  (obs-array 1 2 3 4)
  => com.sun.javafx.collections.ObservableListWrapper)

^{:refer fx.gui.observable/obs-map :added "3.0"}
(fact "creates an observable map"

  (obs-map :a 1 :b 2)
  => com.sun.javafx.collections.ObservableMapWrapper)

^{:refer fx.gui.observable/obs-bool :added "3.0"}
(fact "creates an observable bool"

  (obs-bool true)
  => javafx.beans.property.SimpleBooleanProperty)

^{:refer fx.gui.observable/obs-int :added "3.0"}
(fact "creates an observable integer"

  (obs-int 0)
  => javafx.beans.property.SimpleIntegerProperty)

^{:refer fx.gui.observable/obs-float :added "3.0"}
(fact "creates an observable float"

  (obs-float 1)
  => javafx.beans.property.SimpleFloatProperty)

^{:refer fx.gui.observable/obs-long :added "3.0"}
(fact "creates an observable long"

  (obs-long 1)
  => javafx.beans.property.SimpleLongProperty)

^{:refer fx.gui.observable/obs-object :added "3.0"}
(fact "creates an observable object"

  (obs-object "hello")
  => javafx.beans.property.SimpleObjectProperty)

^{:refer fx.gui.observable/obs-str :added "3.0"}
(fact "creates an observable string"

  (obs-str "hello")
  => javafx.beans.property.SimpleStringProperty)

^{:refer fx.gui.observable/obs-set :added "3.0"}
(fact "creates an observable set"

  (obs-set 1 2 3 4)
  => com.sun.javafx.collections.ObservableSetWrapper)

^{:refer fx.gui.observable/add-listener :added "3.0"}
(fact "adds a listener to the observable")

^{:refer fx.gui.observable/get-val :added "3.0"}
(fact "gets value of observable")

^{:refer fx.gui.observable/create-thunk :added "3.0"}
(fact "creates a thunk for handling observable")

^{:refer fx.gui.observable/bind :added "3.0"}
(fact "binds a value to an expression and a series of values"
  (-> (doto (obs-long 0)
        (bind +
              (obs-long 1)
              (obs-long 1)
              (obs-long 1)
              (obs-long 1)))
      (.get))
  => 4 ^:hidden

  (doto (obs-set 0)
    (bind hash-set
          (obs-long 1)
          (obs-long 4)
          (obs-long 3)
          (obs-long 1)))
  => #{4 1 3})

(comment
  (./import)
  (./scaffold)
  (./arrange))
