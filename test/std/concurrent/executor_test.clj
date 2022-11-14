(ns std.concurrent.executor-test
  (:use code.test)
  (:require [std.concurrent.executor :refer :all]
            [std.concurrent.queue :as q]
            [std.lib.component.track :as track ]
            [std.lib :as h]))

^{:refer std.concurrent.executor/wrap-min-time :added "3.0"}
(fact "wraps a function with min-time and delay"

  ((wrap-min-time (fn []) 20 0))

  ((wrap-min-time (fn []) 100 10)))

^{:refer std.concurrent.executor/exec:queue :added "3.0"}
(fact "contructs a raw queue in different ways"

  (exec:queue)

  (exec:queue 1)

  (exec:queue {:size 1})

  (exec:queue {})

  (exec:queue (q/queue)))

^{:refer std.concurrent.executor/executor:single :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop 2)]}
(fact "constructs a single executor"

  ;; any sized pool
  (executor:single)

  ;; fixed pool
  (executor:single {:size 10}))

^{:refer std.concurrent.executor/executor:pool :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "constructs a pool executor"

  (executor:pool 10 10 1000 {:size 10}))

^{:refer std.concurrent.executor/executor:cached :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "creates a cached executor"

  (executor:cached))

^{:refer std.concurrent.executor/exec:shutdown :added "3.0"}
(fact "shuts down executor"
  ^:hidden

  (-> (executor:single)
      (doto (submit (fn [] (Thread/sleep 1000))))
      (doto (submit (fn [] (Thread/sleep 1000))))
      (doto (exec:shutdown)))
  => (all exec:shutdown?
          exec:terminating?
          (comp not exec:terminated?)))

^{:refer std.concurrent.executor/exec:shutdown-now :added "3.0"}
(fact "shuts down executor immediately"
  ^:hidden

  (-> (executor:single)
      (doto (submit (fn [] (Thread/sleep 1000))))
      (doto (submit (fn [] (Thread/sleep 1000))))
      (exec:shutdown-now)))

^{:refer std.concurrent.executor/exec:get-queue :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "gets the queue from the executor"

  (-> (executor:pool 10 10 1000 (q/queue))
      (exec:get-queue)))

^{:refer std.concurrent.executor/submit :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop 2)]}
(fact "submits a task to an executor"

  @(submit (executor:single)
           (fn [])
           {:min 100}) ^hidden

  @(submit (executor:single)
           (fn []
             (Thread/sleep 1000))
           {:max 100})
  => (throws))

^{:refer std.concurrent.executor/submit-notify :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "submits a task (generally to a fixed size queue)"

  (doto (executor:single 1)
    (submit-notify (fn [])
                   1000)
    (submit-notify (fn [])
                   1000)
    (submit-notify (fn [])
                   1000)))

^{:refer std.concurrent.executor/executor:scheduled :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "constructs a scheduled executor"

  (executor:scheduled 10))

^{:refer std.concurrent.executor/schedule :added "3.0"}
(fact "schedules task for execution" ^:hidden

  (h/-> (doto (executor:scheduled 1)
          (schedule (fn [] (Thread/sleep 10)) 10)
          (schedule (fn [] (Thread/sleep 10)) 20)
          (schedule (fn [] (Thread/sleep 10)) 30)
          (schedule (fn [] (Thread/sleep 10)) 40))
        (do (Thread/sleep 100)
            (exec:current-completed %)))
  => 4)

^{:refer std.concurrent.executor/schedule:fixed-rate :added "3.0"}
(fact "schedules task at a fixed rate" ^:hidden

  (def -counter- (atom 0))

  (h/-> (doto (executor:scheduled 1)
          (schedule:fixed-rate (fn [] (Thread/sleep 20)) 30))
        (do (Thread/sleep 100)
            (exec:shutdown-now %)
            (exec:current-completed %)))
  => #(<= 3 %))

^{:refer std.concurrent.executor/schedule:fixed-delay :added "3.0"}
(fact "schedules task at fixed delay" ^:hidden

  (def -counter- (atom 0))

  (h/-> (doto (executor:scheduled 1)
          (schedule:fixed-delay (fn [] (Thread/sleep 20)) 30))
        (do (Thread/sleep 100)
            (exec:shutdown-now %)
            (exec:current-completed %)))
  => #(<= 1 %))

^{:refer std.concurrent.executor/exec:await-termination :added "3.0"}
(fact "await termination for executor service" ^:hidden

  ;; Shutdown
  (def -counter- (atom 0))

  (-> (executor:single)
      (doto (submit (fn [] (Thread/sleep 100) (swap! -counter- inc))))
      (doto (submit (fn [] (Thread/sleep 100) (swap! -counter- inc))))
      (exec:shutdown)
      (doto (exec:await-termination)))

  @-counter-
  => 2

  ;; Shutdown now
  (def -counter- (atom 0))

  (-> (executor:single)
      (doto (submit (fn [] (Thread/sleep 100) (swap! -counter- inc))))
      (doto (submit (fn [] (Thread/sleep 100) (swap! -counter- inc))))
      (exec:shutdown-now)
      (doto (exec:await-termination)))

  @-counter-
  => 0)

^{:refer std.concurrent.executor/exec:shutdown? :added "3.0"}
(fact "checks if executor is shutdown")

^{:refer std.concurrent.executor/exec:terminated? :added "3.0"}
(fact "checks if executor is shutdown and all threads have finished")

^{:refer std.concurrent.executor/exec:terminating? :added "3.0"}
(fact "check that executor is terminating")

^{:refer std.concurrent.executor/exec:current-size :added "3.0"}
(fact "returns number of threads in pool")

^{:refer std.concurrent.executor/exec:current-active :added "3.0"}
(fact "returns number of active threads in pool")

^{:refer std.concurrent.executor/exec:current-submitted :added "3.0"}
(fact "returns number of submitted tasks")

^{:refer std.concurrent.executor/exec:current-completed :added "3.0"}
(fact "returns number of completed tasks")

^{:refer std.concurrent.executor/exec:pool-size :added "3.0"}
(fact "gets and sets the core pool size")

^{:refer std.concurrent.executor/exec:pool-max :added "3.0"}
(fact "gets and sets the core pool max")

^{:refer std.concurrent.executor/exec:keep-alive :added "3.0"}
(fact "gets and sets the keep alive time")

^{:refer std.concurrent.executor/exec:rejected-handler :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop 2)]}
(fact "sets the rejected task handler" ^:hidden

  (def -counter- (atom 0))
  (doto (executor:single 1)
    (exec:rejected-handler (fn [_ _] (swap! -counter- inc)))
    (h/-> (doseq [i (range 10)]
            (submit % (fn [] (Thread/sleep 1000))))))

  (<= 8 @-counter-)
  => true

  (-> (executor:single)
      (exec:rejected-handler))
  => anything)

^{:refer std.concurrent.executor/exec:at-capacity? :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "checks if executor is at capacity" ^:hidden

  (-> (doto (executor:single 1)
        (h/-> (try (submit % (fn [] (Thread/sleep 1000)))
                   (catch Throwable t))))
      (exec:at-capacity?))
  => boolean?)

^{:refer std.concurrent.executor/exec:increase-capacity :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "increases the capacity of the executor" ^:hidden

  (-> (executor:single)
      (doto (exec:increase-capacity))
      (exec:pool-size))
  => 2)

^{:refer std.concurrent.executor/executor:type :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "returns executor service type" ^:hidden

  (-> (executor:single)
      (executor:type))
  => :single)

^{:refer std.concurrent.executor/executor:info :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "returns executor service info" ^:hidden

  (-> (executor:single)
      (executor:info))
  => {:type :single,
      :running true,
      :current {:threads 0, :active 0, :queued 0, :terminated false},
      :counter {:submit 0, :complete 0},
      :options {:pool {:size 0, :max 1, :keep-alive 0},
                :queue {:remaining 2147483647, :total 0}}})

^{:refer std.concurrent.executor/executor:props :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop)]}
(fact "returns props for getters and setters" ^:hidden

  (-> (executor:single)
      (executor:props))
  => {:pool {:size {:get exec:pool-size
                    :set exec:pool-size},
             :max {:get exec:pool-max
                   :set exec:pool-max},
             :keep-alive {:get exec:keep-alive
                          :set exec:keep-alive}}
      :rejected-handler {:get exec:rejected-handler
                         :set exec:rejected-handler}})

^{:refer std.concurrent.executor/executor:health :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop 2)]}
(fact "returns health of the executor" ^:hidden

  (-> (executor:single)
      (executor:health))
  => {:status :ok}

  (-> (executor:single)
      (exec:shutdown)
      (executor:health))
  => {:status :not-healthy})

^{:refer std.concurrent.executor/executor :added "3.0"
  :teardown [(track/tracked:last [:raw :executor] :stop 2)]}
(fact "creates an executor"

  (executor {:type :pool
             :size 3
             :max 3
             :keep-alive 1000}) ^:hidden

  (executor {:type :single
             :size 1}))

^{:refer std.concurrent.executor/executor:shared :added "3.0"}
(fact "lists all shared executors"

  (-> (executor:shared) keys sort)
  => [:async :default :pooled])

^{:refer std.concurrent.executor/executor:share :added "3.0"}
(fact "registers a shared executor")

^{:refer std.concurrent.executor/executor:unshare :added "3.0"}
(fact "deregisters a shared executor")
