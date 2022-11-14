(ns std.lib.transform.validate
  (:require [std.lib.foundation :as h]))

(defn wrap-single-model-validate
  "validates input according to model
 
   (graph/normalise {:account/name \"Chris\"}
                    {:schema (schema/schema examples/account-name-age-sex)
                     :pipeline {:validate {:account {:name (fn [x _] (number? x))}}}}
                    *wrappers*)
   => (throws-info {:id :not-validated :nsv [:account :name]})
 
   (graph/normalise {:account/name \"Bob\"}
                    {:schema (schema/schema examples/account-name-age-sex)
                     :pipeline {:validate {:account {:name (fn [x _] (= x \"Bob\"))}}}}
                    *wrappers*)
   => {:account {:name \"Bob\"}}"
  {:added "3.0"}
  ([f]
   (fn [subdata [attr] nsv interim fns datasource]
     (let [subvalidate (:validate interim)]
       (cond (fn? subvalidate)
             (let [res (subvalidate subdata datasource)
                   nsubdata (cond (or (true? res) (nil? res))
                                  subdata

                                  :else
                                  (h/error (str "SINGLE_VALIDATE: Not validated")
                                           {:id :not-validated
                                            :data subdata
                                            :nsv nsv
                                            :key-path (:key-path interim)
                                            :validator subvalidate
                                            :error res}))]
               (f nsubdata [attr] nsv interim fns datasource))

             :else
             (f subdata [attr] nsv interim fns datasource))))))
