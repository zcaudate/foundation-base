(ns code.query.match.optional
  (:require [code.query.common :as common]))

(defn tag-meta
  "increment a counter everytime a `:?` element is seen
   (let [out (atom {:? -1})]
     (tag-meta ^:? () out)
     @out)
   => {:? 0}"
  {:added "3.0"}
  ([ele out]
   (let [mele (meta ele)]
     (cond (:? mele)
           (do (swap! out update-in [:?] inc)
               (with-meta ele (assoc mele :? (:? @out))))

           :else ele))))

(defn pattern-seq
  "generate a sequence of possible matches
   (pattern-seq '(+ ^:? (1) ^:? (^:? + 2)))
   => '((+)
        (+ (1))
        (+ (2))
        (+ (1) (2))
        (+ (+ 2))
        (+ (1) (+ 2)))"
  {:added "3.0"}
  ([pattern]
   (let [out      (atom {:? -1})
         pattern (common/prewalk #(tag-meta % out) pattern)
         combos   (range (bit-shift-left 1 (inc (:? @out))))]
     (distinct
      (for [combo combos]
        (let [hide? #(if-let [num (-> % meta :?)]
                       (-> combo
                           (bit-shift-right num)
                           (mod 2)
                           (= 0)))]
          (common/remove-items hide? pattern)))))))
