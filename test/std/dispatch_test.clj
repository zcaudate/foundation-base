(ns std.dispatch-test
  (:use code.test)
  (:require [std.dispatch :refer :all]))

^{:refer std.dispatch/dispatch? :added "3.0"}
(fact "checks if object is an dispatch")

^{:refer std.dispatch/submit :added "3.0"}
(fact "submits entry to an dispatch")

^{:refer std.dispatch/create :added "3.0"}
(fact "creates a component compatible dispatch")

^{:refer std.dispatch/dispatch :added "3.0"}
(fact "creates and starts an dispatch")

(comment
  (require '[std.dispatch.debounce-test :as debounce.test])
  (meta #'create)

  (create {})

  (create debounce.test/+test-config+))
