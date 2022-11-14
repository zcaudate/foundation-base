(ns lib.aether.wagon-test
  (:use code.test)
  (:require [lib.aether.wagon :refer :all]))

^{:refer lib.aether.wagon/add-factory :added "3.0"}
(fact "registers a wagon factory for creating transports")

^{:refer lib.aether.wagon/remove-factory :added "3.0"}
(fact "removes the registered wagon factory")

^{:refer lib.aether.wagon/all-factories :added "3.0"}
(fact "list all registered factories"

  (all-factories)
  ;;=> {:https org.apache.maven.wagon.providers.webdav.WebDavWagon}
  )

^{:refer lib.aether.wagon/create :added "3.0"}
(fact "create a wagon given a scheme"

  (create :https)
  ;;;=> org.apache.maven.wagon.providers.webdav.WebDavWagon
  )
