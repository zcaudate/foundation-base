(ns fx.gui.bind
  (:require [clojure.core :as clj]
            [fx.gui.observable :as obs])
  (:import (java.util Arrays
                      Collection)
           (java.util.stream Stream)
           (javafx.beans Observable)
           (javafx.beans.binding BooleanBinding
                                 ObjectBinding
                                 Bindings)
           (javafx.collections FXCollections
                               ObservableList)
           (javafx.beans.property SimpleBooleanProperty
                                  SimpleIntegerProperty
                                  SimpleFloatProperty
                                  SimpleLongProperty
                                  SimpleObjectProperty
                                  SimpleSetProperty
                                  SimpleStringProperty)
           (javafx.beans.value ObservableBooleanValue
                               ObservableValue)
           (hara.fx.gui FlattenedList)
           (org.fxmisc.easybind EasyBind)
           (eu.lestard.advanced_bindings.api MathBindings
                                             CollectionBindings
                                             LogicBindings
                                             SwitchBindings
                                             NumberBindings
                                             StringBindings))
  (:refer-clojure :exclude [filter
                            map
                            keep
                            reduce
                            or
                            and
                            concat]))

(defn concat
  "returns an observable concatentation of observable lists
 
   (bind/concat (obs/obs-array 1 2 3)
                (obs/obs-array 4 5 6))
   => [1 2 3 4 5 6]"
  {:added "3.0"}
  ([& obs]
   (FlattenedList. (FXCollections/observableArrayList ^Collection obs))))

(defn map
  "creates observable array using map
 
   (def -arr1- (obs/obs-array 0 1 2 3 4 5 6))
 
   (def -out1- (bind/map #(* 2 %) -arr1-))
 
   -out1- => [0 2 4 6 8 10 12]"
  {:added "3.0"}
  ([f ^ObservableList list]
   (EasyBind/map list (proxy [java.util.function.Function] []
                        (apply [val] (f val))))))

(defn ^ObjectBinding reduce
  "creates an observable object
 
   (.get (bind/reduce + 0 (obs/obs-array 0 1 2 3 4 5 6)))
   => 21"
  {:added "3.0"}
  ([f out ^ObservableList list]
   (Bindings/createObjectBinding
    (proxy [java.util.concurrent.Callable] []
      (call []
        (-> (.stream list)
            (.reduce (proxy [java.util.function.BinaryOperator] []
                       (apply [out i] (f out i))))
            (.orElse out))))
    (into-array Observable [list]))))

(defn ^ObjectBinding reduce-obs
  "creates an observable object from array of observables
 
   (.get (bind/reduce-obs +
                          0
                          (obs/obs-array (obs/obs-int 1)
                                         (obs/obs-int 2)
                                         (obs/obs-int 3)
                                         (obs/obs-int 4))))
   => 10"
  {:added "3.0"}
  ([f out list]
   (EasyBind/combine list
                     (proxy [java.util.function.Function] []
                       (apply [^Stream stream]
                         (-> stream
                             (.reduce (proxy [java.util.function.BinaryOperator] []
                                        (apply [out i] (f out i))))
                             (.orElse out)))))))

(defn ^BooleanBinding or
  "creates an or observable bool
 
   (.get (bind/or (obs/obs-bool false)
                  (obs/obs-bool true)))
   => true
 
   (.get (bind/or (obs/obs-bool false)
                  (obs/obs-bool false)))
   => false"
  {:added "3.0"}
  ([& obs]
   (LogicBindings/or (into-array ObservableValue obs))))

(defn ^BooleanBinding and
  "creates an and observable bool
 
   (.get (bind/and (obs/obs-bool true)
                   (obs/obs-bool true)))
   => true
 
   (.get (bind/and (obs/obs-bool false)
                   (obs/obs-bool true)))
   => false"
  {:added "3.0"}
  ([& obs]
   (LogicBindings/and (into-array ObservableValue obs))))

(defn ^ObjectBinding expr
  "creates an observable object from observables
 
   (.get (bind/expr (fn [a b]
                      (+ (.get a) (.get b)))
                    (obs/obs-int 1)
                    (obs/obs-int 2)))
   => 3"
  {:added "3.0"}
  ([f & obs]
   (Bindings/createObjectBinding
    (proxy [java.util.concurrent.Callable] []
      (call [] (apply f obs)))
    (into-array Observable obs))))
