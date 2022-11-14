(ns std.contract.sketch-test
  (:use code.test)
  (:require [std.contract.sketch :refer :all])
  (:refer-clojure :exclude [remove]))

^{:refer std.contract.sketch/optional-string :added "3.0"}
(fact "string for optional")

^{:refer std.contract.sketch/maybe-string :added "3.0"}
(fact "string for maybe")

^{:refer std.contract.sketch/as:optional :added "3.0"}
(fact "creates an optional")

^{:refer std.contract.sketch/optional? :added "3.0"}
(fact "checks if optional type")

^{:refer std.contract.sketch/as:maybe :added "3.0"}
(fact "creates a maybe")

^{:refer std.contract.sketch/maybe? :added "3.0"}
(fact "checks if maybe type")

^{:refer std.contract.sketch/func-string :added "3.0"}
(fact "string for func")

^{:refer std.contract.sketch/func-invoke :added "3.0"}
(fact "invokes the func")

^{:refer std.contract.sketch/fn-sym :added "3.0"}
(fact "gets function symbol")

^{:refer std.contract.sketch/func-form :added "3.0"}
(fact "constructs a func")

^{:refer std.contract.sketch/func :added "3.0"}
(fact "macro for constructing a func")

^{:refer std.contract.sketch/func? :added "3.0"}
(fact "checks if instance is a func")

^{:refer std.contract.sketch/from-schema-map :added "3.0"}
(fact "sketch from malli's map syntax")

^{:refer std.contract.sketch/from-schema :added "3.0"}
(fact "sketch from schema")

^{:refer std.contract.sketch/to-schema-extend :added "3.0"}
(fact "extending schema conversion")

^{:refer std.contract.sketch/to-schema :added "3.0"}
(fact "converts object to schema")

^{:refer std.contract.sketch/lax :added "3.0"}
(fact "relaxes a map (optional keys and maybe vals)")

^{:refer std.contract.sketch/norm :added "3.0"}
(fact "gets rid of optional keys")

^{:refer std.contract.sketch/closed :added "3.0"}
(fact "closes the map")

^{:refer std.contract.sketch/opened :added "3.0"}
(fact "opens the map")

^{:refer std.contract.sketch/tighten :added "3.0"}
(fact "tightens a map (no optionals or maybes)")

^{:refer std.contract.sketch/remove :added "3.0"}
(fact "removes a key from map")
