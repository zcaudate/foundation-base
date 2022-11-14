(ns std.time.duration
  (:require [std.protocol.time :as protocol.time]
            [std.time.map :as map]))

(def forward-year-array  [366 365 365 365])

(def forward-month-array [31 29 31 30 31 30 31 31 30 31 30 31
                          31 28 31 30 31 30 31 31 30 31 30 31
                          31 28 31 30 31 30 31 31 30 31 30 31
                          31 28 31 30 31 30 31 31 30 31 30 31])

(def backward-year-array  [365 366 365 365])

(def backward-month-array [31 31 29 31 30 31 30 31 31 30 31 30
                           31 31 28 31 30 31 30 31 31 30 31 30
                           31 31 28 31 30 31 30 31 31 30 31 30
                           31 31 28 31 30 31 30 31 31 30 31 30])

(def variable-keys [:months :years])

(def ms-length [[:weeks        604800000]
                [:days         86400000]
                [:hours        3600000]
                [:minutes      60000]
                [:seconds      1000]
                [:milliseconds 1]])

(def adjust-options
  {:forward  {:month {:array forward-month-array}
              :year  {:array forward-year-array
                      :pred  [[(fn [yidx month day]
                                 (and (zero? yidx)
                                      (or (> month 2)
                                          (and (= month 2)
                                               (= day 29)))))
                               dec]
                              [(fn [yidx month day]
                                 (and (= 3 yidx)
                                      (> month 2)))
                               inc]]}}
   :backward {:month {:array backward-month-array}
              :year  {:array backward-year-array
                      :pred [[(fn [yidx month day]
                                (and (zero? yidx)
                                     (or (> month 2)
                                         (and (= month 2)
                                              (= day 29)))))
                              inc]
                             [(fn [yidx month day]
                                (and (= 1 yidx)
                                     (> month 2)))
                              dec]]}}})

(defn adjust-year-days
  "calculates the number of days to be adjusted based on year
   (adjust-year-days 1 {:year 2012 :month 2 :day 28})
   => 366
 
   (adjust-year-days 1 {:year 2012 :month 2 :day 29})
   => 365
 
   (adjust-year-days 1 {:year 2012 :month 3 :day 1})
   => 365
 
   (adjust-year-days 1 {:year 2011 :month 3 :day 1})
   => 366"
  {:added "3.0"}
  ([years {:keys [year month day backward]}]
   (let [yidx (rem year 4)
         year-opts (-> adjust-options
                       ((if backward :backward :forward))
                       :year)
         year-days (->> (cycle (:array year-opts))
                        (drop yidx)
                        (take years)
                        (apply +))]
     (or (if (zero? year-days) year-days)
         (if-let [[_ f]
                  (first (filter (fn [[pred _]]
                                   (pred yidx month day))
                                 (:pred year-opts)))]
           (f year-days)
           year-days)))))

(defn adjust-month-days
  "calculates the number of days to be adjusted based on month
   (adjust-month-days 0 2 {:year 2012 :month 3 :day 1})
   => 61
 
   (adjust-month-days 0 2 {:year 2012 :month 3 :day 1 :backward true})
   => 60
 
   (adjust-month-days 1 2 {:year 2011 :month 1 :day 3})
   => 60
 
   (adjust-month-days 1 2 {:year 2012 :month 1 :day 3})
   => 59"
  {:added "3.0"}
  ([years months {:keys [year month day backward]}]
   (let [midx (rem (-> (+ year years)
                       (* 12)
                       (+ (dec month)))
                   48)
         month-opts (-> adjust-options
                        ((if backward :backward :forward))
                        :month)]
     (->> (cycle (:array month-opts))
          (drop midx)
          (take months)
          (apply +)))))

(defn adjust-days
  "calculates the number of days to be forwarded based on year and month
   (adjust-days {:years 0 :months 2} {:year 2012 :month 1 :day 31})
   => 60
 
   (adjust-days {:years 1 :months 2} {:year 2012 :month 1 :day 31})
   => 425
 
   (adjust-days {:months 2} {:year 2012 :month 1 :day 31 :backward true})
   => 62
 
   (adjust-days {:years 1} {:year 2013 :month 1 :day 31 :backward true})
   => 366"
  {:added "3.0"}
  ([{:keys [years months]
     :or   {years 0 months 0}}
    {:keys [year month day] :as rep}]
   (if-not (and year month day)
     (throw (Exception. (format "Year (%s), Month (%s) and Day (%s) are required:" year month day)))) (let [year-days  (adjust-year-days years rep)
                                                                                                            month-days (adjust-month-days years months rep)]
                                                                                                        (+ year-days month-days))))

(defn to-fixed-length
  "converts a duration map to a duration in milliseconds
   (to-fixed-length {:days 1 :hours 3})
   => 97200000
 
   (to-fixed-length {:weeks 2 :days 7 :hours 53})
   => 2005200000"
  {:added "3.0"}
  ([m]
   (reduce (fn [len [k mul]]
             (+ len (* mul (or (get m k) 0))))
           0
           ms-length)))

(defn map-to-length
  "converts a duration to a length
   (map-to-length {:months 2} {:year 2012 :month 1 :day 31 :backward true})
   => 5356800000"
  {:added "3.0"}
  ([d rep]
   (+ (if (some d [:months :years])
        (* 86400000 (adjust-days d rep))
        0)
      (to-fixed-length d))))

(defmethod protocol.time/-from-length :default
  ([long _]
   (first (reduce (fn [[out long] [k num]]
                    (let [[q r] [(quot long num) (rem long num)]]
                      [(assoc out k q) r]))
                  [{} long]
                  ms-length))))

(extend-type clojure.lang.APersistentMap
  protocol.time/IDuration
  (-to-length [d rep]
    (map-to-length d (if (map? rep)
                       rep
                       (map/to-map rep {} [:year :month :day])))))
