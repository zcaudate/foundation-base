(ns lib.lucene.impl-test
  (:use code.test)
  (:require [lib.lucene.impl :refer :all]))

^{:refer lib.lucene.impl/create-directories :added "3.0"}
(fact "create multiple lucene directories")

^{:refer lib.lucene.impl/create-analyzers :added "3.0"}
(fact "creates multiple analyzers")

^{:refer lib.lucene.impl/start-lucene :added "3.0"}
(fact "starts the lucene engine")

^{:refer lib.lucene.impl/stop-lucene :added "3.0"}
(fact "stops the lucene engine")

^{:refer lib.lucene.impl/get-index :added "3.0"}
(fact "gets a particular index")

^{:refer lib.lucene.impl/index-add-lucene :added "3.0"}
(fact "adds an entry to the index")

^{:refer lib.lucene.impl/index-update-lucene :added "3.0"}
(fact "updates an entry in the index")

^{:refer lib.lucene.impl/index-remove-lucene :added "3.0"}
(fact "removes an entry from the index")

^{:refer lib.lucene.impl/search-lucene :added "3.0"}
(fact "searches lucene")

(comment
  (./import)
  (def -l-
    (-> {:type :lucenea :refresh false
         :store :disk
         :root  "search/root"
         :template {:topic    {:analyzer {:type :standard}}
                    :prospect {:custom {:id {:tokenized true
                                             :stored true
                                             :index #{:doc :freq}}}
                               :analyzer {:type :field
                                          :default {:type :standard
                                                    :stop-words []}
                                          :fields {}}}}}
        (assoc :instance (atom {}))
        (start-lucene)))

  (index-add-lucene -l- :topic {:id "hoeuoeu"
                                :data "stuff"} {})

  (map meta (search-lucene -l- :topic {:id "hello"} {}))

  (create-directories)

  (create-analyzers {:type :lucenea :refresh false
                     :store :disk
                     :root  "search/root"
                     :template {:topic    {:analyzer {:type :standard}}
                                :prospect {:custom {:id {:type :standard
                                                         :tokenized true
                                                         :stored true
                                                         :index #{:doc :freq}}}
                                           :analyzer {:type :field
                                                      :default {:type :standard
                                                                :stop-words []}
                                                      :fields {}}}}})

  (index-add engine :topic {:id   "hello"
                            :code "stuff"}
             {})

  (index-update engine
                {:topic {:id   "hello"}}
                {:topic {:id   "hello"}}
                {})

  (create {:type :lucenea :refresh false
           :store :disk
           :root  "search/root"
           :template {:topic    {:analyzer {:type :standard}}
                      :prospect {:custom {:id {:type :standard
                                               :tokenized true
                                               :stored true
                                               :index #{:doc :freq}}}
                                 :analyzer {:type :field
                                            :default :standard
                                            :fields {}}}}}))
