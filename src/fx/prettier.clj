(ns fx.prettier
  (:require [fx.gui.data.return :as return]
            [rt.graal :as graal]
            [std.concurrent :as cc]
            [std.lang :as l]
            [std.lib :as h])
  (:import (org.graalvm.polyglot Value)))

(defonce ^:dynamic *default* nil)

(def +path+ "assets/fx.prettier")

(def +files+ ["prettier@2.2.1/standalone.js"
              "prettier@2.2.1/parser-babel.js"
              "prettier@2.2.1/parser-html.js"])

(defn prettier:download
  "downloads prettier assets from unpkg"
  {:added "4.0"}
  []
  (h/on:all
   (map (fn [file]
          (h/sh "wget" (str "https://unpkg.com/" file)
                {:async true
                 :root (str "resources/" +path+)}))
        +files+)))

(def +init-prettier+
  (l/emit-as
   :js  '[(def prettier    (require "./standalone.js"))
          (def parserBabel (require "./parser-babel.js"))
          (def parserHtml ( require "./parser-html.js"))
          (defn prettify
            [s]
            (return (prettier.format s {:parser "babel"
                                        :plugins [parserBabel parserHtml]})))]))

(defn prettier:create
  "creates a prettier instance"
  {:added "4.0"}
  ([opts]
   (graal/rt-graal:create {:lang :js
                           :resource [+path+]})))

(h/res:spec-add
 {:type :hara/fx.prettier
  :instance {:create   prettier:create
             :setup    (fn [prettier]
                         (h/start prettier)
                         (h/p:rt-raw-eval prettier +init-prettier+)
                         (h/set! *default* prettier) prettier)
             :teardown (fn [prettier] (h/set! *default* nil) (h/stop prettier))}})

(defn get-prettier
  "gets the global prettier instance"
  {:added "4.0"}
  ([]
   (or *default*
       (h/res :hara/fx.prettier))))

(defn prettify
  "prettifies a js/jsx string
 
   (prettify \"const    a  \\n =    1\")
   => \"const a = 1;\\n\""
  {:added "4.0"}
  ([s]
   (prettify s {}))
  ([s opts]
   (prettify (get-prettier) s opts))
  ([prettier s opts]
   (h/-> (h/p:rt-raw-eval prettier (l/emit-as :js [(list 'prettify s)]))
         (.asString ^Value %))))

(comment

  (h/res:stop :hara/fx.prettier)
  (get-prettier)
  (l/js (prettify "const html = /* HTML */ `<DIV> </DIV>`"))
  
  (prettify "const html = /* HTML */ `<DIV> </DIV>`")
  
  (prettier:download)
  
  (def -prettier- (h/start (prettier:create {})))

  (h/pl +init-prettier+)
  (h/req -prettier- {:op :exec :body  "prettier = require(\"./prettier.min.js\")"})
  (h/req -prettier- {:op :exec :body  "prettier_lite = require(\"./prettier-lite.min.js\")"})
  (h/req -prettier- {:op :exec :body  +init-prettier+})

  (h/res:stop :hara/fx.prettier)
  (render {:$schema "https://prettier.github.io/schema/prettier-lite/v4.json",
           :width 400, :height 300, :description "A simple bar chart.",
           :data {:values [{:a "A", :b 28} {:a "B", :b 55}
                           {:a "C", :b 43} {:a "D", :b 91} {:a "E", :b 81}
                           {:a "F", :b 53} {:a "G", :b 19} {:a "H", :b 87} {:a "I", :b 52}]},
           :mark "bar", :encoding {:x {:field "a", :type "ordinal"},
                                   :y {:field "b", :type "quantitative"}}}
          {:title "Hello"})
  (std.fs/write-all-bytes "output.svg" *1))
