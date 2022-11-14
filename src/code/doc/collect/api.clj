(ns code.doc.collect.api
  (:require [code.doc.collect.reference :as reference]))

(defn collect-apis
  "gets all the `api` tags in the document
 
   (let [project (project/project)
         project (assoc project :lookup (project/file-lookup project))
         elems   (parse/parse-file \"src-doc/documentation/code_doc.clj\" project)
         bundle  {:articles {\"code-doc\" {:elements elems}}
                  :references {}
                  :project project}]
     (with-redefs [reference/reference-namespaces (fn [_ _ namespaces] namespaces)]
       (-> (collect-apis bundle \"code-doc\")
          :references)))
   => '[code.doc code.doc.parse]"
  {:added "3.0"}
  ([{:keys [articles project] :as interim} name]
   (let [all    (->> (get-in articles [name :elements])
                     (filter #(-> % :type (= :api))))
         namespaces (-> (map (comp symbol :namespace) all))]
     (-> interim
         (update-in [:references]
                    (fnil (fn [references]
                            (reference/reference-namespaces references
                                                            (:lookup project)
                                                            namespaces))
                          {}))))))
