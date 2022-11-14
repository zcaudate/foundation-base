(ns std.math.markov)

(defn cumulative
  "Reassemble the map of probabilities in cumulative order"
  {:added "3.0"}
  ([probabilities]
   (let [desc (sort-by (comp - second) probabilities)]
     (map list
          (map first desc)
          (reductions + (map second desc))))))

(defn select
  "Given a map of probabilities, select one at random."
  {:added "3.0"}
  ([probabilities]
   (select (rand) probabilities))

  ([r probabilities]
   (let [cumu (cumulative probabilities)
         maxv (or (second (last cumu)) 1)
         r (* r maxv)]
     (ffirst
      (drop-while
       #(> r (second %))
       cumu)))))

(defn generate
  "generate takes a probability matrix, and produces an infinite stream of 
  tokens, taking into consideration any remembered state
 
   (->> (collate (seq \"AEAEAAAAAEAAAAAAEEAEEAEAEEEAAAEAAAA\") 1)
        (generate)
        (take 10)
        (apply str))
   ;; \"EAAAEAAAAE\"
   => string?"
  {:added "3.0"}
  ([probabilities-matrix]
   (let [initial (rand-nth (keys probabilities-matrix))]
     (lazy-cat
      initial
      (generate
       initial
       probabilities-matrix))))

  ([state probabilities-matrix]
   (let [next-selection (select (get probabilities-matrix state))
         prev-state (vec (next state))]
     (if-not (nil? next-selection)
       (cons
        next-selection
        (lazy-seq
         (generate
          (conj prev-state next-selection)
          probabilities-matrix)))))))

(defn tally
  "helper function for collate"
  {:added "3.0"}
  ([x]
   (if (nil? x) 1 (inc x))))

(defn collate
  "generates an output probability map given a sequence
 
   (collate (seq \"AEAEAAAAAEAAAAAAEEAEEAEAEEEAAAEAAAA\") 3)
   => '{(\\A \\E \\A) {\\E 2, \\A 3},
        (\\E \\A \\E) {\\A 2, \\E 2},
        (\\E \\A \\A) {\\A 4},
        (\\A \\A \\A) {\\A 6, \\E 3},
        (\\A \\A \\E) {\\A 2, \\E 1},
        (\\A \\E \\E) {\\A 2, \\E 1},
        (\\E \\E \\A) {\\E 2, \\A 1},
       (\\E \\E \\E) {\\A 1}}"
  {:added "3.0"}
  ([tokens n]
   (reduce
    (fn [acc value]
      (update-in acc [(butlast value) (last value)] tally))
    {}
    (partition (inc n) 1 tokens))))
