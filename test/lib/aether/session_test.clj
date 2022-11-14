(ns lib.aether.session-test
  (:use code.test)
  (:require [lib.aether.session :refer :all]
            [lib.aether.system :as system]))

^{:refer lib.aether.session/session :added "3.0"}
(fact "creates a session from a system:"

  (session (system/repository-system)
           {})
  => org.eclipse.aether.RepositorySystemSession)