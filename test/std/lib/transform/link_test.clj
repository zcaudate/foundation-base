(ns std.lib.transform.link-test
  (:use code.test)
  (:require [std.lib.transform.link :refer :all]
            [std.lib.transform :as graph]))

^{:refer std.lib.transform.link/wrap-link-current :added "3.0"}
(fact "adds the current ref to `:link :current`")

^{:refer std.lib.transform.link/wrap-link-attr :added "3.0"}
(fact "adds the parent link `:id` of the ref")

^{:refer std.lib.transform.link/wrap-link-parent :added "0.1"}
(fact "adding parent to current data")
