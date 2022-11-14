(ns lib.aether.base-test
  (:use code.test)
  (:require [lib.aether.base :refer :all]
            [lib.aether :as aether]))

^{:refer lib.aether.base/aether :added "3.0"}
(fact "creates an `Aether` object"

  (aether)
  => (contains-in
      {:repositories (contains [{:id "clojars",
                                 :type "default",
                                 :url "https://clojars.org/repo"}
                                {:id "central",
                                 :type "default",
                                 :url "https://repo1.maven.org/maven2/"}]
                               :in-any-order
                               :gaps-ok),
       :system org.eclipse.aether.RepositorySystem
       :session org.eclipse.aether.RepositorySystemSession}))

(comment
  (code.manage/import))
