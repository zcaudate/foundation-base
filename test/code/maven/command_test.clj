(ns code.maven.command-test
  (:use code.test)
  (:require [std.config :as config]
            [std.config.ext.gpg :as gpg]
            [code.maven.command :refer :all]
            [code.maven.package :as package]
            [code.link :as linkage]
            [code.project :as project]
            [lib.aether :as aether]
            [jvm.artifact :as artifact]))

(def -key- (config/resolve [:include "config/keys/test@test.com" {:type :gpg}]))

(def -collected- (linkage/collect))

^{:refer code.maven.command/sign-file :added "1.2"}
(fact "signs a file with gpg"

  (sign-file {:file "test-data/code.maven/maven.edn" :extension "edn"}
             -key-)
  => {:file "test-data/code.maven/maven.edn.asc"
      :extension "edn.asc"})

^{:refer code.maven.command/create-digest :added "1.2"}
(fact "creates a digest given a file and a digest type"

  (create-digest "MD5"
                 "md5"
                 {:file "test-data/code.maven/maven.edn"
                  :extension "edn"})
  => {:file "test-data/code.maven/maven.edn.md5", :extension "edn.md5"})

^{:refer code.maven.command/add-digest :added "1.2"}
(fact "adds MD5 and SHA1 digests to all artifacts"

  (add-digest [{:file "test-data/code.maven/maven.edn"
                :extension "edn"}])
  => [{:file "test-data/code.maven/maven.edn.md5",
       :extension "edn.md5"}
      {:file "test-data/code.maven/maven.edn",
       :extension "edn"}])

^{:refer code.maven.command/clean :added "3.0"}
(fact "cleans the interim directory"

  (def -args- ['hara/object nil -collected- (project/project)])

  (apply package/package -args-)

  (apply clean -args-))

^{:refer code.maven.command/prepare-artifacts :added "3.0"}
(fact "prepares the files for deployment"
  ^:hidden
  
  (prepare-artifacts {:pom "test-data/code.maven/sample.pom"
                      :jar "test-data/code.maven/sample.jar"
                      :interim ""}
                     {:secure true :digest true}
                     {:deploy {:signing {:key -key-}}})
  => coll?)

^{:refer code.maven.command/install :added "3.0"}
(fact "installs a package to the local `.m2` repository"

  (install 'hara/std.object
           {}
           -collected-
           (assoc (project/project) :version "3.0.1" :aether (aether/aether)))
  => (contains [artifact/rep? artifact/rep?]) ^:hidden

  (install 'hara/std.object
           {:secure true :digest true}
           -collected-
           (assoc (project/project)
                  :version "3.0.1"
                  :aether (aether/aether)
                  :deploy {:signing {:key -key-}}))
  => vector?)

^{:refer code.maven.command/deploy :added "3.0"}
(comment "deploys a package to a test repo"

  (deploy 'hara/base
          {:tag :dev}   ;;(read-config)
          -collected-
          (assoc (project/project)
                 :deploy (config/load "config/deploy.edn")
                 :aether (aether/aether))))

(comment)
