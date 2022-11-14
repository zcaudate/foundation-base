(ns std.lang.base.runtime-wrap
  (:require [std.lib :as h]
            [std.string :as str]))

(defn wrap-start
  "install setup steps for rt keys"
  {:added "4.0"}
  ([f steps]
   (fn [rt]
     (h/-> rt
           (reduce (fn [rt {:keys [key setup]}]
                     (if-let [data (get rt key)]
                       (setup rt data)
                       rt))
                   %
                   (filter :setup steps))
           (f)
           (reduce (fn [rt {:keys [key start]}]
                     (if-let [data (get rt key)]
                       (start rt data)
                       rt))
                   %
                   (filter :start steps))))))

(defn wrap-stop
  "install teardown steps for rt keys"
  {:added "4.0"}
  ([f steps]
   (fn [rt]
     (h/-> rt
           (reduce (fn [rt {:keys [key stop]}]
                     (if-let [data (get rt key)]
                       (stop rt data)
                       rt))
                   %
                   (filter :stop steps))
           (f)
           (reduce (fn [rt {:keys [key teardown]}]
                     (if-let [data (get rt key)]
                       (teardown rt data)
                       rt))
                   %
                   (filter :teardown steps))))))
