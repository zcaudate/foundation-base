(ns rt.docker.ryuk
  (:require [std.lib :as h]
            [std.concurrent :as cc]
            [std.json :as json]
            [std.string :as str]
            [rt.docker.common :as common]))

;;
;; Testing
;;

(defonce ^:dynamic *ryuk* nil)

(def +ryuk+
  {:group   "testing"
   :id      "reaper"
   :image   "testcontainers/ryuk:0.3.1"
   :ports   [8080]
   :labels  {"reaped" true}
   :volumes {"/var/run/docker.sock" "/var/run/docker.sock"}})

(defn start-ryuk
  "starts the reaper"
  {:added "4.0"}
  []
  (if-not (common/has-container? +ryuk+)
    (let [m (common/start-container +ryuk+)
          _ (h/wait-for-port (:container-ip m)
                             (first (:ports m))
                             {:timeout common/*timeout*})
          s (h/socket (:container-ip m) (first (:ports m)))
          r (cc/relay {:type :socket
                       :attached s})
          _ (cc/send  r "label=reaped=true")]
      (alter-var-root #'*ryuk* (fn [_]
                                 (assoc m :socket s :relay r))))
    *ryuk*))

(defn stop-ryuk
  "stops the reaper"
  {:added "4.0"}
  []
  (common/stop-container +ryuk+)
  (if *ryuk*
    (let [_ (h/stop  (:relay *ryuk*))
          _ (h/close (:socket *ryuk*))]
      (alter-var-root #'*ryuk* (constantly nil)))))

(defn start-reaped
  "starts a reaped container"
  {:added "4.0"}
  ([{:keys [group id image cmd flags labels environment volumes ports remove detached] :as m
     :or {group "testing"}}]
   (if (not *ryuk*) (do (alter-var-root #'*ryuk* (constantly :starting))
                        (future (start-ryuk))))
   (common/start-container (-> m
                               (assoc :group group)
                               (assoc-in [:labels "reaped"] true)))))

(defn stop-all-reaped
  "stops all reaped"
  {:added "4.0"}
  ([]
   (let [cs (->> (common/list-containers {} ["-f" "label=reaped"])
                 (filter #(-> % :Name (not= "testing_reaper")))
                 (map :id))]
     (when (not-empty cs)
       ;;cs
       (apply h/sh (concat ["docker" "--host" (or common/*host* "127.0.0.1") "stop"] cs))))))
