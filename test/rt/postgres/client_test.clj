(ns rt.postgres.client-test
  (:use code.test)
  (:require [rt.postgres.client :as client]
            [std.lib :as h]
            [std.lang :as l]
            [rt.postgres.script.scratch :as scratch]))

^{:refer rt.postgres.client/setup-module :adopt true :added "4.0"
  :setup [(def -pg- (client/rt-postgres {:dbname "test-scratch"}))
          (def -out- (promise))]
  :teardown [(h/stop -pg-)]}
(fact "creates a postgres runtime"

  (l/rt:setup-single -pg- 'rt.postgres.script.scratch)
  (l/rt:teardown-single -pg- 'rt.postgres.script.scratch)
  (l/rt:teardown -pg- 'rt.postgres.script.scratch)
  (l/rt:setup -pg- 'rt.postgres.script.scratch))

^{:refer rt.postgres.client/rt-postgres:create :added "4.0"}
(fact "creates a postgres runtime")

^{:refer rt.postgres.client/rt-postgres :added "4.0"}
(fact "creates and startn a postgres runtime")

^{:refer rt.postgres.client/rt-add-notify :added "4.0"
  :setup [(def -pg- (client/rt-postgres {:dbname "test-scratch"}))
          (def -out- (promise))]
  :teardown [(h/stop -pg-)]}
(fact "adds a notification channel"
  ^:hidden
  
  (client/rt-add-notify -pg- "hello" {:channel "hello"
                                      :on-notify (fn [& args]
                                                   (deliver -out- (vec args)))})
  => vector?
  
  (keys @(:notifications -pg-))
  => '("hello")

  (do (h/p:rt-raw-eval -pg- "notify hello;")
      (deref -out- 1000 :timeout))
  => vector?

  (do (client/rt-remove-notify -pg- "hello")
      (client/rt-list-notify -pg-))
  => nil)

^{:refer rt.postgres.client/rt-remove-notify :added "4.0"}
(fact "removes a notification channel")

^{:refer rt.postgres.client/rt-list-notify :added "4.0"}
(fact "lists all notification channels")


