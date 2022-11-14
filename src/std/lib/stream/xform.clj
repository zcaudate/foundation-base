(ns std.lib.stream.xform
  (:require [std.lib.collection :as c]
            [std.lib.foundation :as h]
            [std.lib.time :as t])
  (:import (java.util.concurrent.atomic AtomicLong)
           (java.util ArrayDeque)))

(defn x:map
  "map transducer"
  {:added "3.0"}
  ([] (x:map identity))
  ([f]
   (fn [rf]
     (fn rf:map
       ([] (rf))
       ([result] (rf result))
       ([result input]
        (rf result (f input)))
       ([result input & inputs]
        (rf result (apply f input inputs)))))))

(defn x:map-indexed
  "map-indexed transducer"
  {:added "3.0"}
  ([f]
   (fn [rf]
     (let [i (volatile! -1)]
       (fn rf:map-indexed
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (rf result (f (vswap! i inc) input))))))))

(defn x:filter
  "filter transducer"
  {:added "3.0"}
  ([] (x:filter identity))
  ([pred]
   (fn [rf]
     (fn rf:filter
       ([] (rf))
       ([result] (rf result))
       ([result input]
        (if (pred input)
          (rf result input)
          result))))))

(defn x:remove
  "remove transducer"
  {:added "3.0"}
  ([] (x:remove (comp not identity)))
  ([f] (x:filter (comp not f))))

(defn x:keep
  "keep transducer"
  {:added "3.0"}
  ([] (x:keep identity))
  ([f]
   (fn [rf]
     (fn rf:keep
       ([] (rf))
       ([result] (rf result))
       ([result input]
        (let [v (f input)]
          (if (nil? v)
            result
            (rf result v))))))))

(defn x:keep-indexed
  "keep-indexed transducer"
  {:added "3.0"}
  ([f]
   (fn [rf]
     (let [iv (volatile! -1)]
       (fn
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (let [i (vswap! iv inc)
                v (f i input)]
            (if (nil? v)
              result
              (rf result v)))))))))

(defn x:prn
  "prn transducer"
  {:added "3.0"}
  ([]
   (x:prn identity))
  ([f]
   (x:map (fn [v] (doto v (-> f prn))))))

(defn x:peek
  "peek transducer"
  {:added "3.0"}
  ([]
   (x:peek identity))
  ([f]
   (x:map (fn [v] (doto v f)))))

(defn x:delay
  "delay transducer"
  {:added "3.0"}
  ([ms]
   (let [ms-fn (cond (fn? ms) ms :else (constantly ms))]
     (x:map (fn [v] (Thread/sleep (ms-fn)) v)))))

(defn x:mapcat
  "mapcat transducer"
  {:added "3.0"}
  ([] (x:mapcat identity))
  ([f] (comp (x:map f) cat)))

(defn x:pass
  "identity transducer"
  {:added "3.0"}
  ([] (x:pass nil))
  ([init]
   (fn [rf]
     (fn rf:pass
       ([] (or init (rf)))
       ([acc] (rf acc))
       ([acc k] (rf acc k))))))

(defn x:apply
  "applies a reduction function
 
   @(reduce (x:apply (fn
                       ([])
                       ([_])
                       ([_ x] (reduced x))))
            (range 5))
   => 1"
  {:added "3.0"}
  ([rf]
   (fn rf:apply
     ([])
     ([acc] acc)
     ([acc & args]
      (let [acc (apply rf acc args)]
        (if (reduced? acc)
          (reduced acc)
          acc))))))

(defn x:reduce
  "transducer for accumulating results"
  {:added "3.0"}
  ([f]
   (fn [rf]
     (let [vacc (volatile! (f))]
       (fn rf:reduce
         ([] (rf))
         ([acc]
          (let [v @vacc]
            (when-not (identical? v vacc)
              (vreset! vacc vacc)
              (rf (unreduced (rf acc (f (unreduced v))))))))
         ([acc & args]
          (if (reduced? (vswap! vacc #(apply f % args)))
            (reduced acc)
            acc))))))
  ([f init]
   (x:reduce ((x:pass init) f))))

(defn x:take
  "take transducer"
  {:added "3.0"}
  ([] (x:map))
  ([n]
   (fn [rf]
     (let [nv (volatile! n)]
       (fn rf:take
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (let [n @nv
                nn (vswap! nv dec)
                result (if (pos? n)
                         (rf result input)
                         result)]
            (if (not (pos? nn))
              (ensure-reduced result)
              result))))))))

(defn x:take-last
  "take-last transducer"
  {:added "3.0"}
  ([^long n]
   (fn [rf]
     (let [^ArrayDeque dq (ArrayDeque. n)]
       (fn rf:take-last
         ([] (rf))
         ([acc] (transduce (x:map #(if (identical? dq %) nil %)) rf acc dq))
         ([acc x]
          (.add dq (if (nil? x) dq x))
          (when (< n (.size dq)) (.poll dq))
          acc))))))

(defn x:drop
  "drop transducer"
  {:added "3.0"}
  ([] (x:take 0))
  ([n]
   (fn [rf]
     (let [nv (volatile! n)]
       (fn rf:drop
         ([] (rf))
         ([result] (rf result))
         ([result input]
          (let [n @nv]
            (vswap! nv dec)
            (if (pos? n)
              result
              (rf result input)))))))))

(defn x:drop-last
  "drop-last transducer"
  {:added "3.0"}
  ([] (x:drop-last 1))
  ([^long n]
   (fn [rf]
     (let [dq (java.util.ArrayDeque. n)
           xform (x:map #(if (identical? dq %) nil %))
           rf (xform rf)]
       (fn
         ([] (rf))
         ([acc] (rf acc))
         ([acc x]
          (.add dq (if (nil? x) dq x))
          (if (< n (.size dq))
            (rf acc (.poll dq))
            acc)))))))

(defn x:butlast
  "butlast transducer"
  {:added "3.0"}
  ([] (x:drop-last 1)))

;;
;; some
;;

(defn x:some
  "some transducer"
  {:added "3.0"}
  ([] (x:reduce
       (fn rf:some
         ([] nil)
         ([x] x)
         ([_ x] (when x (reduced x)))))))

;;
;; stats
;;

(defn x:last
  "last transducer"
  {:added "3.0"}
  ([] (x:reduce (fn rf:last
                  ([] nil)
                  ([x] x)
                  ([_ x] x)))))

(defn x:count
  "count transducer"
  {:added "3.0"}
  ([]
   (let [^AtomicLong n (AtomicLong.)]
     (fn [rf]
       (fn rf:count
         ([] (rf))
         ([acc] (rf (unreduced (rf acc (.get n)))))
         ([acc _] (.incrementAndGet n) acc))))))

(defn x:min
  "min transducer"
  {:added "3.0"}
  ([] (x:min compare))
  ([^java.util.Comparator comparator]
   (x:reduce (fn rf:min
               ([] nil)
               ([x] x)
               ([a b] (cond (nil? a) b
                            (nil? b) a
                            (pos? (.compare comparator a b)) b
                            :else a))))))

(defn x:max
  "max transducer"
  {:added "3.0"}
  ([] (x:max compare))
  ([^java.util.Comparator comparator]
   (x:reduce (fn rf:max
               ([] nil)
               ([x] x)
               ([a b] (cond (nil? a) b
                            (nil? b) a
                            (neg? (.compare comparator a b)) b
                            :else a))))))

(defn x:mean
  "mean transducer"
  {:added "3.0"}
  ([]
   (x:reduce (fn rf:mean
               ([] nil)
               ([^doubles acc] (when acc (/ (aget acc 1) (aget acc 0))))
               ([^doubles acc x]
                (let [acc (or acc (double-array 2))]
                  (doto acc
                    (aset 0 (inc (aget acc 0)))
                    (aset 1 (+ (aget acc 1) x)))))))))

(defn x:stdev
  "stdev transducer
 
   (sequence (x:stdev)
             (range 5))
   => '(1.5811388300841898)"
  {:added "3.0"}
  ([] (x:reduce (fn rf:stdev
                  ([] (double-array 3))
                  ([^doubles a]
                   (let [s (aget a 0) n (aget a 2)]
                     (if (< 1 n)
                       (Math/sqrt (/ s (dec n)))
                       0.0)))
                  ([^doubles a x]
                   (let [s (aget a 0) m (aget a 1) n (aget a 2)
                         d (- x m)
                         n (inc n)
                         m' (+ m (/ d n))]
                     (doto a
                       (aset 0 (+ s (* d (- x m'))))
                       (aset 1 m')
                       (aset 2 n))))))))

;;
;; str
;;

(defn- str:builder
  "Like xforms/str but returns a StringBuilder."
  ([] (StringBuilder.))
  ([^StringBuilder sb] sb)
  ([^StringBuilder sb x] (.append sb x)))

(defn x:str
  "str transducer"
  {:added "3.0"}
  ([] (x:reduce (completing str:builder str))))

(defn x:sort
  "sort transducer"
  {:added "3.0"}
  ([] (x:sort compare))
  ([cmp]
   (fn [rf]
     (let [buf (java.util.ArrayList.)]
       (fn rf:sort
         ([] (rf))
         ([acc] (rf (reduce rf acc (doto buf (java.util.Collections/sort cmp)))))
         ([acc x] (.add buf x) acc))))))

(defn x:sort-by
  "sort-by transducer"
  {:added "3.0"}
  ([xf] (x:sort-by xf compare))
  ([xf cmp]
   (x:sort (fn [a b]
             (.compare ^java.util.Comparator cmp (xf a) (xf b))))))

(defn x:reductions
  "reductions transducer"
  {:added "3.0"}
  ([f] (x:reductions f (f)))
  ([f init]
   (fn [rf]
     (let [prev (volatile! nil)]
       (vreset! prev prev) ; cheap sentinel to detect the first call, this is done to avoid having a 1-item delay
       (fn rf:reductions
         ([] (rf)) ; no you can't emit init there since there's no guarantee that this arity is going to be called
         ([acc] (if (identical? @prev prev)
                  (rf (unreduced (rf acc init)))
                  (rf acc)))
         ([acc x]
          (if (identical? @prev prev)
            (let [acc (rf acc (vreset! prev init))]
              (if (reduced? acc)
                acc
                (recur acc x)))
            (let [curr (vswap! prev f x)]
              (if (reduced? curr)
                (ensure-reduced (rf acc @curr))
                (rf acc curr))))))))))

(defn x:wrap
  "wrap transducer"
  {:added "3.0"}
  ([open close]
   (fn [rf]
     (let [vrf (volatile! nil)]
       (vreset! vrf
                (fn [acc x]
                  (let [acc (rf acc open)]
                    (vreset! vrf rf)
                    (if (reduced? acc)
                      acc
                      (rf acc x)))))
       (fn rf:wrap
         ([] (rf))
         ([acc] (rf (unreduced (rf acc close))))
         ([acc x] (@vrf acc x)))))))

(defn x:time
  "timing transducer"
  {:added "3.0"}
  ([xform] (x:time "Elapsed time" xform))
  ([tag-or-f xform]
   (let [pt (if (fn? tag-or-f)
              tag-or-f
              #(println (str tag-or-f ": " % " msecs")))]
     (fn [rf]
       (let [at (AtomicLong.)
             rf
             (fn
               ([] (rf))
               ([acc] (let [t (t/time-ns)
                            r (rf acc)]
                        (.addAndGet at (- t (t/time-ns)))
                        r))
               ([acc x]
                (let [t (t/time-ns)
                      r (rf acc x)]
                  (.addAndGet at (- t (t/time-ns)))
                  r)))
             rf (xform rf)]
         (fn
           ([] (rf))
           ([acc]
            (let [t (t/time-ns)
                  r (rf acc)
                  total (.addAndGet at (- (t/time-ns) t))]
              (pt (* total 1e-6))
              r))
           ([acc x]
            (let [t (t/time-ns)
                  r (rf acc x)]
              (.addAndGet at (- (t/time-ns) t))
              r))))))))

(defn x:window
  "returns a window of elements"
  {:added "3.0"}
  ([n]
   (let [f    (fn
                ([] (c/queue))
                ([acc] acc)
                ([acc x] (conj acc x)))
         invf (fn ([acc _] (pop acc)))]
     (x:window n f invf)))
  ([n f invf]
   (fn [rf]
     (let [ring (object-array n)
           vi (volatile! (- n))
           vwacc (volatile! (f))]
       (fn x:window
         ([] (rf))
         ([acc] (rf acc))
         ([acc x]
          (let [i @vi
                wacc @vwacc] ; window accumulator
            (if (neg? i) ; not full yet
              (do
                (aset ring (+ n i) x)
                (vreset! vi (inc i))
                (rf acc (f (vreset! vwacc (f wacc x)))))
              (let [x' (aget ring i)]
                (aset ring i x)
                (vreset! vi (let [i (inc i)] (if (= n i) 0 i)))
                (rf acc (f (vreset! vwacc (f (invf wacc x') x)))))))))))))
