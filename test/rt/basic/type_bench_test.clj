(ns rt.basic.type-bench-test
  (:use code.test)
  (:require [rt.basic.type-bench :refer :all]
            [std.lib :as h]
            [std.fs :as fs]))

^{:refer rt.basic.type-bench/bench? :added "4.0"}
(fact "checks if object is a bench")

^{:refer rt.basic.type-bench/get-bench :added "4.0"}
(fact "gets an active bench given port")

^{:refer rt.basic.type-bench/create-bench-process :added "4.0"}
(fact "creates the bench process"
  ^:hidden
  
  (create-bench-process
   :python (h/port:check-available 0)
   {:root-dir (str (fs/create-tmpdir))}
   ["python" "-c"]
   "1+1")
  => bench?)

^{:refer rt.basic.type-bench/start-bench-process :added "4.0"}
(fact "starts a bench process"
  ^:hidden
  
  (start-bench-process :python
                       {:exec ["python" "-c"]
                        :bootstrap (fn [port opts]
                                     "1+1")}
                       0
                       {:root-dir (str (fs/create-tmpdir))})
  => bench?

  (stop-bench-process 0)
  => bench?)

^{:refer rt.basic.type-bench/stop-bench-process :added "4.0"}
(fact "stops the bench process")

^{:refer rt.basic.type-bench/start-bench :added "4.0"}
(fact "starts a test bench process")

^{:refer rt.basic.type-bench/stop-bench :added "4.0"}
(fact "stops a test bench process")