(ns kmi.queue.aaa-test
  (:use code.test)
  (:require [std.lang :as  l]
            [std.lib :as h]
            [rt.redis]))

;; This is run before kmi.queue.common-test in order to
;; not get a connection error for the namespace

(l/script- :lua
  {:runtime :redis.client
   :config {:port 17003
            :bench true}
   :require [[xt.lang.base-lib :as k :include [:fn]]
             [kmi.redis :as r]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

