(ns lib.postgres-test
  (:use code.test)
  (:require [lib.postgres :as base]
            [lib.postgres.connection :as conn]
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
(fact "starts the database")

^{:refer lib.postgres/stop-pg-temp-teardown :added "4.0"}
(fact "tears down a temp database"
  ^:hidden
  
  (do (base/start-pg-temp-init {:dbname "test-temp-db"})
      (base/stop-pg-temp-teardown {:dbname "test-temp-db"}))
  => -1)

^{:refer lib.postgres/stop-pg-raw :added "4.0"}
(fact "stops the postgres runtime")
