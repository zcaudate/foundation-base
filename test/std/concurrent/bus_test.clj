(ns std.concurrent.bus-test
  (:use [code.test :exclude [global]])
  (:require [std.concurrent.bus :refer :all]
            [std.concurrent :as cc])
  (:refer-clojure :exclude [send]))

^{:refer std.concurrent.bus/bus:get-thread :added "3.0"}
(fact "gets thread given an id"

  (bus:with-temp bus
                 (->> (bus:get-id bus)
                      (bus:get-thread bus)))
  => (cc/thread:current))

^{:refer std.concurrent.bus/bus:get-id :added "3.0"}
(fact "gets registered id given thread"

  (bus:with-temp bus
                 (bus:get-id bus))
  => string?)

^{:refer std.concurrent.bus/bus:has-id? :added "3.0"}
(fact "checks that the bus has a given id")

^{:refer std.concurrent.bus/bus:get-queue :added "3.0"}
(fact "gets the message queue associated with the thread"

  (bus:with-temp bus
                 (vec (bus:get-queue bus)))
  => [])

^{:refer std.concurrent.bus/bus:all-ids :added "3.0"}
(fact "returns all registered ids"

  (bus:with-temp bus
                 (bus:all-ids bus))
  => (contains #{string?}))

^{:refer std.concurrent.bus/bus:all-threads :added "3.0"}
(fact "returns all registered threads"

  (bus:with-temp bus
                 (= (first (vals (bus:all-threads bus)))
                    (Thread/currentThread)))
  => true)

^{:refer std.concurrent.bus/bus:get-count :added "3.0"}
(fact "returns the number of threads registered")

^{:refer std.concurrent.bus/bus:register :added "3.0"}
(fact "registers a thread to the bus")

^{:refer std.concurrent.bus/bus:deregister :added "3.0"}
(fact "deregisters from the bus")

^{:refer std.concurrent.bus/bus:send :added "3.0"}
(fact "sends a message to the given thread" ^:hidden

  (bus:with-temp bus
                 (bus:send bus (bus:get-id bus)
                           {:op :hello :message "world"})
                 (cc/take (bus:get-queue bus)))
  => (contains {:op :hello, :message "world", :id string?}))

^{:refer std.concurrent.bus/bus:wait :added "3.0"}
(fact "bus:waits on the message queue for message"

  (bus:with-temp bus
                 (bus:send bus (bus:get-id bus)
                           {:op :hello :message "world"})
                 (bus:wait bus))
  => (contains {:op :hello, :message "world", :id string?})

  (bus:with-temp bus
                 (bus:wait bus {:timeout 100})))

^{:refer std.concurrent.bus/handler-thunk :added "3.0"}
(fact "creates a thread loop for given message handler")

^{:refer std.concurrent.bus/run-handler :added "3.0"}
(fact "runs the handler in a thread loop")

^{:refer std.concurrent.bus/bus:send-all :added "3.0"}
(fact "bus:sends message to all thread queues")

^{:refer std.concurrent.bus/bus:open :added "3.0"}
(fact "bus:opens a new handler loop given function"

  (bus:with-temp bus
                 (let [{:keys [id]} @(bus:open bus (fn [m]
                                                     (update m :value inc)))]
                   (Thread/sleep 100)
                   @(bus:send bus id {:value 1})))
  => (contains {:value 2, :id string?}) ^:hidden

  (bus:with-temp bus
                 (let [{:keys [id stopped]} @(bus:open bus (fn [m]
                                                             (update m :value inc))
                                                       {:timeout 400})]
                   [@stopped (bus:get-count bus)]))
  => (contains-in [{:exit :timeout
                    :id string?
                    :start number?
                    :end number?}
                   1]))

^{:refer std.concurrent.bus/bus:close :added "3.0"}
(fact "bus:closes all bus:opened loops"

  (bus:with-temp bus
                 (let [{:keys [stopped id]} @(bus:open bus (fn [m]
                                                             (update m :value inc)))]
                   (Thread/sleep 10)
                   (bus:close bus id)
                   [@stopped (bus:get-count bus)]))
  => (contains-in [{:exit :normal,
                    :id string?
                    :unprocessed empty?
                    :start number?
                    :end number?}
                   1]))

^{:refer std.concurrent.bus/bus:close-all :added "3.0"}
(fact "stops all thread loops" ^:hidden

  (bus:with-temp bus
                 (let [_ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))]
                   (Thread/sleep 10)
                   (bus:close-all bus)
                   (Thread/sleep 100)
                   (bus:get-count bus)))
  => 1)

^{:refer std.concurrent.bus/bus:kill :added "3.0"}
(fact "bus:closes all bus:opened loops" ^:hidden

  (bus:with-temp bus
                 (let [{:keys [id stopped]} @(bus:open bus (fn [m]
                                                             (update m :value inc)))]
                   (Thread/sleep 10)
                   (bus:kill bus id)
                   [@stopped (bus:get-count bus)]))
  => (contains-in [{:exit :interrupted
                    :id string?
                    :unprocessed empty?
                    :start number?
                    :end number?}
                   1]))

^{:refer std.concurrent.bus/bus:kill-all :added "3.0"}
(fact "stops all thread loops" ^:hidden

  (bus:with-temp bus
                 (let [_ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))
                       _ @(bus:open bus (fn [m]
                                          (update m :value inc)))]
                   (Thread/sleep 10)
                   (bus:kill-all bus)
                   (Thread/sleep 10)
                   (bus:get-count bus)))
  => 1)

^{:refer std.concurrent.bus/main-thunk :added "3.0"}
(fact "creates main message return handler")

^{:refer std.concurrent.bus/main-loop :added "3.0"}
(fact "creates a new message return loop")

^{:refer std.concurrent.bus/started?-bus :added "3.0"}
(fact "checks if bus is running"

  (bus:with-temp bus
                 (Thread/sleep 10)
                 (started?-bus bus))
  => true)

^{:refer std.concurrent.bus/start-bus :added "3.0"}
(fact "starts the bus")

^{:refer std.concurrent.bus/stop-bus :added "3.0"}
(fact "stops the bus")

^{:refer std.concurrent.bus/info-bus :added "3.0"}
(fact "returns info about the bus")

^{:refer std.concurrent.bus/bus:create :added "3.0"}
(fact "creates a bus")

^{:refer std.concurrent.bus/bus :added "3.0"}
(fact "creates and starts a bus")

^{:refer std.concurrent.bus/bus? :added "3.0"}
(fact "checks if object is instance of Bus")

^{:refer std.concurrent.bus/bus:with-temp :added "3.0"
  :style/indent 1}
(fact "checks if object is instance of Bus")

^{:refer std.concurrent.bus/bus:reset-counters :added "4.0"}
(fact "resets the counters for a bus")

(comment

  (use 'jvm.tool)
  (./arrange)
  (./incomplete)
  (./import)

  (bus)
  (list-active)
  (bus:kill-active)

  (stop-bus *1)

  (stop-bus -bus1-)
  (def -bus1- (bus))
  (into {} -bus1-)

  (spawn -bus1- (fn [m]
                  (prn :PROCESSING m)
                  (thread/sleep 1000)
                  (prn :DONE m))
         {:on-stop (fn [] (prn :STOPPING))})

  (bus:send-all -bus1- {:hello 2}))
