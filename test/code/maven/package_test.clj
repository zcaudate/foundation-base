(ns code.maven.package-test
  (:use code.test)
  (:require [code.maven.package :refer :all]
            [code.link :as linkage]
            [std.fs :as fs]
            [code.project :as project]
            [std.html :as html]))

(def -collected- (linkage/collect))

^{:refer code.maven.package/pom-properties :added "3.0"}
(fact "creates a pom.properties file"

  (pom-properties (project/project))
  ;; "# foundation.deploy\n# Sun Oct 14 22:57:11 CST 2018\nversion=3.0.1\ngroupId=foundation\nartifactId=base"
  => string?)

^{:refer code.maven.package/coordinate->tree :added "3.0"}
(fact "creates a coordinate tree entry"

  (coordinate->tree '[foundation/std.lib "0.1.1"])
  => [:dependency
      [:groupId "foundation"]
      [:artifactId "std.lib"]
      [:version "0.1.1"]])

^{:refer code.maven.package/repository->tree :added "3.0"}
(fact "creates a repository tree entry"

  (repository->tree ["clojars" "https://clojars.org/repo"])
  => [:repository
      [:id "clojars"]
      [:url "https://clojars.org/repo"]
      [:snapshots [:enabled "true"]]
      [:releases [:enabled "true"]]])

^{:refer code.maven.package/license->tree :added "3.0"}
(fact "creates a license tree entry"

  (license->tree {:name "MIT" :url "http/mit.license"})
  => [:license
      [:name "MIT"]
      [:url "http/mit.license"]])

^{:refer code.maven.package/pom-xml :added "3.0"}
(fact "creates a pom.properties file"
  ^:hidden

  (->> (pom-xml '{:description "task execution of and standardization",
                  :name foundation/std.task,
                  :artifact "std.task",
                  :group "foundation",
                  :version "3.0.1",
                  :dependencies [[foundation/std.lib "3.0.1"]]})
       (html/tree))
  => (contains
      [[:modelversion "4.0.0"]
       [:packaging "jar"]
       [:groupid "foundation"]
       [:artifactid "std.task"]
       [:version "3.0.1"]
       [:name "foundation/std.task"]
       [:description "task execution of and standardization"]
       [:dependencies
        [:dependency 
         [:groupid "foundation"] 
         [:artifactid "std.lib"] 
         [:version "3.0.1"]]]]
      :gaps-ok))

^{:refer code.maven.package/generate-manifest :added "3.0"}
(fact "creates a manifest.mf file for the project"

  (generate-manifest "test-scratch" {:simulate true})
  => "MANIFEST.MF")

^{:refer code.maven.package/generate-pom :added "3.0"}
(fact "generates a pom file given an entry"

  (-> (generate-pom {:artifact "std.task"
                     :group "foundation"
                     :version "3.0.1"}
                    "test-scratch")
      (update-in [0] html/tree)) ^:hidden
  => (contains
      [(contains [:project
                  [:packaging "jar"]
                  [:groupid "foundation"]
                  [:artifactid "std.task"]
                  [:version "3.0.1"]]
                 :gaps-ok)
       "META-INF/maven/foundation/std.task/pom.xml"
       "META-INF/maven/foundation/std.task/pom.properties"]))

^{:refer code.maven.package/linkage :added "3.0"}
(fact "returns the linkage for a given name"

  (linkage 'foundation/std.lib nil -collected- nil)
  => (contains {:name 'foundation/std.lib}))

^{:refer code.maven.package/package :added "3.0"}
(fact "returns the packaged items for a given name"

  (package 'foundation/std.image {} -collected- {:root "." :version "3.0.1"})

  => (contains {:package 'foundation/std.image,
                :jar "foundation-std.image-3.0.1.jar",
                :pom "foundation-std.image-3.0.1.pom.xml",
                :interim (str (fs/file "./target/interim/foundation/std.image")),
                :files (contains ["MANIFEST.MF"
                                  "META-INF/maven/foundation/std.image/pom.xml"
                                  "META-INF/maven/foundation/std.image/pom.properties"])}))

^{:refer code.maven.package/infer :added "3.0"}
(fact "returns the infered items for a given name"

  (infer 'foundation/std.image nil -collected-  {:root "." :version "3.0.1"})
  => (contains {:package 'foundation/std.image,
                :jar "foundation-std.image-3.0.1.jar",
                :pom "foundation-std.image-3.0.1.pom.xml",
                :interim (str (fs/file "./target/interim/foundation/std.image"))}))

(comment
  (./code:import)
  (.replaceAll "\n\n" "\\s" ""))
