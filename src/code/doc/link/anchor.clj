(ns code.doc.link.anchor)

(defn link-anchors-lu
  "adds anchor lookup table by name"
  {:added "3.0"}
  ([{:keys [articles] :as interim} name]
   (let [anchors (->> (get-in articles [name :elements])
                      (filter :tag)
                      (map #(select-keys % [:type :tag :number])))]

     (->> anchors
          (reduce (fn [m {:keys [type tag number] :as anchor}]
                    (let [m (if number
                              (assoc-in m [:by-number type number] anchor)
                              m)]
                      (assoc-in m [:by-tag tag] anchor)))
                  {})
          (assoc-in interim [:anchors-lu name])))))

(defn link-anchors
  "add anchors to the bundle
 
   (-> (link-anchors {:anchors-lu
                      {\"code.doc\" {:by-tag {:a 1
                                            :b 2}}}}
                     \"code.doc\")
       :anchors)
   => {\"code.doc\" {:a 1, :b 2}}"
  {:added "3.0"}
  ([{:keys [anchors-lu articles] :as interim} name]
   (assoc-in interim
             [:anchors name]
             (or (:by-tag (get anchors-lu name))
                 {}))))
