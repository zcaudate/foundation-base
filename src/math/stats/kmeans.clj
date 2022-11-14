(ns math.stats.kmeans)

(defn distance2
  "sum of squared distances between two points
 
   (distance2 [1 3 5] [2 4 6])
   => 3"
  {:added "3.0"}
  ([p0 pc]
   (->> (map - p0 pc) (map #(* % %)) (apply +'))))

(defn min-index
  "finds the index of the centroid closest to `p0`
 
   (min-index [0 0] [[-1 -1] [0.5 0.5] [1 1]])
   => 1"
  {:added "3.0"}
  ([p0 centroids]
   (let [xs (mapv #(distance2 p0 %) centroids)]
     (.indexOf xs (apply min xs)))))

(defn cluster
  "takes an array centroids and groups points based upon closest distance
 
   (cluster [[0 0] [1 1] [2 2] [3 3] [4 4] [5 5] [6 6]]
            [[1 1] [4 4]])
   => [[[0 0] [1 1] [2 2]]
       [[3 3] [4 4] [5 5] [6 6]]]"
  {:added "3.0"}
  ([ps centroids]
   (let [groups (vec (repeat (count centroids) []))]
     (reduce (fn [out p]
               (let [i (min-index p centroids)]
                 (update-in out [i] conj p)))
             groups
             ps))))

(defn centroid
  "finds the central position of  a group of points
 
   (centroid [[3 3] [4 4] [5 5] [6 6]])
   => [9/2 9/2]"
  {:added "3.0"}
  ([ps]
   (let [len (count ps)]
     (apply mapv (fn [& xs] (/ (apply +' xs) len)) ps))))

(defn initial-centroids
  "finds a set of centroids from the initial set of points"
  {:added "3.0"}
  ([ps k]
   (->> (partition (quot (count ps) k) ps)
        (map rand-nth))))

(defn k-means
  "clusters points together based on the number of groups specified
 
   (set (k-means [[0.2 0.4] [0.2 0.3] [0.3 0.4]
                  [1.2 0.4] [1.1 0.3] [2.3 0.4]]
                 2))
   => (any #{[[0.2 0.4] [0.2 0.3] [0.3 0.4]]
             [[1.2 0.4] [1.1 0.3] [2.3 0.4]]}
           #{[[0.2 0.4] [0.2 0.3] [0.3 0.4] [1.2 0.4] [1.1 0.3]]
             [[2.3 0.4]]})"
  {:added "3.0"}
  ([ps k]
   (k-means ps k 1000))
  ([ps k max-steps]
   (loop [trial       0
          centroids   (initial-centroids ps k)]
     (let [candidate  (cluster ps centroids)
           ncentroids (mapv centroid candidate)]
       (cond (> trial max-steps)
             (throw (Exception. "Kmeans goes past the maximum number of steps"))

             (= centroids ncentroids)
             candidate

             :else
             (recur (inc trial) ncentroids))))))
