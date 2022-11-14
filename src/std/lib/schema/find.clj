(ns std.lib.schema.find)

(defn all-attrs
  "finds all attributes satisfying `f` in a schema"
  {:added "3.0"}
  ([fsch f]
   (->> fsch
        (filter (fn [[k [attr]]] (f attr)))
        (into {}))))

(defn all-idents
  "finds all idents satisfying `f` in a schema"
  {:added "3.0"}
  ([fsch f]
   (keys (all-attrs fsch f))))

(defn is-reverse-ref?
  "predicate for reverse ref"
  {:added "3.0"}
  ([attr]
   (and (= :ref (:type attr))
        (= :reverse (-> attr :ref :type)))))
