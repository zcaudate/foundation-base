(ns fx.d3
  (:require [fx.gui.data.return :as return]
            [rt.graal :as graal]
            [std.concurrent :as cc]
            [std.lang :as l]
            [std.lib :as h]
	    [js.lib.d3 :as d3])
  (:import (java.util.concurrent CompletableFuture)
           (org.graalvm.polyglot Value)))

(l/script :js
  {:require [[js.lib.d3 :as d3]]})

(defonce ^:dynamic *default* nil)

(def +path+ "assets/fx.d3")

(def +url+ "d3@6.6.2/dist/d3.min.js")

(defn d3:download
  "downloads the d3 script"
  {:added "4.0"}
  []
  (h/sh "wget" (str "https://unpkg.com/" +url+)
        {:root (str "resources/" +path+)}))

(def +init-d3+
  (l/emit-as :js '[(def d3 (require "./d3.min.js"))]))

(defn d3:create
  "creates a d3 webview instance"
  {:added "4.0"}
  ([opts]
   (graal/rt-graal:create {:lang :js
                           :resource [+path+]})))

(h/res:spec-add
 {:type :hara/fx.d3
  :instance {:create   d3:create
             :setup    (fn [d3]
                         (h/start d3)
                         (h/p:rt-raw-eval d3 +init-d3+)
                         (h/set! *default* d3) d3)
             :teardown (fn [d3] (h/set! *default* nil) (h/stop d3))}})

(defn get-d3
  "gets or creates a d3 webview instance"
  {:added "4.0"}
  ([]
   (or *default*
       (h/res :hara/fx.d3))))
