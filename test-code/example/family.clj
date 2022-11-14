(ns example.family)

(def family-links
  {:person {:name    [{}]
            :age     [{:type :long}]
            :gender  [{:type :enum
                       :enum {:ns :person.gender
                              :values #{:m :f}}}]
            :parent  [{:type :ref
                        :cardinality :many
                        :ref {:ns   :person
                              :rval :child}}]
            :sibling [{:type :ref
                        :cardinality :many
                        :ref {:ns :person
                              :mutual true}}]
            :grandson [{:type :alias
                        :alias {:ns :child/child
                                :template {:child {:son {}}}}}]
            :granddaughter [{:type :alias
                             :alias {:ns :child/child
                                     :template {:child {:daughter {}}}}}]
            :grandma [{:type :alias
                       :alias {:ns :parent/parent
                               :template {:parent {:mother {}}}}}]
            :grandpa [{:type :alias
                       :alias {:ns :parent/parent
                               :template {:parent {:father {}}}}}]
            :nephew  [{:type :alias
                       :alias {:ns :sibling/child
                              :template {:sibling {:child {:gender :m}}}}}]
            :niece   [{:type :alias
                       :alias {:ns :sibling/child
                              :template {:sibling {:child {:gender :m}}}}}]
            :uncle  [{:type :alias
                       :alias {:ns :parent/sibling
                               :template {:parent {:brother {}}}}}]
            :aunt   [{:type :alias
                      :alias {:ns :parent/sibling
                              :template {:parent {:sister {}}}}}]
            :son     [{:type :alias
                       :alias {:ns :child
                               :template {:child {:gender :m}}}}]
            :daughter [{:type :alias
                        :alias {:ns :child
                               :template {:child {:gender :f}}}}]
            :father  [{:type :alias
                       :alias {:ns :parent
                               :template {:parent {:gender :m}}}}]
            :mother  [{:type :alias
                       :alias {:ns :parent
                               :template {:parent {:gender :f}}}}]
            :brother  [{:type :alias
                        :alias {:ns :sibling
                                :template {:sibling {:gender :m}}}}]
            :sister  [{:type :alias
                       :alias {:ns :sibling
                               :template {:sibling {:gender :f}}}}]
            :elder-brother [{:type :alias
                             :alias {:ns :sibling
                                     :template {:age '?x
                                                :sibling {:age '(< ?x)
                                                          :gender :m}}}}]
            :full-sibling  [{:type :alias
                             :alias {:ns :sibling
                                     :template {:parent #{{:gender :m :+ {:db {:id '?x}}}
                                                          {:gender :f :+ {:db {:id '?y}}}}
                                                :sibling {:parent #{{:gender :m :+ {:db {:id '?x}}}
                                                                    {:gender :f :+ {:db {:id '?y}}}}}}}}]}
   :male   [{:type :alias
             :alias {:ns :person
                     :template {:person {:gender :m}}}}]

   :female [{:type :alias
             :alias {:ns :person
                     :template {:person {:gender :f}}}}]})
