(ns rt.postgres.script.graph-insert-test
  (:use code.test)
  (:require [rt.postgres.script.graph-insert :as insert]
            [rt.postgres.script.impl-base :as impl]
            [rt.postgres.grammar.common-application :as app]
            [rt.postgres.script.scratch :as scratch]
            [std.lib.schema :as schema]
            [std.lang :as l]
            [std.lib :as h]))

(l/script- :postgres
  {:require [[rt.postgres.script.scratch :as scratch]]
   :static {:application ["scratch"]
            :seed        ["scratch"]
            :all    {:schema   ["scratch"]}}})

^{:refer rt.postgres.script.graph-insert/insert-walk-ids :added "4.0"}
(fact "inserts walk ids to the entries"
  ^:hidden
  
  (let [output (volatile! #{})]
    (h/postwalk
     (fn [x]
       (if (not-empty (meta x))
         (vswap! output conj (meta x)))
       x)
     (insert/insert-walk-ids 
      '{:TaskCache
        {:id "hello",
         :tasks
         [{:name "task1", :status "pending", :cache "hello", :id ?id-00}
          {:name "task2",
           :status "pending",
           :cache "hello",
           :id ?id-01}]}}))
    @output)
  => #{#:walk{:id 2}
       #:walk{:id 4}
       #:walk{:id 1}
       #:walk{:id 0, :data true}
       #:walk{:id 3}})

^{:refer rt.postgres.script.graph-insert/insert-generate-graph-tree :added "4.0"}
(fact "generates a graph tree from nodes"
  ^:hidden
  
  (insert/insert-generate-graph-tree
   (insert/insert-walk-ids 
    '{:TaskCache
      {:id "hello",
       :tasks
       [{:name "task1", :status "pending", :cache "hello", :id ?id-00}
        {:name "task2",
         :status "pending",
         :cache "hello",
         :id ?id-01}]}}))
  => '[{:TaskCache
        {:id "hello",
         :tasks [{:name "task1",
                  :status "pending",
                  :cache "hello",
                  :id ?id-00}
                {:name "task2",
                 :status "pending",
                 :cache "hello",
                 :id ?id-01}]}}
       {3 {}, 4 {}, 2 [3 4], 1 {:tasks 2}}])

^{:refer rt.postgres.script.graph-insert/insert-associate-graph-data :added "4.0"}
(fact "associate nodes wit h graph data"
  ^:hidden
  
  (apply insert/insert-associate-graph-data
   (insert/insert-generate-graph-tree
    (insert/insert-walk-ids 
     '{:TaskCache
       {:id "hello",
        :tasks
        [{:name "task1", :status "pending", :cache "hello", :id ?id-00}
         {:name "task2",
          :status "pending",
          :cache "hello",
          :id ?id-01}]}})))
  => '[(3 4 2 1)
       {3 gid_3, 4 gid_4, 2 gid_2, 1 gid_1}
       {?id-00 3, ?id-01 4, "hello" 1}])

^{:refer rt.postgres.script.graph-insert/insert-gen-sql :added "4.0"}
(fact "generates sql given graph"
  ^:hidden
  
  (insert/insert-gen-sql
   '{:TaskCache
     {:id "hello",
      :tasks
      [{:name "task1", :status "pending", :cache "hello", :id ?id-00}
       {:name "task2",
        :status "pending",
        :cache "hello",
        :id ?id-01}]}}
   {:track 'o-op}
   (assoc (last (impl/prep-table 'scratch/TaskCache true (l/rt:macro-opts :postgres)))
          :application (app/app "scratch")))
  => h/form?)

^{:refer rt.postgres.script.graph-insert/insert-fn-raw :added "4.0"}
(fact "constructs insert form with prep")

^{:refer rt.postgres.script.graph-insert/insert-fn :added "4.0"}
(fact "constructs insert form"
  ^:hidden
  
  (l/with:macro-opts [(l/rt:macro-opts :postgres)]
    (insert/insert-fn 'scratch/TaskCache
                      {:id "hello"
                       :tasks [{:name "task1"
                                :status "pending"}
                               {:name "task2"
                                :status "pending"}]}
                      {:track 'o-op}))
  => '(let [(:uuid ?id-00)
          (rt.postgres/uuid-generate-v4)
          (:uuid ?id-01)
          (rt.postgres/uuid-generate-v4)
          gid_1
          [:with
           j-ret
           :as
           [:insert-into
            rt.postgres.script.scratch/TaskCache
            (>-<
             [#{"id"}
              #{"op_created"}
              #{"op_updated"}
              #{"time_created"}
              #{"time_updated"}])
            :values
            (>-<
             [(:uuid "hello")
              (:uuid (:->> o-op "id"))
              (:uuid (:->> o-op "id"))
              (:bigint (:->> o-op "time"))
              (:bigint (:->> o-op "time"))])
            :returning
            *]
           \\
           :select
           (to-jsonb j-ret)
           :from
           j-ret]
          gid_3
          [:with
           j-ret
           :as
           [:insert-into
            rt.postgres.script.scratch/Task
            (>-<
             [#{"id"}
              #{"status"}
              #{"name"}
              #{"cache_id"}
              #{"op_created"}
              #{"op_updated"}
              #{"time_created"}
              #{"time_updated"}])
            :values
            (>-<
             [(:uuid ?id-00)
              (++ "pending" rt.postgres.script.scratch/EnumStatus)
              (:text "task1")
              (:uuid "hello")
              (:uuid (:->> o-op "id"))
              (:uuid (:->> o-op "id"))
              (:bigint (:->> o-op "time"))
              (:bigint (:->> o-op "time"))])
            :returning
            *]
           \\
           :select
           (to-jsonb j-ret)
           :from
           j-ret]
          gid_4
          [:with
           j-ret
           :as
           [:insert-into
            rt.postgres.script.scratch/Task
            (>-<
             [#{"id"}
              #{"status"}
              #{"name"}
              #{"cache_id"}
              #{"op_created"}
              #{"op_updated"}
              #{"time_created"}
              #{"time_updated"}])
            :values
            (>-<
             [(:uuid ?id-01)
              (++ "pending" rt.postgres.script.scratch/EnumStatus)
              (:text "task2")
              (:uuid "hello")
              (:uuid (:->> o-op "id"))
              (:uuid (:->> o-op "id"))
              (:bigint (:->> o-op "time"))
              (:bigint (:->> o-op "time"))])
            :returning
            *]
           \\
           :select
           (to-jsonb j-ret)
           :from
           j-ret]]))
    

^{:refer rt.postgres.script.graph-insert/insert-fn.assign :adopt true :added "4.0"}
(fact "constructs insert form with assignment"
  ^:hidden

  ((-> (l/with:macro-opts [(l/rt:macro-opts :postgres)]
         (insert/insert-fn 'scratch/TaskCache
                           {:id "hello"
                            :tasks [{:name "task1"
                                     :status "pending"}
                                    {:name "task2"
                                     :status "pending"}]}
                           {:track 'o-op}))
       meta
       :assign/fn)
   'o-output)
  => '(let [(:uuid ?id-00)
           (rt.postgres/uuid-generate-v4)
           (:uuid ?id-01)
           (rt.postgres/uuid-generate-v4)
           gid_1
           [:with
            j-ret
            :as
            [:insert-into
             rt.postgres.script.scratch/TaskCache
             (>-<
              [#{"id"}
               #{"op_created"}
               #{"op_updated"}
               #{"time_created"}
               #{"time_updated"}])
             :values
             (>-<
              [(:uuid "hello")
               (:uuid (:->> o-op "id"))
               (:uuid (:->> o-op "id"))
               (:bigint (:->> o-op "time"))
               (:bigint (:->> o-op "time"))])
             :returning
             *]
            \\
            :select
            (to-jsonb j-ret)
            :from
            j-ret]
           gid_3
           [:with
            j-ret
            :as
            [:insert-into
             rt.postgres.script.scratch/Task
             (>-<
              [#{"id"}
               #{"status"}
               #{"name"}
               #{"cache_id"}
               #{"op_created"}
               #{"op_updated"}
               #{"time_created"}
               #{"time_updated"}])
             :values
             (>-<
              [(:uuid ?id-00)
               (++ "pending" rt.postgres.script.scratch/EnumStatus)
               (:text "task1")
               (:uuid "hello")
               (:uuid (:->> o-op "id"))
               (:uuid (:->> o-op "id"))
               (:bigint (:->> o-op "time"))
               (:bigint (:->> o-op "time"))])
             :returning
             *]
            \\
            :select
            (to-jsonb j-ret)
            :from
            j-ret]
           gid_4
           [:with
            j-ret
            :as
            [:insert-into
             rt.postgres.script.scratch/Task
             (>-<
              [#{"id"}
               #{"status"}
               #{"name"}
               #{"cache_id"}
               #{"op_created"}
               #{"op_updated"}
               #{"time_created"}
               #{"time_updated"}])
             :values
             (>-<
              [(:uuid ?id-01)
               (++ "pending" rt.postgres.script.scratch/EnumStatus)
               (:text "task2")
               (:uuid "hello")
               (:uuid (:->> o-op "id"))
               (:uuid (:->> o-op "id"))
               (:bigint (:->> o-op "time"))
               (:bigint (:->> o-op "time"))])
             :returning
             *]
            \\
            :select
            (to-jsonb j-ret)
            :from
            j-ret]
           gid_2
           (js [gid_3 gid_4])
           _ (:= gid_1 (|| gid_1 {:tasks gid_2}))]
        [:select gid_1 :into o-output]))
  
