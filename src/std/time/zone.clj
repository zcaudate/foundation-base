(ns std.time.zone
  (:import (java.util Calendar Date TimeZone)))

(def by-offset
  (->> (reduce (fn [out ^String id]
                 (update-in out [(.getRawOffset (TimeZone/getTimeZone id))]
                            (fnil #(conj % id) #{})))
               {}
               (TimeZone/getAvailableIDs))
       (reduce-kv (fn [out k ids]
                    (let [id (or (first (filter (fn [^String id] (.startsWith id "Etc")) ids))
                                 (first (sort-by count ids)))]
                      (assoc out k id)))
                  {})))

(defn pad-zeros
  "if number less than 10, make double digit
 
   (zone/pad-zeros \"5\") => \"05\""
  {:added "3.0"}
  ([s]
   (if (= 1 (count s))
     (str 0 s)
     s)))

(defn generate-offsets
  "make offsets in milliseconds
   (zone/generate-offsets)
   => ([\"00:00\" 0] [\"00:15\" 900000] .... [\"11:30\" 41400000] [\"11:45\" 42300000])"
  {:added "3.0"}
  ([]
   (for [i (range 0 12)
         j (range 0 60 15)]
     [(format "%s:%s"
              (pad-zeros (str i))
              (pad-zeros (str j)))
      (+ (* 3600000 i)
         (* 60000 j))])))

(def by-string-offset
  (let [half (generate-offsets)]
    (->> (concat (map (fn [[s val]] [(str "+" s) (- val)]) half)
                 (map (fn [[s val]] [(str "-" s) val]) half))
         (into {"Z" 0}))))
