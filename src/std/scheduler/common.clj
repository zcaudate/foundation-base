(ns std.scheduler.common
  (:require [std.concurrent :as cc]))

(defn new-runtime
  "contructs a new runtime for runner
 
   (def -rt- (new-runtime))"
  {:added "3.0"}
  ([]
   (new-runtime {}))
  ([{:keys [pool]}]
   (let [scheduler  (cc/executor:scheduled 2)
         core       (cc/executor:cached)]
     {:scheduler scheduler
      :core core
      :programs {}
      :past     {}
      :running  {}})))

(defn stop-runtime
  "stops the executors in the new instance
 
   (stop-runtime (new-runtime))"
  {:added "3.0"}
  ([{:keys [scheduler core] :as runtime}]
   (cc/exec:shutdown scheduler)
   (cc/exec:shutdown core)
   runtime))

(defn kill-runtime
  "kills all objects in the runtime
 
   (kill-runtime (new-runtime))"
  {:added "3.0"}
  ([{:keys [scheduler core] :as runtime}]
   (cc/exec:shutdown-now scheduler)
   (cc/exec:shutdown-now core)
   runtime))

(defn all-ids
  "returns all running program ids"
  {:added "3.0"}
  ([f runner]
   (->> (map (fn [id]
               [id (f runner id)])
             (keys (:running @(:runtime runner))))
        (into {}))))

(defn spawn-form
  "generate a spawn/runtime form"
  {:added "3.0"}
  ([[sym spawn-sym]]
   (let [spawn-var (resolve spawn-sym)
         {:keys [arglists doc]} (meta spawn-var)]
     `(defn ~sym
        ~(or doc "")
        {:arglists ~(list 'quote (map #(apply vector 'runner (rest %)) arglists))}
        ~'[runner & args]
        (apply ~spawn-sym (:runtime ~'runner) ~'args)))))

(defmacro gen-spawn
  "generates a spawn/runtime forms"
  {:added "3.0"}
  ([& pairs]
   (mapv spawn-form pairs)))

(defn spawn-all-form
  "generates all forms"
  {:added "3.0"}
  ([[sym spawn-sym]]
   (let [spawn-var (resolve spawn-sym)
         {:keys [arglists doc]} (meta spawn-var)]
     `(defn ~sym
        ~(or doc "")
        (~'[runner]
         (all-ids ~sym ~'runner))
        (~'[runner id]
         (~spawn-sym (:runtime ~'runner) ~'id))))))

(defmacro gen-spawn-all
  "generates all spawn/runiime forms"
  {:added "3.0"}
  ([& pairs]
   (mapv spawn-all-form pairs)))
