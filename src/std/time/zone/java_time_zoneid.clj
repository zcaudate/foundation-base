(ns std.time.zone.java-time-zoneid
  (:require [std.protocol.string :as protocol.string]
            [std.protocol.time :as protocol.time])
  (:import (java.time ZoneId)))

(defmethod protocol.time/-time-meta ZoneId
  ([_]
   {:base :zone}))

(extend-type ZoneId
  protocol.string/IString
  (-to-string [tz]
    (.getId tz)))

(defmethod protocol.string/-from-string ZoneId
  ([^String string _ _]
   (ZoneId/of string ZoneId/SHORT_IDS)))

