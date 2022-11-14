(ns code.doc.link.test
  (:require [code.test.base.executive :as executive]
            [std.fs :as fs]
            [std.lib :as h]))

(def ^:dynamic *run-tests* nil)

(defn failed-tests
  "creates a link to all the failed tests in the project"
  {:added "3.0"}
  ([facts]
   (->> facts
        (keep (fn [{:keys [results meta] :as fact}]
                (let [failures  (->> results
                                     (filter #(and (-> % :from (= :verify))
                                                   (-> % :data false?)))
                                     (map (fn [{:keys [actual checker meta]}]
                                            (-> actual
                                                (select-keys [:data :form])
                                                (assoc :check (:form checker)
                                                       :code (select-keys meta [:line :column]))))))]
                  (if-not (empty? failures)
                    (assoc meta :output failures))))))))

(defn link-tests
  "creates a link to all the passed tests in the project"
  {:added "3.0"}
  ([{:keys [project] :as interim} name]
   (if *run-tests*
     (let [path   (or (str (:root project) "/" (get-in project [:publish :files name :input]))
                      ((:lookup project) (symbol name)))
           rel    (str (fs/relativize (:root project) path))
           fails  (->> (fn [id sink] (load-file path))
                       executive/accumulate
                       failed-tests
                       (h/map-juxt [:line identity]))]
       (update-in interim [:articles name :elements]
                  (fn [elements]
                    (map (fn [{:keys [type] :as elem}]
                           (cond (not= :test type)
                                 elem

                                 :else
                                 (if-let [failed (get fails (-> elem :line :row))]
                                   (assoc elem :failed failed :path rel)
                                   elem)))
                         elements))))
     interim)))

(comment
  "DO NOT DELETE!!!!!"

  (:lookup PROJECT)
  (-> (parse/parse-file
       "test/documentation/hara_zip.clj" PROJECT)
      (->> (assoc-in {} [:articles "hara-zip" :elements]))
      (assoc :project PROJECT)
      (link-tests "hara-zip")
      (get-in [:articles "hara-zip" :elements]))

  (->> (fn [id sink]
         (load-file "/Users/chris/Development/chit/hara/test/documentation/hara_zip.clj"))
       runner/accumulate
       failed-tests)
  (map (juxt :line identity))
  (into {}))

(comment
  (do (require '[code.doc :as publish]
               '[code.doc.theme :as theme]
               '[code.project :as project]
               '[code.doc.parse :as parse])

      (def project-file "/Users/chris/Development/chit/hara/project.clj")

      (def PROJECT (let [project (project/project project-file)]
                     (assoc project :lookup (project/file-lookup project))))))

