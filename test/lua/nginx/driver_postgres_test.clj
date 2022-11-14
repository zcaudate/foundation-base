(ns lua.nginx.driver-postgres-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.json :as json]
            [rt.postgres]))

(l/script- :postgres
  {:runtime :jdbc.client
   :config  {:rt/id :test.scratch
             :dbname "test-scratch"
             :temp :create}
   :require [[rt.postgres :as pg]
             [rt.postgres.script.scratch :as scratch]]})

(l/script- :lua
  {:runtime :basic
   :config {:program :resty}
   :require [[xt.lang.base-lib :as k]
             [lua.nginx.driver-postgres :as lua-postgres]
             [xt.sys.conn-dbsql :as driver]]})

(fact:global
 {:setup    [(l/rt:restart)
             (l/rt:teardown :postgres)
             (l/rt:setup :postgres)]
  :teardown [(l/rt:stop)]})

^{:refer lua.nginx.driver-postgres/default-env :added "4.0"}
(fact "gets the default env"
  ^hidden
  
  (lua-postgres/default-env)
  => map?)

^{:refer lua.nginx.driver-postgres/default-env-set :added "4.0"}
(fact "sets the default env")

^{:refer lua.nginx.driver-postgres/db-error :added "4.0"}
(fact "parses the error into table"
  ^:hidden
  
  (!.lua
   (lua-postgres/db-error "SERROR" true))
  => {"debug" {"severity" "ERROR"}})

^{:refer lua.nginx.driver-postgres/raw-query :added "4.0"}
(fact "Performs a raw query")

^{:refer lua.nginx.driver-postgres/connect-constructor :added "4.0"}
(fact "create db connection"
  ^:hidden

  ;; ENCODING TRUE INTEGER
  (!.lua
   (local conn (lua-postgres/connect-constructor {:database "test-scratch"}))
   (driver/query conn (@! (l/emit-as
                       :postgres [`[:select (scratch/addf 1 2)]]))))
  => 3
  
  ;; ENCODING FALSE STRING
  (!.lua
   (local conn (lua-postgres/connect-constructor {:database "test-scratch"}))
   (driver/query conn (@! (l/emit-as
                      :postgres [`[:select (scratch/ping)]]))))
  => "pong"

  ;; ENCODING FALSE JSON
  (!.lua
   (local conn (lua-postgres/connect-constructor {:database "test-scratch"}))
   (driver/query conn (@! (l/emit-as
                       :postgres ['[:select (% {:a 1 :b 2})]]))))
  => "{\"a\": 1, \"b\": 2}"
  
  ;; FUNCTION ERROR
  (!.lua
   (local conn (lua-postgres/connect-constructor {:database "test-scratch"}))
   (driver/query conn (@! (l/emit-as
                           :postgres [`[:select (scratch/ping 1)]]))
                 {:error  (fn [err]
                            (return {:status "error"
                                     :data err}))}))
  => (contains-in
      {"status" "error", "data" {"debug" {"message" "function scratch.ping(integer) does not exist"}}}))
