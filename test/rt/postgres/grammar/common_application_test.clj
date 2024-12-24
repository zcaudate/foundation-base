(ns rt.postgres.grammar.common-application-test
  (:use code.test)
  (:require [rt.postgres.grammar.common-application :refer :all]))

^{:refer rt.postgres.grammar.common-application/app-modules :added "4.0"}
(fact "checks for modules related to a given application")

^{:refer rt.postgres.grammar.common-application/app-create-raw :added "4.0"}
(fact "creates a schema from tables and links")

^{:refer rt.postgres.grammar.common-application/app-create :added "4.0"}
(fact "makes the app graph schema"
  ^:hidden
  
  (app-create "test.postgres")
  => map?)

^{:refer rt.postgres.grammar.common-application/app-clear :added "4.0"}
(fact "clears the entry for an app"
  ^:hidden
  
  (app-clear "test.postgres")
  => nil)

^{:refer rt.postgres.grammar.common-application/app-rebuild :added "4.0"}
(fact "rebuilds the app schema"
  ^:hidden
  
  (app-rebuild "test.postgres")
  => map?)

^{:refer rt.postgres.grammar.common-application/app-rebuild-tables :added "4.0"}
(fact "initiate rebuild of app schema")

^{:refer rt.postgres.grammar.common-application/app-list :added "4.0"}
(fact "rebuilds the app schema"
  ^:hidden
  
  (app-list)
  => coll?)

^{:refer rt.postgres.grammar.common-application/app :added "4.0"}
(fact "gets an app"
  ^:hidden
  
  (app "test.postgres")
  => map?)

^{:refer rt.postgres.grammar.common-application/app-schema :added "4.0"}
(fact "gets the app schema"
  ^:hidden
  
  (app-schema "test.postgres")
  => map?)
