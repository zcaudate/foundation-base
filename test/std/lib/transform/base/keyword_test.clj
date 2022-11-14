(ns std.lib.transform.base.keyword-test
  (:use code.test)
  (:require [std.lib.transform :as graph]
            [std.lib.transform.base.keyword :refer :all]
            [std.lib.schema :as schema]
            [example.data :as examples]))

^{:refer std.lib.transform.base.keyword/wrap-single-keyword :added "3.0"}
(fact "removes the keyword namespace if there is one"

  (graph/normalise {:account {:type :account.type/vip}}
                   {:schema (schema/schema {:account/type [{:type :keyword
                                                            :keyword {:ns :account.type}}]})}
                   {:normalise-single [wrap-single-keyword]})
  => {:account {:type :vip}})
