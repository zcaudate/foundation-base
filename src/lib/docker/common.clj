(ns lib.docker.common
  (:require [std.lib :as h]
            [std.concurrent :as cc]
            [std.json :as json]
            [std.string :as str]))

(defonce ^:dynamic *host* (System/getenv "DOCKER_HOST"))

(defonce ^:dynamic *timeout* 10000)

(defn raw-exec
  "executes a shell command"
  {:added "4.0" :guard true}
  [args opts]
  (let [lines (->> @(apply h/sh args)
                   (str/trim)
                   (str/split-lines)
                   (filter not-empty))]
    (cond->> lines
      (:json opts) (mapv #(json/read % json/+keyword-mapper+)))))

(defn raw-command
  "executes a docker command"
  {:added "4.0"}
  [command & [opts tail]]
  (let [{:keys [host format]
         :or {host *host*}} opts
        args (concat ["docker"]
                     (when host ["--host" host])
                     command
                     (cond (= false format)
                           []

                           :else
                           ["--format" (or format "{{json .}}")])
                     tail)]
    (raw-exec args (merge {:json true} opts))))

(defn get-ip
  "gets the ip of a container"
  {:added "4.0"}
  [container-id]
  (first (raw-command ["inspect" "-f"
                       "{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}"
                       container-id]
                      {:format false
                       :json false})))

(defn list-containers
  "gets all local containers
 
   (list-containers)
   => vector?"
  {:added "4.0"}
  ([]
   (list-containers {} ["-f" "name=.*"]))
  ([opts tail]
   (raw-command ["ps"]
                (merge {:format "{\"id\":\"{{ .ID }}\", \"image\": \"{{ .Image }}\", \"name\":\"{{ .Names }}\"}"}
                       opts)
                (or tail ["-f" "name=.*"]))))

(defn has-container?
  "checks that a container exists"
  {:added "4.0"}
  ([{:keys [id group]
     :or {group "testing"}}]
   (not (empty? (filter #(-> % :name (= (str group "_" id)))
                        (list-containers))))))

(defn start-container
  "starts a container"
  {:added "4.0"}
  ([{:keys [group id image cmd flags labels volumes
            environment ports remove detached no-host] :as m
     :or {remove true detached true
          group "testing"}}
    & [repeat]]
   (let [args (reduce (fn [args [from to]]
                        (conj args "-v" (str from ":" to)))
                      []
                      volumes)
         args (reduce (fn [args [k v]]
                        (conj args "-l" (str k "=" v)))
                      args
                      labels)
         args (reduce (fn [args [k v]]
                        (conj args "-e" (str k "=" v)))
                      args
                      environment)
         args (if (not no-host)
                (conj args "--add-host=host.docker.internal:host-gateway")
                args)
         name (str group "_" (or id (h/error "Id required")))
         args (apply conj args "--name" name image cmd)
         cid  (cond (has-container? m)
                    nil
                    
                    :else
                    @(apply h/sh (concat ["docker" "--host" (or *host* "127.0.0.1") "run"]
                                         (if detached ["-d"])
                                         (if remove ["--rm"])
                                         flags args)))
         cid   (if (empty? cid)
                 @(h/sh "docker" "--host" (or *host* "127.0.0.1") "ps" "-aqf" (str "name=^" name "$"))
                 cid)
         ip   (get-ip cid)]
     (if (or repeat ip)
       (assoc m :container-id cid :container-ip ip :container-name name)
       (start-container m true)))))

(defn stop-container
  "stops a container"
  {:added "4.0"}
  ([{:keys [group id] :as m
     :or {group "testing"}}]
   (when (has-container? m)
     @(h/sh "docker"  "--host" (or *host* "127.0.0.1") "kill" (str group "_" (or id (h/error "Id required")))))))

