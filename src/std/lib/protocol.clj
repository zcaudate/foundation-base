(ns std.lib.protocol
  (:require [std.lib.class :as class]
            [std.lib.foundation :as h]))

(defn protocol:interface
  "returns the java interface for a given protocol
 
   (protocol:interface state/IStateGet)
   => std.protocol.state.IStateGet"
  {:added "3.0"}
  ([protocol]
   (-> protocol :on-interface)))

(defn protocol:methods
  "returns the methods provided by the protocol
 
   (protocol:methods state/IStateSet)
   => '[-clone-state -empty-state -set-state -update-state]"
  {:added "3.0"}
  ([protocol]
   (->> protocol :sigs vals (map :name) sort vec)))

(defn protocol:signatures
  "returns the method signatures provided by the protocol
 
   (protocol:signatures state/IStateSet)
   => '{-update-state  {:arglists ([obj f args opts]) :doc nil}
        -set-state     {:arglists ([obj v opts]) :doc nil}
        -empty-state   {:arglists ([obj opts])   :doc nil}
        -clone-state   {:arglists ([obj opts])   :doc nil}}"
  {:added "3.0"}
  ([protocol]
   (->> (:sigs protocol)
        (reduce (fn [out [_ m]]
                  (assoc out (:name m) (dissoc m :name :tag)))
                {}))))

(defn protocol:impls
  "returns types that implement the protocol
 
   (protocol:impls state/IStateSet)
   => (any (contains [clojure.lang.Agent
                      clojure.lang.Ref
                      clojure.lang.IAtom
                      clojure.lang.Volatile
                      clojure.lang.IPending
                      clojure.lang.Var]
                     :in-any-order :gaps-ok)
           #{})
 
   (protocol:impls state/IStateGet)
   => (any (contains [clojure.lang.IDeref clojure.lang.IPending]
                     :in-any-order :gaps-ok)
           #{})"
  {:added "3.0"}
  ([protocol]
   (-> protocol :impls keys set)))

(defn protocol?
  "checks whether an object is a protocol
 
   (protocol? state/IStateGet)
   => true"
  {:added "3.0"}
  ([obj]
   (boolean (and (instance? clojure.lang.PersistentArrayMap obj)
                 (every? #(contains? obj %) [:on :on-interface :var])
                 (-> obj :on str Class/forName h/suppress)
                 (protocol:interface obj)))))

(defn class:implements?
  "checks whether a type has implemented a protocol
 
   (class:implements? state/IStateGet clojure.lang.Atom)
   => true"
  {:added "3.0"}
  ([{:keys [impls] :as protocol} type]
   (boolean (or (get impls type)
                (class/class:inherits? (protocol:interface protocol)
                                       type)
                (map first impls)
                (filter #(class/class:inherits? % type))
                seq)))
  ([protocol type method]
   (let [method (keyword (str method))
         impls (:impls protocol)]
     (boolean (or (get-in impls [type method])
                  (class/class:inherits? (protocol:interface protocol)
                                         type)
                  (->> impls
                       (filter (fn [[_ methods]] (get methods method)))
                       (map first)
                       (filter #(class/class:inherits? % type))
                       seq))))))

(defn protocol:remove
  "removes a protocol
 
   (defprotocol -A-
     (-dostuff [_]))
 
   (do (extend-protocol -A-
         String
         (-dostuff [_]))
 
       (class:implements? -A- String \"-dostuff\"))
   => true
 
   (do (protocol:remove -A- String)
       (class:implements? -A- String \"-dostuff\"))
   => false"
  {:added "3.0"}
  ([protocol atype]
   (-reset-methods (alter-var-root (:var protocol) update-in [:impls] dissoc atype))))
