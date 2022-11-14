(ns std.config-test
  (:use code.test)
  (:require [std.config :refer :all])
  (:refer-clojure :exclude [load resolve]))

^{:refer std.config/get-session :added "3.0"}
(fact "retrieves the global session")

^{:refer std.config/swap-session :added "3.0"}
(fact "updates the global session")

^{:refer std.config/clear-session :added "3.0"}
(fact "clears the global session")
