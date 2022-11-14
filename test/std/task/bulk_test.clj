(ns std.task.bulk-test
  (:use code.test)
  (:require [std.task.bulk :refer :all]))

^{:refer std.task.bulk/bulk-display :added "3.0"}
(fact "constructs bulk display options")

^{:refer std.task.bulk/bulk-process-item :added "3.0"}
(fact "bulk operation processing each item")

^{:refer std.task.bulk/bulk-items-parallel :added "3.0"}
(fact "bulk operation processing in parallel")

^{:refer std.task.bulk/bulk-items-single :added "3.0"}
(fact "bulk operation processing in single")

^{:refer std.task.bulk/bulk-items :added "3.0"}
(fact "processes each item given a input")

^{:refer std.task.bulk/bulk-warnings :added "3.0"}
(fact "outputs warnings that have been processed")

^{:refer std.task.bulk/bulk-errors :added "3.0"}
(fact "outputs errors that have been processed")

^{:refer std.task.bulk/prepare-columns :added "3.0"}
(fact "prepares columns for printing"

  (prepare-columns [{:key :name}
                    {:key :data}]
                   [{:name "Chris"
                     :data "1"}
                    {:name "Bob"
                     :data "100"}])
  => [{:key :name, :id :name, :length 7}
      {:key :data, :id :data, :length 5}])

^{:refer std.task.bulk/bulk-results :added "3.0"}
(fact "outputs results that have been processed")

^{:refer std.task.bulk/bulk-summary :added "3.0"}
(fact "outputs summary of processed results")

^{:refer std.task.bulk/bulk-package :added "3.0"}
(fact "packages results for return")

^{:refer std.task.bulk/bulk :added "3.0"}
(fact "process and output results for a group of inputs")

(comment
  (jvm.namespace/check [*ns*] {:return :items :package :vector})
  (jvm.namespace/check #{'code.manage} {:return :items :package :vector})
  (jvm.namespace/random-test ['code.manage] {:print {:item true :result true}})
  (jvm.namespace/list-aliases ['code.manage] {:print {:result true
                                                     :summary true}
                                             :sort-by :count
                                             :package :vector})
  (jvm.namespace/list-aliases 'code.manage {:package :map})
  (jvm.namespace/list-aliases 'code.manage {:package :map})
  (jvm.namespace/list-aliases ['code.manage] {:package :map}))
