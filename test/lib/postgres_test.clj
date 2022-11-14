(ns lib.postgres-test
  (:use code.test)
  (:require [lib.postgres :as base]
            [lib.postgres.connection :as conn]
            [rt.postgres.client :as client]
            [rt.postgres.script.builtin :as builtin]
            [rt.postgres.script.addon :as addon]
            [rt.postgres.script.scratch :as scratch]
            [std.lang.base.util :as ut]
            [lib.jdbc :as jdbc]
            [std.lib :as h]))

^{:refer lib.postgres/start-pg-temp-init :added "4.0"}
(fact "initialises a temp database"
  ^:hidden
  
  (base/start-pg-temp-init {:dbname "test"})
  => nil)

^{:refer lib.postgres/wait-for-pg :added "4.0"}
(fact "waits for the postgres database to come online")

^{:refer lib.postgres/start-pg-raw :added "4.0"}
(fact "starts the database"
  ^:hidden
  
  (def -pg- (base/start-pg-raw (client/rt-postgres:create {:dbname "test"})))
  
  (h/stop -pg-))

^{:refer lib.postgres/stop-pg-temp-teardown :added "4.0"}
(fact "tears down a temp database"
  ^:hidden
  
  (do (base/start-pg-temp-init {:dbname "test-temp-db"})
      (base/stop-pg-temp-teardown {:dbname "test-temp-db"}))
  => -1)

^{:refer lib.postgres/stop-pg-raw :added "4.0"}
(fact "stops the postgres runtime")

^{:refer lib.postgres/raw-eval-pg-return :added "4.0"}
(fact "returns a regularised result"
  ^:hidden
  
  (base/raw-eval-pg-return [{:pg_jit_available true}])
  => true

  (base/raw-eval-pg-return [{:result "{\"a\": 1}"}])
  => {:a 1})

^{:refer lib.postgres/raw-eval-pg :added "4.0"
  :setup [(def -pg- (client/rt-postgres {:dbname "test"}))]
  :teardown (h/stop -pg-)}
(fact "executes a raw value"
  ^:hidden
  
  (binding [conn/*execute* jdbc/fetch]
    (base/raw-eval-pg -pg- "select 1;"))
  => [{:?column? 1}])

^{:refer lib.postgres/init-ptr-pg :added "4.0"
  :setup [(def -pg- (client/rt-postgres {:dbname "test"
                                         :mode :dev}))]
  :teardown (h/stop -pg-)}
(fact "initiates a pointer in the runtime"

  (base/init-ptr-pg -pg- scratch/addf)
  => -2)

^{:refer lib.postgres/prepend-select-check-form :added "4.0"}
(fact "checks if form needs a `SELECT` prepended"
  ^:hidden
  
  (base/prepend-select-check builtin/acos
                             [])
  => true
  
  (base/prepend-select-check addon/b:insert
                             [])
  => false
  
  (base/prepend-select-check (ut/lang-pointer :postgres)
                             [1])
  => true
  
  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ["hello"])
  => true
  
  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(:int 0.5)])
  => true
  
  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(++ 0.5 :int)])
  => true
  
  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(if a 1 2)])
  => false

  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(do:block 1 2 3)])
  => false

  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(do:assert 1 2 3)])
  => false

  (base/prepend-select-check (ut/lang-pointer :postgres)
                             ['(do:run 1 2 3)])
  => nil

  (base/prepend-select-check (ut/lang-pointer :postgres)
                             [[:anything]])
  => false)

^{:refer lib.postgres/prepend-select-check :added "4.0"}
(fact "checks if values needs a `SELECT` prepended"
  ^:hidden
  
  (base/prepend-select-check builtin/cot [40])
  => true)

^{:refer lib.postgres/invoke-ptr-pg :added "4.0"
  :setup [(def -pg- (client/rt-postgres {:dbname "test"}))]
  :teardown (h/stop -pg-)}
(fact "invokes a pointer in runtime"
  ^:hidden
  
  (base/invoke-ptr-pg -pg- builtin/cot [40])
  => -0.8950829176379128)
