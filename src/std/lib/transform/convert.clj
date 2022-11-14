(ns std.lib.transform.convert)

(defn wrap-single-model-convert
  "converts input according to model
   (graph/normalise {:account/name \"Chris\"}
                    {:schema (schema/schema examples/account-name-age-sex)
                     :pipeline {:convert {:account {:name (fn [x _] (.toLowerCase ^String x))}}}}
                    *wrappers*)
   => {:account {:name \"chris\"}}"
  {:added "3.0"}
  ([f]
   (fn [subdata [attr] nsv interim fns datasource]
     (let [trans-fn (:convert interim)
           nsubdata (if (fn? trans-fn)
                      (trans-fn subdata datasource)
                      subdata)]
       (f nsubdata [attr] nsv interim fns datasource)))))
