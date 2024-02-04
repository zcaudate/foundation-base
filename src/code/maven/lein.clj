(ns code.maven.lein
  (:require [std.fs :as fs]
            [std.lib :as h]
            [code.maven.package :as package]))

(defn deploy-lein
  "returns the infered items for a given name
 
   (infer 'xyz.zcaudate/std.image nil -collected-  {:root \".\" :version \"3.0.1\"})
   => (contains {:package 'xyz.zcaudate/std.image,
                 :jar \"foundation-std.image-3.0.1.jar\",
                 :pom \"foundation-std.image-3.0.1.pom.xml\",
                 :interim (str (fs/file \"./target/interim/xyz.zcaudate/std.image\"))})"
  {:added "3.0"}
  ([name {:keys [interim] :as params} _ {:keys [root version]}]
   (let [interim (or interim package/+interim+)
         interim (fs/path root interim (str name))

         _       (h/sh {:root (str interim)
                        :args ["rm" "-R" "deploy"]
                        :ignore-errors true})
         _       (h/sh {:root (str interim)
                        :args ["mkdir" "deploy"]})
         _       (h/sh {:root (str interim)
                        :args ["cp" "-R" "package" "deploy/src"]})
         _       (h/sh {:root (str interim)
                        :args ["cp" "project.clj" "deploy"]})
         _       (h/sh {:root (str (fs/path interim "deploy" "src"))
                        :args ["rm" "-R" "META-INF" "MANIFEST.MF"]})
         out     (h/sh {:root (str (fs/path interim "deploy"))
                        :args ["lein" "deploy" "clojars"]})]
     {:results out
      :interim (str interim)})))
