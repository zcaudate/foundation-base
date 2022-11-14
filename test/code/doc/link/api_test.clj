(ns code.doc.link.api-test
  (:use code.test)
  (:require [code.doc.link.api :refer :all]
            [code.project :as project]))

^{:refer code.doc.link.api/external-vars :added "3.0"}
(fact "grabs external vars from the `module/include` form"

  ;; THIS NEEDS FIXING to use h/intern-all as well as module/include
  (external-vars (project/file-lookup (project/project))
                 'code.test)
  => {}
  #_'{code.test.checker.common [throws exactly approx satisfies stores anything]
      code.test.checker.collection [contains just contains-in just-in throws-info]
      code.test.checker.logic [any all is-not]
      code.test.compile [fact facts =>]})

^{:refer code.doc.link.api/create-api-table :added "3.0"}
(fact "creates a api table for publishing")

^{:refer code.doc.link.api/link-apis :added "3.0"}
(fact "links all the api source and test files to the elements")

(comment
  (external-vars (project/file-lookup (project/project))
                 'std.block)

  (external-vars (project/file-lookup (project/project))
                 'std.string)

  (-> INTERIM
      (link-apis "hara-zip")
      (get-in [:articles "hara-zip" :elements])
      (->> (filter #(-> % :type (= :api))))
      (first)
      :table
      (keys))

  (keys (:references INTERIM)))

(comment
  (do (require '[code.doc :as publish]
               '[code.doc.theme :as theme]
               '[code.project :as project]
               '[code.doc.parse :as parse]
               '[code.doc.collect.reference :refer [collect-references]]
               '[code.doc.collect.api :refer [collect-apis]])

      (def project-file "/Users/chris/Development/chit/hara/project.clj")

      (def PROJECT (let [project (project/project project-file)]
                     (assoc project :lookup (project/file-lookup project))))

      (theme/load-settings "stark" PROJECT)

      (def INTERIM (-> (parse/parse-file
                        "test/documentation/hara_zip.clj" PROJECT)
                       (->> (assoc-in {} [:articles "hara-zip" :elements]))
                       (assoc :project PROJECT)
                       (collect-references "hara-zip")))))
