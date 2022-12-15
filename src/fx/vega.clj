(ns fx.vega
  (:require [fx.gui.data.return :as return]
            [rt.graal :as graal]
            [std.concurrent :as cc]
            [std.lang :as l]
            [std.lib :as h])
  (:import (java.util.concurrent CompletableFuture)
           (org.graalvm.polyglot Value)))

(defonce ^:dynamic *default* nil)

(def +path+ "assets/fx.vega")

(def +files+ ["vega@5.18.0/build/vega.min.js"
              "vega-lite@4.17.0/build/vega-lite.min.js"])

(defn vega:download
  "downloads vega assets from unpkg"
  {:added "4.0"}
  []
  (h/sys:wget-bulk "https://unpkg.com/"
                   +path+
                   +files+))

(def +init-vega+
  (l/emit-as
   :js '[(def vega      (require "./vega.min.js"))
         (def vega-lite (require "./vega-lite.min.js"))
         (defn renderVega
           [spec]
           (var vspec (:? (. spec ["$schema"] (includes "vega-lite")) (. vega-lite (compile spec) ["spec"]) spec))
           (var view (. (new vega.View (vega.parse vspec nil)
                             {:loader (vega.loader {:baseURL nil})
                              :logger (vega.logger vega.Warn "error")
                              :renderer "none"})
                        (finalize)))
           (var Future (Java.type "java.util.concurrent.CompletableFuture"))
           (var out    (new Future))
           (. view (toSVG) (then (fn [svg] (out.complete svg))))
           (return out))]))

(defn vega:create
  "creates a new vega graal context"
  {:added "3.0"}
  ([opts]
   (graal/rt-graal:create {:lang :js
                           :resource [+path+]})))

(h/res:spec-add
 {:type :hara/fx.vega
  :instance {:create   vega:create
             :setup    (fn [vega]
                         (h/start vega)
                         (graal/eval-graal vega +init-vega+)
                         (h/set! *default* vega) vega)
             :teardown (fn [vega] (h/set! *default* nil) (h/stop vega))}})

(defn get-vega
  "gets current vega context
 
   (get-vega)"
  {:added "3.0"}
  ([]
   (or *default*
       (h/res :hara/fx.vega))))

(defn render
  "renders a vega image
 
   (render {:$schema \"https://vega.github.io/schema/vega-lite/v4.json\",
            :width 400, :height 300, :description \"A simple bar chart.\",
            :data {:values [{:a \"A\", :b 28} {:a \"B\", :b 55}
                            {:a \"C\", :b 43} {:a \"D\", :b 91} {:a \"E\", :b 81}
                            {:a \"F\", :b 53} {:a \"G\", :b 19} {:a \"H\", :b 87} {:a \"I\", :b 52}]},
            :mark \"bar\", :encoding {:x {:field \"a\", :type \"ordinal\"},
                                    :y {:field \"b\", :type \"quantitative\"}}}
           {:type :png :title nil})
  => bytes?"
  {:added "3.0"}
  ([data]
   (render data {}))
  ([data opts]
   (render (get-vega) data opts))
  ([vega data opts]
   (h/-> (graal/eval-graal vega (l/emit-as :js [(list 'renderVega data)]))
         (.asHostObject ^Value %)
         (.get ^CompletableFuture %)
         (.getBytes ^String %)
         (return/process-output (merge {:type :svg
                                        :title "Vega Plot"}
                                       opts)))))

(comment

  (get-vega)

  (graal/eval-graal (get-vega)
                    "json_encode")
  
  (h/res:stop :hara/fx.vega)
  (def -vega- (h/start (vega:create {})))

  (h/pl +init-vega+)
  (h/req -vega- {:op :exec :body  "vega = require(\"./vega.min.js\")"})
  (h/req -vega- {:op :exec :body  "vega_lite = require(\"./vega-lite.min.js\")"})
  (h/req -vega- {:op :exec :body  +init-vega+})

  (h/res:stop :hara/fx.vega)
  (render {:$schema "https://vega.github.io/schema/vega-lite/v4.json",
           :width 400, :height 300, :description "A simple bar chart.",
           :data {:values [{:a "A", :b 28} {:a "B", :b 55}
                           {:a "C", :b 43} {:a "D", :b 91} {:a "E", :b 81}
                           {:a "F", :b 53} {:a "G", :b 19} {:a "H", :b 87} {:a "I", :b 52}]},
           :mark "bar", :encoding {:x {:field "a", :type "ordinal"},
                                   :y {:field "b", :type "quantitative"}}}
          {:title "Hello"
           :size [450 350]})
  (std.fs/write-all-bytes "output.svg" *1))
