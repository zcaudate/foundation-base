(ns rt.postgres.system-test
  (:use code.test)
  (:require [std.lang :as l]
            [rt.postgres.system :as sys]))

(l/script- :postgres
  {:runtime :jdbc.client
   :config  {:dbname "test-scratch"}
   :require [[rt.postgres.system :as sys]]})

^{:refer rt.postgres.system/CANARY :adopt true :added "4.0"}
(fact "creates a pg template"

  (sys/jit-available)
  => boolean?

  (l/with:raw
   (sys/jit-available))
  => (contains-in [{:pg_jit_available boolean?}]))

^{:refer rt.postgres.system/pg-tmpl :added "4.0"}
(fact "creates a pg template"

  (sys/pg-tmpl 'hello)
  => '(def$.pg hello pg-hello))
