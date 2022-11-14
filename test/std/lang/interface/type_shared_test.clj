(ns std.lang.interface.type-shared-test
  (:use code.test)
  (:require [std.lang.interface.type-shared :as shared]
            [std.lang :as l]))

^{:refer std.lang.interface.type-shared/get-groups :added "4.0"}
(fact "gets all shared groups"

  (shared/get-groups)
  ;; (:hara/rt.postgres :hara/rt.redis :hara/rt.nginx :hara/rt.cpython.shared :hara/rt.luajit.shared)
  => vector?)

^{:refer std.lang.interface.type-shared/get-group-count :added "4.0"}
(fact "gets the group count for a type and id"

  (shared/get-group-count :hara/rt.redis)
  ;; {:default 21, :test 2}
  => map?)

^{:refer std.lang.interface.type-shared/update-group-count :added "4.0"}
(fact "updates the group counte")

^{:refer std.lang.interface.type-shared/get-group-instance :added "4.0"}
(fact "gets the group instance")

^{:refer std.lang.interface.type-shared/set-group-instance :added "4.0"}
(fact "sets the group instance")

^{:refer std.lang.interface.type-shared/update-group-instance :added "4.0"}
(fact "updates the group instance")

^{:refer std.lang.interface.type-shared/restart-group-instance :added "4.0"}
(fact "restarts the group instance")

^{:refer std.lang.interface.type-shared/remove-group-instance :added "4.0"}
(fact "removes the group instance")

^{:refer std.lang.interface.type-shared/start-shared :added "4.0"}
(fact "starts a shared runtime client")

^{:refer std.lang.interface.type-shared/stop-shared :added "4.0"}
(fact "stops a shared runtime client")

^{:refer std.lang.interface.type-shared/rt-shared:create :added "4.0"}
(fact "creates a shared runtime client")

^{:refer std.lang.interface.type-shared/rt-shared :added "4.0"}
(fact "creates and starts and shared runtime client")

^{:refer std.lang.interface.type-shared/rt-is-shared? :added "4.0"}
(fact "checks if a runtime is shared")

^{:refer std.lang.interface.type-shared/rt-get-inner :added "4.0"}
(fact "gets the inner runtime")

(comment
  
  (rt-is-shared? (rt-shared:create {}))
  
  (require '[rt.basic.impl.script :as script]))
