(ns std.scheduler.common-test
  (:use code.test)
  (:require [std.scheduler.common :refer :all]))

(declare -rt-)

^{:refer std.scheduler.common/new-runtime :added "3.0"
  :teardown [(kill-runtime -rt-)]}
(fact "contructs a new runtime for runner"

  (def -rt- (new-runtime)))

^{:refer std.scheduler.common/stop-runtime :added "3.0"}
(fact "stops the executors in the new instance"

  (stop-runtime (new-runtime)))

^{:refer std.scheduler.common/kill-runtime :added "3.0"}
(fact "kills all objects in the runtime"

  (kill-runtime (new-runtime)))

^{:refer std.scheduler.common/all-ids :added "3.0"}
(fact "returns all running program ids")

^{:refer std.scheduler.common/spawn-form :added "3.0"}
(fact "generate a spawn/runtime form")

^{:refer std.scheduler.common/gen-spawn :added "3.0"}
(fact "generates a spawn/runtime forms")

^{:refer std.scheduler.common/spawn-all-form :added "3.0"}
(fact "generates all forms")

^{:refer std.scheduler.common/gen-spawn-all :added "3.0"}
(fact "generates all spawn/runiime forms")

