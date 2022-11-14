(ns std.lib.context.registry-test
  (:use code.test)
  (:require [std.lib.context.registry :refer :all]))

^{:refer std.lib.context.registry/rt-null? :added "3.0"}
(fact "checks that object is of type NullRuntime")

^{:refer std.lib.context.registry/registry-list :added "3.0"}
(fact "lists all contexts"

  (registry-list))

^{:refer std.lib.context.registry/registry-install :added "3.0"}
(fact "installs a new context type"

  (registry-install :play.test))

^{:refer std.lib.context.registry/registry-uninstall :added "3.0"}
(fact "uninstalls a new context type"

  (registry-uninstall :play.test))

^{:refer std.lib.context.registry/registry-get :added "3.0"}
(fact "gets the context type"

  (registry-get :null)
  => (contains-in
      {:context :null,
       :rt {:default {:key :default,
                      :resource :hara/context.rt.null,
                      :config {}}}}))

^{:refer std.lib.context.registry/registry-rt-list :added "3.0"}
(fact "return all runtime types"

  (registry-rt-list))

^{:refer std.lib.context.registry/registry-rt-add :added "3.0"}
(fact "installs a context runtime type")

^{:refer std.lib.context.registry/registry-rt-remove :added "3.0"}
(fact "uninstalls a context runtime type")

^{:refer std.lib.context.registry/registry-rt :added "3.0"}
(fact "gets the runtime type information")

^{:refer std.lib.context.registry/registry-scratch :added "4.0"}
(fact "gets the scratch runtime for a registered context")
