(ns lib.aether.listener-test
  (:use code.test)
  (:require [lib.aether.listener :refer :all]))

^{:refer lib.aether.listener/event->rep :added "3.0"}
(fact "converts the event to a map representation")

^{:refer lib.aether.listener/record :added "3.0"}
(fact "adds an event to the recorder")

^{:refer lib.aether.listener/aggregate :added "3.0"}
(fact "summarises all events that have been processed")

^{:refer lib.aether.listener/process-event :added "3.0"}
(fact "processes a recorded event")
