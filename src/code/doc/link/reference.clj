(ns code.doc.link.reference
  (:require [code.framework.docstring :as docstring]))

(defn link-references
  "links references when working with specific source and test code"
  {:added "3.0"}
  ([{:keys [references] :as interim} name]
   (update-in interim [:articles name :elements]
              (fn [elements]
                (mapv (fn [element]
                        (if (-> element :type (= :reference))
                          (let [{:keys [refer mode]} element
                                refer (symbol refer)
                                nsp (symbol (.getNamespace refer))
                                var (symbol (.getName refer))
                                mode (or mode :source)
                                code (if-let [code (get-in references [nsp var mode :code])]
                                       (case mode
                                         :source code
                                         :test   (docstring/->refstring code))
                                       (str "MISSING REFERENCE " {:mode mode :refer refer}))]
                            (-> element
                                (assoc :type :reference
                                       :indentation (case mode :source 0 :test 2)
                                       :code code
                                       :mode mode)
                                (update-in [:title] #(or % (str (clojure.core/name mode) " of <i>" refer "</i>")))))
                          element))
                      elements)))))
