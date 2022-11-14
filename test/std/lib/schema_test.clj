(ns std.lib.schema-test
  (:use code.test)
  (:require [std.lib.schema :as schema]
            [rt.postgres.grammer.common-application :as app]
            [rt.postgres.script.scratch :as scratch]))

(def -tsch- (get-in (app/app "scratch")
                    [:schema
                     :tree
                     :Task]))

^{:refer std.lib.schema/expand-scopes :added "4.0"}
(fact "expand `*` keys into `-` keys"
  ^:hidden
  
  (schema/expand-scopes :*/data)
  => #{:-/info :-/id :-/data :-/key}

  (schema/expand-scopes :*/info)
  => #{:-/info :-/id :-/key})

^{:refer std.lib.schema/linked-primary :added "4.0"}
(fact "gets the linked primary column"
  ^:hidden
  
  (schema/linked-primary -tsch-
                         :cache
                         (get-in (app/app "scratch")
                                 [:schema]))
  => '{:type :uuid, :cardinality :one, :primary true,
       :web {:example "AUD"},
       :sql {:default (rt.postgres/uuid-generate-v4)}, :scope :-/id, :order 0, :ident :TaskCache/id})

^{:refer std.lib.schema/order-keys :added "4.0"}
(fact "order keys given schema"
  ^:hidden
  
  (schema/order-keys -tsch-
                     [:cache
                      :status
                      :id
                      :WRONG])
  => '(:id :status :cache :WRONG))

^{:refer std.lib.schema/get-defaults :added "4.0"}
(fact "get defaults in the schema"
  ^:hidden
  
  (schema/get-defaults -tsch-)
  => '{:__deleted__ false, :id (rt.postgres/uuid-generate-v4)})

^{:refer std.lib.schema/check-valid-columns :added "4.0"}
(fact "check if columns are valid"
  ^:hidden
  
  (schema/check-valid-columns -tsch- [:id :status])
  => [true]

  (schema/check-valid-columns -tsch- [:id :WRONG])
  => (contains-in [false {:not-allowed #{:WRONG}}]))

^{:refer std.lib.schema/check-missing-columns :added "4.0"}
(fact "check if columns are missing"
  ^:hidden
  
  (schema/check-missing-columns -tsch-
                         [:status]
                         :required)
  => [false {:missing #{:name :cache},
             :required #{:name :cache :status}}]

  (schema/check-missing-columns -tsch-
                         [:status :name :cache]
                         :required)
  => [true])

^{:refer std.lib.schema/check-fn-columns :added "4.0"}
(fact "perform a check using the `:check` key"

  (schema/check-fn-columns -tsch- {:status 'a})
  => {})

^{:refer std.lib.schema/get-returning :added "4.0"}
(fact "collects the returning ids and columns"
  
  (->> (schema/get-returning -tsch-
                      [:*/data :cache])
       (map first))
  => '(:id :status :name :cache :time-created :time-updated))
