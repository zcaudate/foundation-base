(ns std.lib.transform.base.keyword
  (:require [std.string.common :as str]
            [std.lib.foundation :as h]
            [std.string.wrap :as wrap]
            [std.string.path :as path]))

(defn wrap-single-keyword
  "removes the keyword namespace if there is one
 
   (graph/normalise {:account {:type :account.type/vip}}
                    {:schema (schema/schema {:account/type [{:type :keyword
                                                             :keyword {:ns :account.type}}]})}
                    {:normalise-single [wrap-single-keyword]})
   => {:account {:type :vip}}"
  {:added "3.0"}
  ([f]
   (fn [subdata [attr] nsv interim fns datasource]
     (cond (= :keyword (:type attr))
           (let [kns (-> attr :keyword :ns)
                 v   (if (and kns (= ((wrap/wrap path/path-ns) subdata)  kns))
                       ((wrap/wrap path/path-stem) subdata)
                       subdata)]
             v)
           :else
           (f subdata [attr] nsv interim fns datasource)))))
