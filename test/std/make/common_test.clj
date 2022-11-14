(ns std.make.common-test
  (:use code.test)
  (:require [std.make.common :refer :all]))

^{:refer std.make.common/triggers-purge :added "4.0"}
(fact "purges triggers")

^{:refer std.make.common/triggers-set :added "4.0"}
(fact "sets a trigger")

^{:refer std.make.common/triggers-clear :added "4.0"}
(fact "clears a trigger")

^{:refer std.make.common/triggers-get :added "4.0"}
(fact "gets a trigger")

^{:refer std.make.common/triggers-list :added "4.0"}
(fact "lists all trigers")

^{:refer std.make.common/get-triggered :added "4.0"}
(fact "gets all configs given a trigger namespace")

^{:refer std.make.common/make-config? :added "4.0"}
(fact "checks that object is a `make` config")

^{:refer std.make.common/make-config-map :added "4.0"}
(fact "creates a make-config map")

^{:refer std.make.common/make-config :added "4.0"}
(fact "function to create a `make` config")

^{:refer std.make.common/make-config-update :added "4.0"}
(fact "updates the make-config")

^{:refer std.make.common/make-dir :added "4.0"}
(fact "gets the dir specified by the config")

^{:refer std.make.common/make-run :added "4.0"}
(fact "runs the `make` executable")

^{:refer std.make.common/make-shell :added "4.0"}
(fact "opens a terminal at the location of the `make` directory")

^{:refer std.make.common/make-dir-setup :added "4.0"}
(fact "sets up the `make` directory")

^{:refer std.make.common/make-dir-exists? :added "4.0"}
(fact "checks that the `make` directory exists")

^{:refer std.make.common/make-dir-teardown :added "4.0"}
(fact "deletes the the `make` directory")
