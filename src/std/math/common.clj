(ns std.math.common
  (:refer-clojure :exclude [abs]))

(defn abs
  "returns the absolute value of `x`
 
   (abs -7) => 7
   (abs 7) => 7"
  {:added "3.0"}
  ([x]
   (if (neg? x) (- x) x)))

(defn square
  "calculates the square of a number
 
   (square 5)
   => 25"
  {:added "3.0"}
  ([x]
   (*' x x)))

(defn sqrt
  "calculates the square root of a number
 
   (sqrt 25)
   => 5.0"
  {:added "3.0"}
  ([x]
   (Math/sqrt x)))

(defn ceil
  "finds the ceiling of a number
 
   (ceil 0.1)
   => 1"
  {:added "3.0"}
  ([x]
   (long (Math/ceil x))))

(defn floor
  "finds the floor of a number
 
   (floor 0.1)
   => 0"
  {:added "3.0"}
  ([x]
   (long (Math/floor x))))

(defn factorial
  "calculates the factorial of `n`
 
   (factorial 4) => 24
 
   (factorial 10) => 3628800"
  {:added "3.0"}
  ([n]
   (reduce *' (range 1 (inc n)))))

(defn combinatorial
  "calculates the result of `n` choose `i`
 
   (combinatorial 4 2) => 6
 
   (combinatorial 4 3) => 4"
  {:added "3.0"}
  ([n i]
   (/  (factorial n)
       (factorial i)
       (factorial (- n i)))))

(defn log
  "calculates the logarithm base `b` of `x`
 
   (log 2 2) => 1.0
 
   (log 4 16) => 2.0"
  {:added "3.0"}
  ([b x]
   (/ (Math/log10 x)
      (Math/log10 b))))

(defn loge
  "calculates the natural log of `x`
 
   (loge 2) => 0.6931471805599453
 
   (loge 10) => 2.302585092994046"
  {:added "3.0"}
  ([x]
   (Math/log x)))

(defn log10
  "calculates the log base 10 of `x`
 
   (log10 2) => 0.3010299956639812
 
   (log10 10) => 1.0"
  {:added "3.0"}
  ([x]
   (Math/log10 x)))

(defn mean
  "calculates the average value of a set of data
 
   (mean [1 2 3 4 5])
   => 3
 
   (mean [1 1.6 7.4 10])
   => 5.0"
  {:added "3.0"}
  ([xs]
   (cond (empty? xs) 0

         :else
         (/ (apply +' xs)
            (count xs)))))

(defn mode
  "calculates the most frequent value of a set of data
 
   (mode [:alan :bob :alan :greg])
   => [:alan]
 
   (mode [:smith :carpenter :doe :smith :doe])
   => [:smith :doe]"
  {:added "3.0"}
  ([xs]
   (let [freqs (frequencies xs)
         occurrences (group-by val freqs)
         modes (last (sort occurrences))
         modes (->> modes
                    val
                    (map key))]
     modes)))

(defn median
  "calculates the middle value of a set of data
 
   (median [5 2 4 1 3])
   => 3
 
   (median [7 0 2 3])
   => 5/2"
  {:added "3.0"}
  ([xs]
   (let [xs  (sort xs)
         cnt (count xs)
         i1  (quot cnt 2)
         v1  (nth xs i1)]
     (if (odd? cnt)
       v1
       (/ (+ v1 (nth xs (dec i1))) 2)))))

(defn percentile
  "calculates the value within the population given a ratio
 
   (percentile [1 9 9 9 9] 0.5)
   => 9"
  {:added "3.0"}
  ([xs ratio]
   (let [c* (* ratio (dec (count xs)))
         c+ (Math/ceil c*) c- (Math/floor c*)]
     (if (= c+ c-)
       (nth xs (int c*))
       (let [xc- (nth xs (int c-))
             xc+ (nth xs (int c+))
             c** (rem c* 1.0)]
         (+ (* c** xc+) (* (- 1.0 c**) xc-)))))))

(defn quantile
  "splits the total population into equal quantiles
 
   (quantile [1 2 4 4 4 4 8 9] 4)
   => [3.5 4.0 5.0]"
  {:added "3.0"}
  ([xs n]
   (mapv (partial percentile (vec (sort xs)))
         (next (range 0 1 (/ 1.0 n))))))

(defn variance
  "calculates the average of the squared differences from the mean
 
   (variance [4 5 2 9 5 7 4 5 4])
   => 4"
  {:added "3.0"}
  ([xs]
   (let [avg  (mean xs)
         total (count xs)]
     (/ (apply +' (for [x xs]
                    (* (- x avg) (- x avg))))
        (dec total)))))

(defn stdev
  "calculates the standard deviation for a set of data
 
   (stdev [4 5 2 9 5 7 4 5 4])
   => 2.0"
  {:added "3.0"}
  ([xs]
   (Math/sqrt (variance xs))))

(defn skew
  "calculates the skewedness of the data
 
   (skew [4 5 2 9 5 7 4 5 4])
   => (approx 0.5833)"
  {:added "3.0"}
  ([xs]
   (skew xs (stdev xs)))
  ([xs stdev]
   (let [m (mean xs)]
     (/ (mean (for [x xs] (Math/pow (- x m) 3)))
        (Math/pow stdev 3)))))

(defn kurtosis
  "calculates the kurtosis of the data
 
   (kurtosis [4 5 2 9 5 7 4 5 4])
   => (approx 2.4722)"
  {:added "3.0"}
  ([xs]
   (kurtosis xs (stdev xs)))
  ([xs stdev]
   (let [m (mean xs)]
     (/ (mean (for [x xs] (Math/pow (- x m) 4)))
        (Math/pow stdev 4)))))

(defn histogram
  "creates a histogram of values
 
   (histogram [1 2 3 3 5 5 7 7 8 8 9] 4)
   => [4 2 2 3]"
  {:added "3.0"}
  ([xs] (histogram xs (min 16 (count xs))))
  ([xs n]
   (let [m- (apply min xs)
         m+ (apply max xs)
         d  (double (- m+ m-))
         w  (/ d n)
         f  (fn [x] (int (quot (- x m- 0.0000001) w)))
         gs (group-by f xs)]
     (mapv (comp count gs) (range 0 n)))))
