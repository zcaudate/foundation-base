(ns std.pretty.dispatch-test
  (:use code.test)
  (:require [std.pretty.dispatch :refer :all]
            [std.pretty :as printer]))

^{:refer std.pretty.dispatch/chained-lookup :added "3.0"}
(fact "chains two or more lookups together"

  (chained-lookup
   (inheritance-lookup printer/clojure-handlers)
   (inheritance-lookup printer/java-handlers)))

^{:refer std.pretty.dispatch/inheritance-lookup :added "3.0"}
(fact "checks if items inherit from the handlers"

  ((inheritance-lookup printer/clojure-handlers)
   clojure.lang.Atom)
  => fn?

  ((inheritance-lookup printer/clojure-handlers)
   String)
  => nil)
