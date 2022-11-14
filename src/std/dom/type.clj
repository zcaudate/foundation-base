(ns std.dom.type)

;; - value     (constructor)
;; - element   (constructor + accessor) + class based format
;; - component (template)

;; :metatype  :value
;; :metaclass :fx/node
;; :metaprop  {:metatype :element,
;;             :metaclass :fx/node,
;;             :construct {}
;;             :props {:style <...> :class <...>}}

;; metaprop is used for dom type instantiation

(defonce +metaclass+ (atom {}))

(defonce +metaprops-tag+ (atom {}))

(defn metaclass
  "returns info associated with given metaclass
   
   (metaclass-add :test {:metatype :value :data \"hello\"})
   
   (metaclass :test)
   => {:id :test :metatype :value :data \"hello\"}"
  {:added "3.0"}
  ([] @+metaclass+)
  ([id]
   (get @+metaclass+ id)))

(defn metaclass-remove
  "removes metaclass information
 
   (metaclass-remove :test)"
  {:added "3.0"}
  ([id] 
   (when-let [res (metaclass id)]
    (swap! +metaclass+ dissoc id)
    res)))

(defn metaclass-add
  "adds metaclass information
 
   (metaclass-add :test {:metatype :value})"
  {:added "3.0"}
  ([id m] 
   (if-not (:metatype m) (throw (ex-info "requires metatype" {:id id :entry m}))) (let [m (assoc m :id id)]
    (swap! +metaclass+ assoc id m)
    m)))

(defn metaprops
  "returns metaprops info associated with the node
 
   (metaprops-add :test {:tag :test/node})
   
   (metaprops :test/node)
   => {:tag :test/node,
       :metaclass :test,
       :metatype :value}"
  {:added "3.0"}
  ([] @+metaprops-tag+)
  ([tag]
   (cond (keyword? tag)
         (get @+metaprops-tag+ tag)
         
         :else
         (throw (ex-info "Cannot find metaprops information" {:tag tag})))))

(defn metaprops-add
  "adds metaprops information
 
   (metaprops-add :test {:tag :test/node})"
  {:added "3.0"}
  ([id m] 
   (if-not (metaclass id)
    (throw (ex-info "No meta type available" {:type id
                                              :input m
                                              :available (vec (sort (keys (metaclass))))}))) (let [{:keys [metatype]} (metaclass id)
        {:keys [tag] :as m} (assoc m :metaclass id :metatype metatype)]
    (if-not tag (throw (ex-info ":tag required" {:input m})))
    (swap! +metaprops-tag+ assoc tag m)
    m)))

(defn metaprops-remove
  "removes metaprops information
 
   (metaprops-remove :test/node)"
  {:added "3.0"}
  ([tag]
   (let [{:keys [tag type] :as result} (metaprops tag)]
     (if tag (swap! +metaprops-tag+ dissoc tag))
     result)))

(defn metaprops-install
  "adds metaclass information
 
   (metaprops-install {:test/a {:tag :test/a
                                :metaclass :dom/value
                               :metatype :dom/value}})"
  {:added "3.0"}
  ([m] 
   (swap! +metaprops-tag+ merge m)))


(def +init+
  (do (metaclass-add :dom/value   {:metatype :dom/value})
      (metaclass-add :dom/element {:metatype :dom/element})))
