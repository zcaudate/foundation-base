(ns std.dispatch.hooks-test
  (:use code.test)
  (:require [std.dispatch.hooks :refer :all]
            [std.concurrent :as cc]))

^{:refer std.dispatch.hooks/counter :added "3.0"}
(fact "creates the executor counter"

  (counter))

^{:refer std.dispatch.hooks/inc-counter :added "3.0"}
(fact "increment the executor counter")

^{:refer std.dispatch.hooks/update-counter :added "3.0"}
(fact "updates the executor counter")

^{:refer std.dispatch.hooks/handle-entry :added "3.0"}
(fact "processes the hook on each stage")

^{:refer std.dispatch.hooks/on-submit :added "3.0"}
(fact "helper for the submit stage")

^{:refer std.dispatch.hooks/on-queued :added "3.0"}
(fact "helper for the queued stage")

^{:refer std.dispatch.hooks/on-batch :added "3.0"}
(fact "helper for the on-batch stage")

^{:refer std.dispatch.hooks/on-process :added "3.0"}
(fact "helper for the process stage")

^{:refer std.dispatch.hooks/on-process-bulk :added "3.0"}
(fact "helper for the process stage")

^{:refer std.dispatch.hooks/on-skip :added "3.0"}
(fact "helper for the skip stage")

^{:refer std.dispatch.hooks/on-poll :added "3.0"}
(fact "helper for the poll stage")

^{:refer std.dispatch.hooks/on-error :added "3.0"}
(fact "helper for the error stage")

^{:refer std.dispatch.hooks/on-complete :added "3.0"}
(fact "helper for the complete stage")

^{:refer std.dispatch.hooks/on-complete-bulk :added "3.0"}
(fact "helper for the complete stage")

^{:refer std.dispatch.hooks/on-shutdown :added "3.0"}
(fact "helper for the shutdown stage")

^{:refer std.dispatch.hooks/on-startup :added "3.0"}
(fact "helper for the startup stage")

