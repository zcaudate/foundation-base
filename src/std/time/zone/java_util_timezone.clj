(ns std.time.zone.java-util-timezone
  (:require [std.protocol.string :as protocol.string]
            [std.protocol.time :as protocol.time])
  (:import (java.util TimeZone)))

(defmethod protocol.time/-time-meta TimeZone
  ([_]
   {:base :zone}))

(extend-type TimeZone
  protocol.string/IString
  (-to-string [tz] (.getID tz)))

(defmethod protocol.string/-from-string TimeZone
  ([^String string _ _]
   (TimeZone/getTimeZone string)))
