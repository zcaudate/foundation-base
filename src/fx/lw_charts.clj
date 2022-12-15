(ns fx.lw-charts
  (:require [fx.gui :as fx]
            [std.concurrent :as cc]
            [std.lang :as l]
            [std.lib :as h])
  (:import (java.util.concurrent CompletableFuture)))

(defonce ^:dynamic *default* nil)

(def +file+ "lw-charts.min.js")

(def +path+ "assets/fx.lw-charts")

(def +url+ "lightweight-charts@3.3.0/dist/lightweight-charts.standalone.production.js")



(comment
  (def +web-view+ (fx/create-html-viewer "lwc"))
  (fx/display-html (l/html [:script {:type "text/javascript"}
                               []])
                   +web-view+
                   [200 200]))

(defn lw-charts:download
  "downloads the js script"
  {:added "4.0"}
  []
  (h/sh "wget" (str "https://unpkg.com/" +url+) "-O" +file+
        {:root (str "resources/" +path+)}))


(comment
  
  (get-lw-charts)
  (h/res:stop :hara/fx.lw-charts))
