(ns fx.graphviz
  (:require [fx.graphviz.command :as command]
            [std.concurrent :as cc]
            [std.html :as html]
            [fx.gui.data.return :as return]
            [std.lib :as h]))

(def ^:dynamic *command* ["dot" "-Tpng"])

(def ^:dynamic *default* nil)

(def ^:dynamic *options*
  {:receive {:save false}
   :send    {:quiet true
             :save false}})

(defn graphviz:create
  "creates a graphviz instance"
  {:added "3.0"}
  ([opts]
   (cc/relay:create {:type :process
                     :args *command*
                     :options (merge opts *options*)})))

(h/res:spec-add
 {:type :hara/fx.graphviz
  :instance {:create   graphviz:create
             :setup    (fn [gp]
                         (h/start gp)
                         (h/set! *default* gp) gp)
             :teardown (fn [gp]
                         (h/stop gp)
                         (h/set! *default* nil) gp)}})

(defn get-graphviz
  "gets the current graphviz instance"
  {:added "3.0"}
  ([]
   (or *default*
       (h/res :hara/fx.graphviz))))

(defn generate-script
  "creates the graphviz dot file"
  {:added "4.0"}
  ([{:keys [nodes edges style]}]
   (command/graph->dot nodes edges style)))

(defn graph
  "graph a set of points"
  {:added "3.0"}
  ([input]
   (graph input {}))
  ([input settings]
   (graph (get-graphviz) input settings))
  ([gv {:keys [nodes edges style]} {:keys [title] :as settings}]
   (let [dot  (command/graph->dot nodes edges style)
         {:keys [output] :as m} @(cc/send gv {:op :bytes :line dot})
         _ (return/process-output output (merge settings
                                                {:type :png
                                                 :size [400 400]}))]
     (assoc m :dot-str dot))))

