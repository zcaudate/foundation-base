(ns fx.uniorg
  (:require [fx.gui.data.return :as return]
            [rt.graal :as graal]
            [std.concurrent :as cc]
            [std.lang :as l]
            [std.lib :as h]
            [std.json :as js]
            [std.lib.zip :as zip])
  (:import (org.graalvm.polyglot Value)
           (java.util.concurrent CompletableFuture)))

(defonce ^:dynamic *default* nil)

(def +path+ "assets/fx.uniorg")

(def +init-uniorg+
  (l/emit-as
   :js '[(def #{extract-ast extract-readme} (require "./bundle.js"))
         (defn as-future [f input]
           (let [Future (Java.type "java.util.concurrent.CompletableFuture")
                 out    (new Future)]
             (. (f input)
                (then (fn [output] (out.complete output))))
             (return out)))]))

(defn uniorg:create
  "creates a uniorg instance"
  {:added "4.0"}
  ([opts]
   (graal/rt-graal:create {:lang :js
                           :resource [+path+]})))

(h/res:spec-add
 {:type :hara/fx.uniorg
  :instance {:create   uniorg:create
             :setup    (fn [uniorg]
                         (h/start uniorg)
                         (cc/req uniorg {:op :exec :body +init-uniorg+})
                         (h/set! *default* uniorg) uniorg)
             :teardown (fn [uniorg] (h/set! *default* nil) (h/stop uniorg))}})

(defn get-uniorg
  "gets global uniorg object"
  {:added "4.0"}
  ([]
   (or *default*
       (h/res :hara/fx.uniorg))))

(defn org-extract-ast
  "extracts the org ast"
  {:added "4.0"}
  ([s]
   (org-extract-ast s {}))
  ([s opts]
   (org-extract-ast (get-uniorg) s opts))
  ([uniorg s opts]
   (cc/req uniorg {:op :exec
                   :body (l/emit-as :js [(list 'extract-ast s)])})))

(defn org-extract-readme
  "extracts the org readme"
  {:added "4.0"}
  ([s]
   (org-extract-readme s {}))
  ([s opts]
   (org-extract-readme (get-uniorg) s opts))
  ([uniorg s opts]
   (h/-> (cc/req uniorg {:op :exec
                         :body (l/emit-as :js [(list 'as-future 'extract-readme s)])})
         (.asHostObject ^Value %)
         (.get ^CompletableFuture %))))

(comment
  (def -found- (volatile! nil))
  
  (h/res:stop :hara/fx.uniorg)

  ()
  (h/req (get-uniorg)
         {:op :exec
          :body (l/js
                 (. processor
                    (process "* org-mode example\n your text goes here")
                    (.then )))})
  (org-extract-readme "* example document")
  (org-extract-ast "* example document"))
