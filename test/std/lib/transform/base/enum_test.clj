(ns std.lib.transform.base.enum-test
  (:use code.test)
  (:require [std.lib.transform :as graph]
            [std.lib.transform.base.enum :refer :all]
            [std.lib.schema :as schema]
            [example.data :as examples]))

^{:refer std.lib.transform.base.enum/wrap-single-enum :added "3.0"}
(fact "wraps normalise with comprehension of the enum type"

  (graph/normalise {:account {:type :account.type/guest}}
                   {:schema (schema/schema {:account/type [{:type :enum
                                                            :enum {:ns :account.type
                                                                   :values #{:vip :guest}}}]})}
                   {:normalise-single [wrap-single-enum]})
  => {:account {:type :guest}}
  ^:hidden
  (graph/normalise {:account {:type :account.type/WRONG}}
                   {:schema (schema/schema {:account/type [{:type :enum
                                                            :enum {:ns :account.type
                                                                   :values #{:vip :guest}}}]})}
                   {:normalise-single [wrap-single-enum]})
  => (throws-info {:check #{:vip :guest}
                   :data :account.type/WRONG
                   :id :wrong-input}))
