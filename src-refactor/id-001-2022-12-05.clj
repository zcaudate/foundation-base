(ns refactor.id-001-2022-12-05)

(comment

  (code.manage/refactor-test
   '[js]
   {:print {:function true}
    :write true
    :edits [(fn [nav]
              (code.query/modify
               nav
               [(fn [form]
                  (and (vector? form)
                       (= :% (first form))
                       (= 'n/Enclosed (second form))))]
               (fn [nav]
                 (let [forms  (->> nav
                                   (code.query.block/children)
                                   (filter (comp not std.block.type/void-block?))
                                   (drop 2))]
                   (-> nav
                       (code.query.block/replace '())
                       (code.query.block/down)
                       (code.query.block/insert 'n/EnclosedCode)
                       (h/-> (reduce (fn [nav elem]
                                       (-> nav
                                           (code.query.block/insert elem)
                                           (code.query.block/insert-newline)))
                                     %
                                     forms))
                       (code.query.block/up))))))]})
  
  (code.manage/locate-test
   :all
   {:query [(fn [form]
              (and (vector? form)
                   (= :% (first form))
                   (= 'n/Enclosed (second form))))]
    :print {:function true :item true :result true :summary true}}))


