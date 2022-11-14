(ns std.scheduler.spawn-test
  (:use [code.test :exclude [run]])
  (:require [std.scheduler.spawn :refer :all]
            [std.scheduler.common :as common]
            [std.concurrent :as cc]
            [std.lib :as h]))

(fact:global
 {:component
  {|rt|  {:create nil
          :setup    (fn [_] (atom (common/new-runtime)))
          :teardown (fn [rt] (common/kill-runtime @rt))}
   |rt3| {:create nil
          :setup    (fn [_]
                      (doto (atom (common/new-runtime))
                        (run :test-program
                             {:type :basic
                              :main-fn identity
                              :interval 1000}
                             "s0")
                        (run :test-program
                             {:type :basic
                              :main-fn identity
                              :interval 1000}
                             "s1")
                        (run :test-program
                             {:type :basic
                              :main-fn identity
                              :interval 1000}
                             "s2")))
          :teardown (fn [rt] (common/kill-runtime @rt))}}})

^{:refer std.scheduler.spawn/spawn-status :added "3.0"}
(fact "returns the spawn status")

^{:refer std.scheduler.spawn/spawn-info :added "3.0"}
(fact "returns the spawn info")

^{:refer std.scheduler.spawn/spawn? :added "3.0"}
(fact "checks that object is a spawn")

^{:refer std.scheduler.spawn/create-spawn :added "3.0"}
(fact "creates a new spawn"

  (-> (create-spawn (merge *defaults* {:interval 1})
                    "spawn.1")
      (spawn-info))
  => {:id "spawn.1", :status :created, :duration "0ms",
      :jobs {:submitted 0 :succeeded 0 :errored 0 :waiting 0}, :state nil})

^{:refer std.scheduler.spawn/set-props :added "3.0"}
(fact "updates the spawn props"

  (-> (create-spawn)
      (set-props :submitted inc :errored 4 :started true)
      (select-keys [:submitted :errored :started]))
  => {:submitted 1, :errored 4, :started true})

^{:refer std.scheduler.spawn/get-props :added "3.0"}
(fact "gets the spawn prop given key"

  (-> (create-spawn)
      (get-props :submitted))
  => 0)

^{:refer std.scheduler.spawn/get-job :added "3.0"}
(fact "retrieves a job given key"

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"}))

      (get-job "j.1"))
  => {:id "j.1"})

^{:refer std.scheduler.spawn/update-job :added "3.0"}
(fact "updates a job given key" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"})
        (update-job "j.1" assoc :value 1))
      (get-job "j.1"))
  => {:id "j.1", :value 1})

^{:refer std.scheduler.spawn/remove-job :added "3.0"}
(fact "removes a job given key" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"})
        (add-job "j.2" {:id "j.2"})
        (remove-job "j.1"))
      (list-job-ids))
  => #{"j.2"})

^{:refer std.scheduler.spawn/add-job :added "3.0"}
(fact "adds job to the spawn" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"}))
      (list-job-ids))
  => #{"j.1"})

^{:refer std.scheduler.spawn/list-jobs :added "3.0"}
(fact "list all jobs" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"})
        (add-job "j.2" {:id "j.2"})
        (add-job "j.3" {:id "j.3"}))
      (list-jobs))
  => [{:id "j.1"} {:id "j.2"} {:id "j.3"}])

^{:refer std.scheduler.spawn/list-job-ids :added "3.0"}
(fact "lists all job ids" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"})
        (add-job "j.2" {:id "j.2"})
        (add-job "j.3" {:id "j.3"}))
      (list-job-ids))
  => #{"j.3" "j.1" "j.2"})

^{:refer std.scheduler.spawn/count-jobs :added "3.0"}
(fact "returns the number of jobs" ^:hidden

  (-> (doto (create-spawn)
        (add-job "j.1" {:id "j.1"})
        (add-job "j.2" {:id "j.2"})
        (add-job "j.3" {:id "j.3"}))
      (count-jobs))
  => 3)

^{:refer std.scheduler.spawn/send-result :added "3.0"}
(fact "sends the result to spawn output")

^{:refer std.scheduler.spawn/handler-run :added "3.0"}
(fact "handles the "

  (let [ret (h/incomplete)
        job {:id "j.0"
             :time 0
             :return ret}
        spawn (doto (create-spawn)
                (add-job "j.0" job))
        result {:task (handler-run *defaults* spawn job)}]
    (assoc result :return @ret :meta @(:output spawn)))
  => (contains-in {:task ["j.0" 0]
                   :return nil
                   :meta {:status :success,
                          :data nil, :exception nil,
                          :time 0,
                          :start integer?
                          :end integer?}}))

^{:refer std.scheduler.spawn/create-handler-basic :added "3.0"}
(fact "creates a basic handler")

^{:refer std.scheduler.spawn/create-handler-constant :added "3.0"}
(fact "creates a constant handler")

^{:refer std.scheduler.spawn/create-handler :added "3.0"}
(fact "creates a run handler"

  (create-handler (assoc *defaults* :type :basic)
                  (create-spawn))
  => fn?)

^{:refer std.scheduler.spawn/schedule-timing :added "3.0"}
(fact "calculates the timing for the next schedule"

  (schedule-timing :constant {:interval 1000} 500 635)
  => [1500 865])

^{:refer std.scheduler.spawn/wrap-schedule :added "3.0"}
(fact "wrapper for the schedule function")

^{:refer std.scheduler.spawn/spawn-save-past :added "3.0"}
(fact "helper function to move spawn from :running to :past")

^{:refer std.scheduler.spawn/spawn-loop :added "3.0"
  :use [|rt|]}
(fact "creates a spawn loop" ^:hidden

  (spawn-loop |rt|
              (assoc *defaults* :type :basic)
              (create-spawn))
  => fn?)

^{:refer std.scheduler.spawn/run :added "3.0"
  :use [|rt|]}
(fact "constructs and starts a spawn loop" ^:hidden

  (run |rt|
       :test-program
       {:type :basic
        :main-fn prn
        :interval 1000}
       "test")
  => spawn?)

^{:refer std.scheduler.spawn/get-all-spawn :added "3.0"
  :use [|rt|]}
(fact "returns all running spawns" ^:hidden

  (-> (doto |rt|
        (run :test-program
             {:type :basic
              :main-fn identity
              :interval 1000}
             "s0")
        (run :test-program
             {:type :basic
              :main-fn identity
              :interval 1000}
             "s1")
        (run :test-program
             {:type :basic
              :main-fn identity
              :interval 1000}
             "s2"))
      (get-all-spawn :test-program))
  => (contains {"s0" spawn?
                "s1" spawn?
                "s2" spawn?}))

^{:refer std.scheduler.spawn/get-spawn :added "3.0"
  :use [|rt|]
  :setup [(run |rt| :test-program
               {:type :basic
                :main-fn identity
                :interval 1000}
               "s0")]}
(fact "gets running spawn with id" ^:hidden

  (get-spawn |rt| :test-program "s0")
  => spawn?)

^{:refer std.scheduler.spawn/count-spawn :added "3.0"
  :use [|rt3|]}
(fact "counts all running spawns" ^:hidden

  (count-spawn |rt3| :test-program)
  => 3)

^{:refer std.scheduler.spawn/list-spawn :added "3.0"
  :use [|rt3|]}
(fact "lists all running spawns" ^:hidden

  (map :id (list-spawn |rt3| :test-program))
  => ["s0" "s1" "s2"])

^{:refer std.scheduler.spawn/latest-spawn :added "3.0"
  :use [|rt3|]}
(fact "returns latest created spawn" ^:hidden

  (:id (latest-spawn |rt3| :test-program))
  => "s2")

^{:refer std.scheduler.spawn/earliest-spawn :added "3.0"
  :use [|rt3|]}
(fact "returns earliest created spawn" ^:hidden

  (:id (earliest-spawn |rt3| :test-program))
  => "s0")

^{:refer std.scheduler.spawn/list-stopped :added "3.0"
  :use [|rt3|]}
(fact "lists all stopped spawns" ^:hidden

  (-> (doto |rt3|
        (stop-spawn :test-program "s1"))
      (list-stopped :test-program)
      (first)
      :id)
  => "s1")

^{:refer std.scheduler.spawn/latest-stopped :added "3.0"
  :use [|rt3|]}
(fact "returns the most recently stopped spawn" ^:hidden

  (-> (doto |rt3|
        (stop-spawn :test-program "s1")
        (stop-spawn :test-program "s0"))
      (latest-stopped :test-program)
      :id)
  => "s0")

^{:refer std.scheduler.spawn/latest :added "3.0"
  :use [|rt3|]}
(fact "returns the latest active or past spawn" ^:hidden

  (-> (doto |rt3|
        (stop-spawn :test-program "s0"))
      (latest :test-program)
      :id)
  => "s2"

  (-> (doto |rt3|
        (stop-spawn :test-program "s1"))
      (latest :test-program)
      :id)
  => "s2")

^{:refer std.scheduler.spawn/stop-spawn :added "3.0"}
(fact "stop a spawn and waits for jobs to finish")

^{:refer std.scheduler.spawn/kill-spawn :added "3.0"
  :use [|rt3|]}
(fact "stops a spawn and all jobs"

  (kill-spawn |rt3| :test-program "s1")
  => spawn?)

^{:refer std.scheduler.spawn/stop-all :added "3.0"
  :use [|rt3|]}
(fact "stops all the running tasks"

  (-> (doto |rt3|
        (stop-all :test-program))
      (count-spawn :test-program))
  => 0)

^{:refer std.scheduler.spawn/kill-all :added "3.0"
  :use [|rt3|]}
(fact "kills all the running tasks"

  (-> (doto |rt3|
        (kill-all :test-program))
      (count-spawn :test-program))
  => 0)

^{:refer std.scheduler.spawn/clear :added "3.0"}
(fact "clears the program and past spawn information")

^{:refer std.scheduler.spawn/get-state :added "3.0"
  :use [|rt3|]}
(fact "gets the global state for the program-id"

  (get-state |rt3| :test-program)
  => {})

^{:refer std.scheduler.spawn/get-program :added "3.0"
  :use [|rt3|]}
(fact "gets the program given runtime and program-id"

  (get-program |rt3| :test-program)
  => {})

(comment

  (h/tracked)
  (h/tracked [] :stop)

  (./import)
  (use 'jvm.tool)
  {:main-fn   (fn [state args t])
   :create-fn (fn [])
   :args-fn   (fn [])}

  (use 'jvm.tool)
  (./scaffold)

  ()
  (time (dotimes [i 10000] (h/flake)))
  (time (dotimes [i 10000] (h/uuid)))
  (time (dotimes [i 10000] (h/sid)))
  (time (h/uuid))
  (time (dotimes [i 10000] (System/currentTimeMillis)))

  (import 'hara.io.runtime.spawn.Counter)

  (defmutable Hello [state])

  ()

  (defn update! [a f & args]
    (cond (h/atom? a)
          (apply swap! a f args)

          (volatile? a)
          (vswap! a #(apply f % args))))

  (let [a (atom 0)]
    (time (dotimes [i 100000]
            (swap! a inc))))

  Counter
  (Counter. 0)
  (let [a (Counter. 0)]
    (time (dotimes [i 100000]
            (swap! a inc))))

  (def)

  (let [a []])

  (time
   (def -a- (reduce conj [] (range 100000))))

  (time (let [a (Counter. 0)]
          (dotimes [i 100000]
            (.inc a))))

  (time (let [a (Hello. 1)]
          (dotimes [i 100000]
            (h/mutable:set a :state i))))

  (time (let [a (volatile! [])]
          (dotimes [i 100000]
            (vswap! a conj i))))

  (time (let [a (atom [])]
          (dotimes [i 100000]
            (swap! a conj i))))

  (time (let [a (atom [])]
          (dotimes [i 100000]
            (swap! a conj i))))

  (let [a (cc/queue 0)]
    (time (dotimes [i 100000]
            (cc/put a i))))

  (let [a (volatile! 0)]
    (time (dotimes [i 100000]
            (vswap! a inc))))

  (let [a (atom 0)]
    (time (dotimes [i 100000]
            (swap! a inc))))

  (let [a (volatile! 0)
        b (atom 0)
        coll [a b]]
    (time (dotimes [i 100000]
            (update! (rand-nth coll) inc))))

  (let [a (volatile! 0)
        b (atom 0)
        coll [a b]]
    (time (dotimes [i 100000]
            (rand-nth coll))))

  (let [a (ref 0)]
    (time (dotimes [i 100000]
            (dosync (alter a inc)))))

  (let [a (ref 0)]
    (time (dosync
           (dotimes [i 100000]
             (alter a inc))))))
