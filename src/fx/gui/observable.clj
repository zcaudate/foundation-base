(ns fx.gui.observable
  (:require [std.lib.extend :as extend]
            [std.lib :as h])
  (:import (javafx.collections FXCollections
                               ObservableArray
                               ObservableList
                               ObservableMap
                               ObservableSet
                               ArrayChangeListener
                               ListChangeListener
                               MapChangeListener
                               SetChangeListener)
           (javafx.beans.value ObservableValue
                               ChangeListener)
           (javafx.beans.property SimpleBooleanProperty
                                  SimpleDoubleProperty
                                  SimpleFloatProperty
                                  SimpleIntegerProperty
                                  SimpleListProperty
                                  SimpleLongProperty
                                  SimpleMapProperty
                                  SimpleObjectProperty
                                  SimpleSetProperty
                                  SimpleStringProperty)
           (java.util Collection)))

(defn- set-all
  ([^ObservableList list ^Collection entries]
   (.setAll list entries)))

(defprotocol IProperty
  (set-value [binding val])
  (get-value [binding])
  (bind [binding source])
  (unbind [binding]))

(extend/extend-all

 IProperty

 [(set-value ([binding val] (.setValue binding val)))
  (get-value ([binding] (.getValue binding)))
  (bind ([binding source] (.bind binding source)))
  (unbind ([binding] (.unbind binding)))]

 [SimpleBooleanProperty
  SimpleDoubleProperty
  SimpleFloatProperty
  SimpleIntegerProperty
  SimpleListProperty
  SimpleLongProperty
  SimpleMapProperty
  SimpleObjectProperty
  SimpleSetProperty
  SimpleStringProperty]
 [nil nil nil nil])

(defn ^ObservableList obs-array
  "creates an observable array
 
   (obs-array 1 2 3 4)
   => com.sun.javafx.collections.ObservableListWrapper"
  {:added "3.0"}
  ([]
   (FXCollections/observableArrayList))
  ([& vals]
   (doto (FXCollections/observableArrayList)
     (.addAll ^Collection vals))))

(defn ^ObservableMap obs-map
  "creates an observable map
 
   (obs-map :a 1 :b 2)
   => com.sun.javafx.collections.ObservableMapWrapper"
  {:added "3.0"}
  ([]
   (FXCollections/observableHashMap))
  ([& vals]
   (doto (FXCollections/observableHashMap)
     (.putAll (apply hash-map vals)))))

(defn ^SimpleBooleanProperty obs-bool
  "creates an observable bool
 
   (obs-bool true)
   => javafx.beans.property.SimpleBooleanProperty"
  {:added "3.0"}
  ([x]
   (SimpleBooleanProperty. x)))

(defn ^SimpleIntegerProperty obs-int
  "creates an observable integer
 
   (obs-int 0)
   => javafx.beans.property.SimpleIntegerProperty"
  {:added "3.0"}
  ([x]
   (SimpleIntegerProperty. x)))

(defn ^SimpleFloatProperty obs-float
  "creates an observable float
 
   (obs-float 1)
   => javafx.beans.property.SimpleFloatProperty"
  {:added "3.0"}
  ([x]
   (SimpleFloatProperty. x)))

(defn ^SimpleLongProperty obs-long
  "creates an observable long
 
   (obs-long 1)
   => javafx.beans.property.SimpleLongProperty"
  {:added "3.0"}
  ([x]
   (SimpleLongProperty. x)))

(defn ^SimpleObjectProperty obs-object
  "creates an observable object
 
   (obs-object \"hello\")
   => javafx.beans.property.SimpleObjectProperty"
  {:added "3.0"}
  ([]
   (obs-object nil))
  ([x]
   (SimpleObjectProperty. x)))

(defn ^SimpleStringProperty obs-str
  "creates an observable string
 
   (obs-str \"hello\")
   => javafx.beans.property.SimpleStringProperty"
  {:added "3.0"}
  ([x]
   (SimpleStringProperty. x)))

(defn ^ObservableSet obs-set
  "creates an observable set
 
   (obs-set 1 2 3 4)
   => com.sun.javafx.collections.ObservableSetWrapper"
  {:added "3.0"}
  ([& vals]
   (FXCollections/observableSet ^"[Ljava.lang.Object;"
    (into-array Object vals))))

(defmulti add-listener
  "adds a listener to the observable"
  {:added "3.0"}
  (fn [obs thunk] (type obs)))

(defmethod add-listener ObservableArray
  ([^ObservableArray obs thunk]
   (.addListener obs (proxy [ArrayChangeListener] []
                       (onChanged [changes] (thunk))))))

(defmethod add-listener ObservableList
  ([^ObservableList obs thunk]
   (.addListener obs (proxy [ListChangeListener] []
                       (onChanged [changes] (thunk))))))

(defmethod add-listener ObservableMap
  ([^ObservableMap obs thunk]
   (.addListener obs (proxy [MapChangeListener] []
                       (onChanged [changes] (thunk))))))

(defmethod add-listener ObservableSet
  ([^ObservableSet obs thunk]
   (.addListener obs (proxy [SetChangeListener] []
                       (onChanged [changes] (thunk))))))

(defmethod add-listener ObservableValue
  ([^ObservableValue obs thunk]
   (.addListener obs (proxy [ChangeListener] []
                       (changed [_ _ _] (thunk))))))

(defmethod add-listener clojure.lang.ARef
  ([obs thunk]
   (add-watch obs (str (java.util.UUID/randomUUID))
              (fn [_ _ _ _] (thunk)))))

(defn get-val
  "gets value of observable"
  {:added "3.0"}
  ([obs]
   (cond (or (instance? ObservableArray obs)
             (instance? ObservableList obs)
             (instance? ObservableMap obs)
             (instance? ObservableSet obs))
         obs

         (instance? clojure.lang.IRef obs)
         (deref obs)

         (instance? ObservableValue obs)
         (get-value obs)

         :else
         obs)))

(defmulti create-thunk
  "creates a thunk for handling observable"
  {:added "3.0"}
  (fn [target f sources] (type target)))

(defmethod create-thunk ObservableMap
  ([^ObservableMap target f sources]
   (fn []
     (let [output (apply f (map get-val sources))
           diff   (h/diff target output)]
       (doseq [k (set (map first (keys (:- diff))))]
         (.remove target k))
       (.putAll target (select-keys output (set (map first (concat (keys (:+ diff))
                                                                   (keys (:> diff)))))))))))

(defmethod create-thunk ObservableSet
  ([^ObservableSet target f sources]
   (fn []
     (let [output  (apply f (map get-val sources))
           added   (h/difference output target)
           removed (h/difference (set target) output)]
       (.removeAll target removed)
       (.addAll target added)))))

(defmethod create-thunk ObservableArray
  ([^ObservableArray target f sources]
   (fn []
     (set-all target (apply f (map get-val sources))))))

(defmethod create-thunk ObservableList
  ([^ObservableList target f sources]
   (fn []
     (set-all target (apply f (map get-val sources))))))

(defmethod create-thunk ObservableValue
  ([^ObservableValue target f sources]
   (fn []
     (set-value target (apply f (map get-val sources))))))

(defmethod create-thunk clojure.lang.Volatile
  ([target f sources]
   (fn []
     (vreset! target (apply f (map get-val sources))))))

(defmethod create-thunk clojure.lang.Atom
  ([target f sources]
   (fn []
     (reset! target (apply f (map get-val sources))))))

(defn bind
  "binds a value to an expression and a series of values
   (-> (doto (obs-long 0)
         (bind +
               (obs-long 1)
               (obs-long 1)
               (obs-long 1)
               (obs-long 1)))
       (.get))
   => 4"
  {:added "3.0"}
  ([target f & sources]
   (let [thunk (create-thunk target f sources)
         _     (thunk)]
     (doseq [source sources]
       (add-listener source thunk))
     target)))
