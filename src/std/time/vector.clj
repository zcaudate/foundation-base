(ns std.time.vector
  (:require [std.protocol.time :as protocol.time]
            [std.time.common :as common]
            [std.time.map :as map]))

(defn to-vector
  "converts an instant to an array representation
   (to-vector 0 {:timezone \"GMT\"} :all)
   => [1970 1 1 0 0 0 0]
 
   (to-vector (Date. 0) {:timezone \"GMT\"} :day)
   => [1970 1 1]
 
   (to-vector (common/calendar (Date. 0)
                               (TimeZone/getTimeZone \"EST\"))
              {}
              [:month :day :year])
   => [12 31 1969]
 
   (to-vector (common/calendar (Date. 0)
                               (TimeZone/getTimeZone \"EST\"))
              {:timezone \"GMT\"}
              [:month :day :year])
   => [1 1 1970]"
  {:added "3.0"}
  ([t {:keys [timezone] :as opts} ks]
   (cond (map? t)
         (if (or (nil? timezone)
                 (= timezone (:timezone opts)))
           (mapv t ks)
           (-> (map/from-map t (assoc opts :type java.util.Calendar))
               (to-vector opts ks)))

         :else
         (let [tmeta (protocol.time/-time-meta (class t))
               [p pmeta] (if-let [{:keys [proxy via]} (-> tmeta :map :to)]
                           [(via t opts) (protocol.time/-time-meta proxy)]
                           [t tmeta])
               p         (if timezone
                           (protocol.time/-with-timezone p timezone)
                           p)
               ks   (cond (vector? ks) ks

                          (= :all ks)
                          (reverse common/+default-keys+)

                          (keyword? ks)
                          (->> common/+default-keys+
                               (drop-while #(not= % ks))
                               (reverse)))
               rep  (reduce (fn [out k]
                              (let [t-fn (get common/+default-fns+ k)]
                                (conj out (t-fn p opts))))
                            []
                            ks)]
           rep))))
