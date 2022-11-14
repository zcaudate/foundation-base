(ns std.time.coerce
  (:require [std.protocol.string :as protocol.string]
            [std.protocol.time :as protocol.time]
            [std.time.common :as common]
            [std.time.map :as map]))

(defn coerce-zone
  "coercion of one zone object to another
   (-> (coerce-zone \"Asia/Kolkata\" {:type TimeZone})
       (string/-to-string))
   => \"Asia/Kolkata\"
 
   (-> (coerce-zone nil {:type TimeZone})
       (string/-to-string))
   => (-> (TimeZone/getDefault)
          (string/-to-string))"
  {:added "3.0"}
  ([value {:keys [type] :as opts}]
   (cond (nil? value)
         (protocol.string/-from-string (common/default-timezone)
                                       type
                                       opts)

         (instance? type value)
         value

         (string? value)
         (protocol.string/-from-string value type opts)

         :else
         (->  value
              (protocol.string/-to-string)
              (protocol.string/-from-string type opts)))))

(defn coerce-instant
  "coerce-instant for java.time datastructures
   (coerce-instant 0 {:type Long
                      :timezone \"GMT\"})
   => 0
 
   (-> (coerce-instant 0 {:type ZonedDateTime
                          :timezone \"GMT\"})
       (map/to-map {} common/+default-keys+))
   => {:type ZonedDateTime
       :timezone \"GMT\", :long 0,
       :year 1970, :month 1, :day 1, :hour 0,
       :minute 0, :second 0 :millisecond 0}
 
   (-> (time/-from-long 0 {:type ZonedDateTime
                           :timezone \"GMT\"})
       (coerce-instant {:type Clock
                        :timezone \"Asia/Kolkata\"})
       (map/to-map {} common/+default-keys+))
   => {:type java.time.Clock$FixedClock,
       :timezone \"Asia/Kolkata\", :long 0,
       :year 1970, :month 1, :day 1, :hour 5,
       :minute 30, :second 0 :millisecond 0}
 
   (-> (time/-from-long 0 {:type Clock
                           :timezone \"GMT\"})
       (coerce-instant {:type Calendar
                        :timezone \"Asia/Kolkata\"})
       (map/to-map {} common/+default-keys+))
   => {:type java.util.GregorianCalendar
       :timezone \"Asia/Kolkata\", :long 0,
       :year 1970, :month 1, :day 1, :hour 5,
       :minute 30, :second 0 :millisecond 0}"
  {}
  ([value {:keys [type] :as opts}]
   (cond (instance? type value)
         value

         (or (= type Long) (nil? type))
         (protocol.time/-to-long value)

         (instance? Long value)
         (protocol.time/-from-long value opts)

         (instance? clojure.lang.APersistentMap value)
         (map/from-map value opts)

         :else
         (-> value
             (protocol.time/-to-long)
             (protocol.time/-from-long opts)))))
