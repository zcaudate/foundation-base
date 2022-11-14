(ns std.lib.transform.link)

(defn wrap-link-current
  "adds the current ref to `:link :current`"
  {:added "3.0"}
  ([f]
   (fn [tdata tsch nsv interim fns datasource]
     (let [interim (if (vector? (second (first tsch)))
                     (assoc-in interim [:link :current] tdata)
                     interim)]
       (f tdata tsch nsv interim fns datasource)))))

(defn wrap-link-attr
  "adds the parent link `:id` of the ref"
  {:added "3.0"}
  ([f]
   (fn [subdata [attr] nsv interim fns datasource]
     (let [interim (cond-> interim
                     (= (:type attr) :ref)
                     (assoc-in [:link :parent]
                               (if (-> attr :cardinality (= :many))
                                 {(-> attr :ref :rval)
                                  (keyword (str (name (first nsv)) ".id")
                                           (:id (-> interim :link :current)))})))]
       (f subdata [attr] nsv interim fns datasource)))))

(defn wrap-link-parent
  "adding parent to current data"
  {:added "0.1"}
  ([f]
   (fn [tdata tsch nsv interim fns datasource]
     (let [result (f tdata tsch nsv interim fns datasource)]
       (if (vector? (second (first tsch)))
         (merge (-> interim :link :parent) result)
         result)))))
