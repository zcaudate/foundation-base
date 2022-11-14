(ns std.dispatch
  (:require [std.protocol.dispatch :as protocol.dispatch]
            [std.protocol.component :as protocol.component]
            [std.lib.component :as component]
            [std.concurrent :as cc]
            [std.dispatch.common]
            [std.dispatch.board]
            [std.dispatch.core]
            [std.dispatch.debounce]
            [std.dispatch.hub]
            [std.dispatch.queue]
            [std.dispatch.types :as types]
            [std.lib :as h]))

(h/build-impl {}
              protocol.dispatch/IDispatch
              :include    [-bulk?]

              protocol.component/IComponent
              :fns {:body-sym-fn component/impl:component}
              :exclude [-props -remote?])

(defn dispatch?
  "checks if object is an dispatch"
  {:added "3.0"}
  ([obj]
   (satisfies? protocol.dispatch/IDispatch obj)))

(defn submit
  "submits entry to an dispatch"
  {:added "3.0"}
  ([{:keys [serial handler] :as dispatch} entry]
   (if serial
     (if (bulk? dispatch)
       (handler dispatch [entry])
       (handler dispatch entry))
     (protocol.dispatch/-submit dispatch entry))))

(defn create
  "creates a component compatible dispatch"
  {:added "3.0"}
  ([{:keys [type options handler] :as m}]
   (-> (types/<dispatch> m)
       (protocol.dispatch/-create))))

(defn dispatch
  "creates and starts an dispatch"
  {:added "3.0"}
  ([m]
   (-> (create m)
       (component/start))))
