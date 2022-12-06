(ns refactor.id-001-2022-12-05)

(comment

  (code.manage/refactor-code
   '[stats]
   {:print {:function true}
    :edits [(fn [nav]
              (code.query/modify
               nav
               [(fn [form]
                  (and (symbol? form)
                       (= "g" (namespace form))))]
               (fn [nav]
                 (let [prev (code.query.block/value nav)
                       next (symbol "common-global"
                                    (name prev))]
                   (code.query.block/replace nav next)))))]})
  

  (code.manage/locate-code
   '[stats]
   {:query '[[statsdb.interface.common-global :as g]]
    :print {:function true :item true :result true :summary true}})
  )


