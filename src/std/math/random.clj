(ns std.math.random
  (:import (org.apache.commons.math3.random MersenneTwister))
  (:refer-clojure :exclude [rand rand-int rand-nth]))

(defonce +default+ (MersenneTwister.))

(defn rand-gen
  "creates a random number generator
 
   (rand-gen)
   => org.apache.commons.math3.random.MersenneTwister"
  {:added "3.0"}
  ([]
   (rand-gen nil))
  ([seed]
   (rand-gen seed :default))
  ([seed type]
   (cond-> (case type
             :default (MersenneTwister.))
     seed (doto (.setSeed ^long seed)))))

(defn rand-seed!
  "sets the seed of a given random number generator
 
   (-> (rand-gen)
       (rand-seed! 10)
       (rand))
   => (any 0.77132064549269
           0.5711645232847797)"
  {:added "3.0"}
  ([seed] (rand-seed! +default+ seed))
  ([^MersenneTwister rng ^long seed]
   (doto rng (.setSeed seed))))

(defn rand
  "returns a random double between 0 and 1
 
   (rand)
   ;;0.19755427425784822
   => number?
 
   (rand (rand-gen))
   ;;0.8479218396605446
   => number?"
  {:added "3.0"}
  ([] (rand +default+))
  ([^MersenneTwister rng] (.nextDouble rng)))

(defn rand-int
  "returns a random integer less than `n`
 
   (rand-int 100)
   ;; 16
   => integer?"
  {:added "3.0"}
  ([n] (rand-int n +default+))
  ([n ^MersenneTwister rng] (.nextLong rng n)))

(defn rand-nth
  "returns a random element in an array
 
   (rand-nth [:a :b :c])
   => #{:a :b :c}"
  {:added "3.0"}
  ([coll]
   (rand-nth coll +default+))
  ([coll rng]
   (nth coll (rand-int (count coll) rng))))

(defn rand-normal
  "returns a random number corresponding to the normal distribution
 
   (rand-normal)
   ;;-0.6591021470679017
   => number?"
  {:added "3.0"}
  ([]
   (rand-normal +default+))
  ([rng]
   (* (Math/sqrt (* -2.0 (Math/log (rand rng))))
      (Math/cos (* 2.0 Math/PI (rand rng))))))

(defn rand-digits
  "constructs a n digit string
 
   (rand-digits 10)
   ;; \"9417985847\"
   => string?"
  {:added "3.0"}
  ([n]
   (let [num (nth (iterate #(* 10 %) 1) n)]
     (subs (str (+ (rand-int num) num)) 1))))

(defn rand-sample
  "takes from the collection given specified proportions
 
   (rand-sample [:a :b :c] [1 2 3])
   => keyword?"
  {:added "3.0"}
  ([coll proportions]
   (let [sample (* (apply + proportions) (rand))
         index  (reduce (fn [{:keys [index sum]} v]
                          (let [sum (+ v sum)]
                            (if (>= sum sample)
                              (reduced index)
                              {:index (inc index)
                               :sum sum})))
                        {:index 0
                         :sum 0}
                        proportions)]
     (nth coll index))))
