(ns rt.basic.type-container
  (:require [std.lib :as h :refer [defimpl]]
            [std.json :as json]
            [std.fs :as fs]
            [std.string :as str]
            [std.make :as make]
            [lib.docker :as docker]
            [rt.basic.type-common :as common]
            [rt.basic.type-oneshot :as oneshot]))

(def ^:dynamic *container* nil)

(defn start-container-process
  "starts the container"
  {:added "4.0"}
  [lang
   {:keys [id suffix group remove ports image exec bootstrap] :as config
    :or {remove true}}
   port
   {:keys [host] :as rt}]
  (let [script (bootstrap port {:host (or host "host.docker.internal")})
        container {:id (or id (h/sid))
                   :suffix suffix
                   :group (or group "testing")
                   :image image
                   :ports ports
                   :remove remove
                   :cmd   (conj exec script)}]
    (def ^:dynamic *container* container)
    (docker/start-runtime rt container)))

(defn start-container
  "starts a container"
  {:added "4.0"}
  [lang config port rt]
  (let [[program process exec] (oneshot/rt-oneshot-setup
                                lang
                                (:program config)
                                (:process config)
                                (:exec config)
                                (:runtime rt))
        config    (merge config
                        process
                        {:lang lang
                         :program program
                         :exec exec
                         :runtime (:runtime rt)})]
    (start-container-process lang config port rt)))

(defn stop-container
  "stops a container"
  {:added "4.0"}
  [{:keys [id] :as m}]
  (docker/stop-container m))


(comment
  (lib.docker.common/start-container
   (assoc *container* :cmd ["luajit" "-e" "print(1+1)"])
   )
  )
