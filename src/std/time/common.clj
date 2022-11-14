(ns std.time.common
  (:require [std.protocol.string :as protocol.string]
            [std.protocol.time :as protocol.time])
  (:import (java.util Calendar Date TimeZone)))

(def ^:dynamic *default-type* clojure.lang.PersistentArrayMap)

(def ^:dynamic *default-timezone* nil)

(defonce +default-keys+ [:millisecond
                         :second
                         :minute
                         :hour
                         :day
                         :month
                         :year])

(defonce +zero-values+  {:millisecond 0
                         :second 0
                         :minute 0
                         :hour 0
                         :day 1
                         :month 1})

(defonce +default-fns+
  {:millisecond #'protocol.time/-millisecond
   :second      #'protocol.time/-second
   :minute      #'protocol.time/-minute
   :hour        #'protocol.time/-hour
   :day         #'protocol.time/-day
   :day-of-week #'protocol.time/-day-of-week
   :month       #'protocol.time/-month
   :year        #'protocol.time/-year})

(defn calendar
  "creates a calendar to be used by the base date classes
   (-> ^Calendar (calendar (Date. 0) (TimeZone/getTimeZone \"GMT\"))
       (.getTime))
   => #inst \"1970-01-01T00:00:00.000-00:00\""
  {:added "3.0"}
  ([^Date date ^TimeZone timezone]
   (doto (Calendar/getInstance timezone)
     (.setTime date))))

(defn default-type
  "accesses the default type for datetime
 
   (default-type) ;; getter
   => clojure.lang.PersistentArrayMap
 
   (default-type Long) ;; setter
   => java.lang.Long"
  {:added "3.0"}
  ([] *default-type*)
  ([cls]
   (alter-var-root #'*default-type*
                   (constantly cls))))

(defn local-timezone
  "returns the current timezone as a string
 
   (local-timezone)
   => \"Asia/Ho_Chi_Minh\""
  {:added "3.0"}
  ([]
   (.getID (TimeZone/getDefault))))

(defn default-timezone
  "accesses the default timezone as a string
 
   (default-timezone)  ;; getter
   => \"Asia/Ho_Chi_Minh\"
 
   (default-timezone \"GMT\")  ;; setter
   => \"GMT\""
  {:added "3.0"}
  ([]
   (or *default-timezone*
       (local-timezone)))
  ([tz]
   (alter-var-root #'*default-timezone*
                   (constantly (protocol.string/-to-string tz)))))
