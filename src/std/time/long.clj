(ns std.time.long
  (:require [std.protocol.time :as protocol.time]
            [std.time.coerce :as coerce]
            [std.time.common :as common])
  (:import (java.util Calendar Date TimeZone)))

(def long-meta
  {:base :instant
   :map  {:from {:proxy Calendar
                 :via (fn [^Calendar cal]
                        (.getTime (.getTime cal)))}
          :to   {:proxy Calendar
                 :via (fn [^Long t {:keys [timezone]}]
                        (common/calendar (Date. t)
                                         (coerce/coerce-zone timezone {:type TimeZone})))}}})

(defmethod protocol.time/-time-meta Long
  ([_]
   long-meta))

(extend-type Long
  protocol.time/IInstant
  (-to-long       [t] t)
  (-has-timezone? [t] false)
  (-get-timezone  [t] nil)
  (-with-timezone [t _] t)

  protocol.time/IDuration
  (-to-length     [d _] d))

(defmethod protocol.time/-from-long Long
  ([long _]
   long))

(defmethod protocol.time/-now Long
  ([_]
   (.getTime (Date.))))

(defmethod protocol.time/-from-length Long
  ([long _]
   long))
