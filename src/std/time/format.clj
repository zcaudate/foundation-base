(ns std.time.format
  (:require [std.protocol.time :as protocol.time]
            [std.time.common :as common])
  (:refer-clojure :exclude [format]))

;; Common set of time references:
;;
;; s - seconds in minute
;; m - minutes in hour
;; H - hours in day
;; d - day in month
;; M - month in year
;; y - year
;; Z - timezone

;; Please refer to the specific formatter strings for the following:

;; java.text.SimpleDateFormat
;; (https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)
;;  - java.util.Date
;;  - java.util.Calendar
;;  - java.sql.Timestamp

;; java.time.format.DateTimeFormatter
;; (https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)
;; - java.time.Instant
;; - java.time.Clock
;; - java.time.ZonedDateTime

(defonce +format-cache+ (atom {}))

(defonce +parse-cache+  (atom {}))

(defn cache
  "helper function to access formatters by keyword
 
   (f/cache f/+format-cache+
            nil
           [java.text.SimpleDateFormat \"HH MM dd Z\"]
            true)"
  {:added "3.0"}
  ([cache constructor ks flag]
   (cond flag
         (or (get-in @cache ks)
             (let [obj (constructor)]
               (swap! cache assoc-in ks obj)
               obj))

         :else
         (constructor))))

(defn format
  "converts a date into a string
 
   (f/format (Date. 0) \"HH MM dd Z\" {:timezone \"GMT\" :cached true})
   => \"00 01 01 +0000\"
 
   (f/format (common/calendar (Date. 0)
                              (TimeZone/getTimeZone \"GMT\"))
             \"HH MM dd Z\"
             {})
   => \"00 01 01 +0000\"
 
   (f/format (Timestamp. 0)
             \"HH MM dd Z\"
             {:timezone \"PST\"})
   => \"16 12 31 -0800\"
 
   (f/format (Date. 0) \"HH MM dd Z\")
   => string?"
  {:added "3.0"}
  ([t pattern] (format t pattern {}))
  ([t pattern {:keys [cached] :as opts}]
   (let [tmeta (protocol.time/-time-meta (class t))
         ftype (-> tmeta :formatter :type)
         fmt   (cache +format-cache+
                      (fn [] (protocol.time/-formatter pattern (assoc opts :type ftype)))
                      [ftype pattern]
                      cached)]
     (protocol.time/-format fmt t opts))))

(defn parse
  "testing format for java.time datastructures
   (-> (f/parse \"00 00 01 01 01 1989 +0000\" \"ss mm HH dd MM yyyy Z\"
                {:type Clock})
       (map/to-map {} common/+default-keys+))
   => {:type java.time.Clock$FixedClock,
       :timezone \"Etc/GMT\"
       :long 599619600000,
       :year 1989, :month 1, :day 1,
       :hour 1, :minute 0, :second 0, :millisecond 0}
 
   (-> (f/parse \"00 00 01 01 01 1989 -1000\" \"ss mm HH dd MM yyyy Z\"
                {:type Clock})
       (map/to-map {} common/+default-keys+))
   => {:type java.time.Clock$FixedClock, :timezone \"Etc/GMT-10\", :long 599583600000,
       :year 1989, :month 1, :day 1, :hour 1, :minute 0, :second 0, :millisecond 0}
 
   (-> (f/parse \"00 00 01 01 01 1989 +1000\" \"ss mm HH dd MM yyyy Z\"
                {:type Clock})
       (map/to-map {} common/+default-keys+))
   => {:type java.time.Clock$FixedClock, :timezone \"Etc/GMT+10\", :long 599655600000,
       :year 1989, :month 1, :day 1, :hour 1, :minute 0, :second 0, :millisecond 0}"
  {}
  ([s pattern] (parse s pattern {}))
  ([s pattern {:keys [cached] :as opts}]
   (let [opts   (merge {:type common/*default-type*} opts)
         type   (:type opts)
         tmeta  (protocol.time/-time-meta type)
         ptype  (-> tmeta :parser :type)
         parser (cache +parse-cache+
                       (fn [] (protocol.time/-parser pattern (assoc opts :type ptype)))
                       [ptype pattern]
                       cached)]
     (protocol.time/-parse parser s opts))))
