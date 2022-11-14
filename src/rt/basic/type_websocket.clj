(ns rt.basic.type-websocket
  (:require [std.protocol.context :as protocol.context]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.runtime :as default]
            [rt.basic.type-common :as common]
            [std.lib :as h :refer [defimpl]]
            [std.json :as json]
            [std.concurrent :as cc]
            [std.string :as str]
            [rt.basic.server-basic :as server]
            [rt.basic.type-basic :as basic]
            [rt.basic.type-bench :as bench]
            [rt.basic.server-websocket :as ws]))

(defn start-websocket
  "starts bench and server for websocket runtime"
  {:added "4.0"}
  [{:keys [id lang bench port process] :as rt}]
  (let [bench  (if (nil? bench)
                 false
                 bench)]
    (basic/start-basic (assoc rt :bench bench)
                       ws/create-websocket-server)))

(defimpl RuntimeWebsocket [id]
  :string basic/rt-basic-string
  :protocols [std.protocol.component/IComponent
              :method {-start start-websocket
                       -stop basic/stop-basic
                       -kill basic/stop-basic}
              protocol.context/IContext
              :prefix "default/default-"
              :method {-raw-eval    basic/raw-eval-basic
                       -invoke-ptr  basic/invoke-ptr-basic}])

(defn rt-websocket:create
  "creates a websocket runtime"
  {:added "4.0"}
  [{:keys [id
           lang
           runtime
           process] :as m
    :or {runtime :websocket}}]
  (let [process (h/merge-nested (common/get-options lang :websocket :default)
                                process)]
    (map->RuntimeWebsocket (merge  m {:id (or id (h/sid))
                                      :tag runtime
                                      :runtime runtime
                                      :process process
                                      :lifecycle process}))))

(defn rt-websocket
  "creates and start a websocket runtime"
  {:added "4.0"}
  [{:keys [id
           lang
           runtime
           program
           process] :as m}]
  (-> (rt-websocket:create m)
      (h/start)))

(comment
  (def rt (bench/start-bench :lua
                            {}
                            49373
                            {}))
  bench/*active*
  (h/sh-output (:process rt))
  (bench/stop-bench rt)
  )
