(ns example.data)

(def account-name-age-sex
  {:account {:name [{}]
             :age  [{:type :long}]
             :sex  [{:type :enum
                     :enum {:ns :account.sex
                            :values #{:m :f}}}]}})

(def link-value-next
  {:link {:value [{}]
          :next  [{:type :ref
                   :ref {:ns :link
                         :rval :prev}}]}})

(def account-orders-items-image
 {:account {:user     [{:representative true}]
            :tags     [{:cardinality :many}]}
  :order   {:account [{:type :ref
                       :ref {:ns :account}}]
              :number [{:type :long}]}
  :item    {:name   [{}]
            :order  [{:type :ref
                      :ref {:ns :order}}]}
  :image   {:item  [{:type :ref
                      :ref {:ns :item}}]
            :url   [{}]}})

(def node-animal-plant
  {:node     {:key    [{:required true}]
              :value  [{}]
              :parent [{:type :ref
                        :ref {:ns :node
                              :rval :children}}]}
   :animal   {:name [{:required true}]}
   :plant    {:name [{:required true}]}})

(def bookstore
  {:book   {:name    [{:required true
                       :fulltext true}]
            :author  [{:fulltext true}]}

   :inventory {:count [{:type :long}]
               :cover [{:type :enum
                        :enum {:ns :cover.type
                               :values #{:hard :soft}}}]
               :book    [{:type :ref
                          :ref  {:ns :book}}]
               :store   [{:type :ref
                          :ref  {:ns :store}}]}
   :store  {:name    [{:required true
                       :fulltext true}]
            :address [{:type :ref
                       :ref  {:ns :address}}]}

   :address {:country [{:required true}]}})
