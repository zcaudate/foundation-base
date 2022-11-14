(ns std.lib.transform.base.ref
  (:require [std.lib.foundation :as h]))

(defn wrap-keyword-id
  "Allow keywords for refs
 
   (graph/normalise-base {:student {:id \"a\"
                                    :profile :profile.id/a}}
                         {:schema -schema-}
                         {:normalise-single [wrap-keyword-id]})
   => {:student {:id \"a\", :profile :profile.id/a}}"
  {:added "3.0"}
  ([f]
   (fn [subdata [attr] nsv interim fns datasource]
     (if (and (= (:type attr) :ref)
              (keyword? subdata))
       subdata
       (f subdata [attr] nsv interim fns datasource)))))
