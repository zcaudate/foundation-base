(ns std.concurrent.atom-test
  (:use code.test)
  (:require [std.concurrent.atom :refer :all]
            [std.lib.future :as f]
            [std.lib :as h]))

^{:refer std.concurrent.atom/aq:new :added "3.0"}
(fact "creates an atom with a vec as queue")

^{:refer std.concurrent.atom/aq:process :added "3.0"}
(fact "processes "

  (def +state+ (atom []))
  
  (aq:process (fn [elems] (swap! +state+ conj elems))
              (atom [1 2 3 4 5]) 3)

  @+state+
  => [[1 2 3] [4 5]])

^{:refer std.concurrent.atom/aq:submit :added "3.0"}
(fact "submission function for one or multiple entries to aq")

^{:refer std.concurrent.atom/aq:executor :added "3.0"}
(fact "creates a executor that takes in an atom queue")

^{:refer std.concurrent.atom/hub-state :added "4.0"}
(fact "creates a hub state")

^{:refer std.concurrent.atom/hub:new :added "3.0"}
(fact "creates a trackable atom queue")

^{:refer std.concurrent.atom/hub:process :added "3.0"}
(fact "like aq:process but with a hub"

  (def +state+ (atom []))
  
  (hub:process (fn [elems] (swap! +state+ conj elems))
               (hub:new [1 2 3 4 5]) 3)
  
  @+state+
  => [[1 2 3] [4 5]])

^{:refer std.concurrent.atom/hub:add-entries :added "3.0"}
(fact "adds entries to the hub"

  (hub:add-entries (hub:new) [1 2 3 4 5])
  => (contains [f/future? 0 5]))

^{:refer std.concurrent.atom/hub:submit :added "3.0"}
(fact "submission function for the hub")

^{:refer std.concurrent.atom/hub:executor :added "3.0"}
(fact "creates a hub based executor"
  
  (def -exe- (hub:executor nil {:handler (fn [& args]
                                           args)
                                :interval 50
                                :max-batch 1000}))
  
  (do (def -res- ((:submit -exe-) 1 2 3 4 5 6))
      @(first -res-))
  => '[(nil (1 2 3 4 5 6))]

  (hub:wait (:queue -exe-))
  => nil)

^{:refer std.concurrent.atom/hub:wait :added "4.0"}
(fact "waits for the hub executor to be ready")
