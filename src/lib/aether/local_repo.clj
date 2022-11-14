(ns lib.aether.local-repo
  (:require [std.fs :as fs]
            [std.object :as object])
  (:import (org.eclipse.aether.repository LocalRepository)))

(defonce +default-local-repo+
  (-> (System/getProperty "user.home")
      (fs/path ".m2" "repository")
      (str)))

(defn local-repo
  "creates a `LocalRepository` from a string
 
   (local-repo)
   => LocalRepository ;; #local \"<.m2/repository>\"
 
   ;; hooks into std.object
   (-> (local-repo \"/tmp\")
       (object/to-data))
   => \"/tmp\""
  {:added "3.0"}
  ([]
   (local-repo +default-local-repo+))
  ([^String path]
   (LocalRepository. path)))

(object/string-like

 LocalRepository
 {:tag "local"
  :read (fn [^LocalRepository repo] (str (.getBasedir repo)))
  :write local-repo})
