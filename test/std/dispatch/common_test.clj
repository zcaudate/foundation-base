(ns std.dispatch.common-test
  (:use code.test)
  (:require [std.dispatch.common :refer :all]
            [std.concurrent :as cc]
            [std.lib :as h]))

(fact:global
 {:component
  {|dispatch| {:create (create-map {:options {:pool {:size 1}}})
               :setup start-dispatch
               :teardown stop-dispatch}}})

^{:refer std.dispatch.common/to-string :added "3.0"}
(fact "returns the executorstring")

^{:refer std.dispatch.common/info-base :added "3.0"}
(fact "returns base executor info")

^{:refer std.dispatch.common/create-map :added "3.0"}
(fact "creates the base executor map"

  (create-map {:options {:pool {:size 1}}})
  => (contains {:options {:pool {:keep-alive 1000,
                                 :size 1,
                                 :max 1}},
                :runtime map?}))

^{:refer std.dispatch.common/handle-fn :added "3.0"}
(fact "generic handle function for entry"

  (let [thunk (handle-fn (-> {:id :hello
                              :handler (fn [{:keys [id]} entry]
                                         {:id id :entry entry})}
                             create-map)
                         {:a 1})]
    (thunk))
  => {:id :hello, :entry {:a 1}})

^{:refer std.dispatch.common/await-termination :added "3.0"}
(fact "generic await termination function for executor")

^{:refer std.dispatch.common/start-dispatch :added "3.0"}
(fact "generic start function for executor")

^{:refer std.dispatch.common/stop-dispatch :added "3.0"}
(fact "generic stop function for executor")

^{:refer std.dispatch.common/kill-dispatch :added "3.0"}
(fact "generic force kill function for executor")

^{:refer std.dispatch.common/started?-dispatch :added "3.0"}
(fact "checks if executor has started")

^{:refer std.dispatch.common/stopped?-dispatch :added "3.0"}
(fact "checks if executor has stopped")

^{:refer std.dispatch.common/info-dispatch :added "3.0"
  :use [|dispatch|]}
(fact "returns generic executor info"

  (info-dispatch |dispatch|)
  => {:type nil, :running true,
      :counter {:submit 0, :queued 0, :process 0, :complete 0, :error 0},
      :options {:pool {:keep-alive 1000, :size 1, :max 1}},
      :current {:threads 0, :active 0, :queued 0, :terminated false}})

^{:refer std.dispatch.common/health-dispatch :added "3.0"}
(fact "returns the health of the executor")

^{:refer std.dispatch.common/remote?-dispatch :added "3.0"}
(fact "returns whether executor is remote")

^{:refer std.dispatch.common/props-dispatch :added "3.0"}
(fact "returns the props of the executor")

^{:refer std.dispatch.common/check-hooks :added "3.0"}
(fact "Checks that hooks conform to arguments")

(comment
  (./import)

  (std.lib/tracked [] :stop))
