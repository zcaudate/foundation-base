(ns std.image.base.util)

(def type-lookup
  {Byte/TYPE    {:size 8
                 :array-fn byte-array
                 :unchecked-fn unchecked-byte
                 :aset-fn aset-byte}
   Short/TYPE   {:size 16
                 :array-fn short-array
                 :unchecked-fn unchecked-short
                 :aset-fn aset-short}
   Integer/TYPE {:size 32
                 :array-fn int-array
                 :unchecked-fn unchecked-int
                 :aset-fn aset-int}
   Long/TYPE    {:size 64
                 :array-fn long-array
                 :unchecked-fn unchecked-long
                 :aset-fn aset-long}
   Float/TYPE   {:size 32
                 :array-fn float-array
                 :unchecked-fn unchecked-float
                 :aset-fn aset-float}
   Double/TYPE  {:size 64
                 :array-fn double-array
                 :unchecked-fn unchecked-double
                 :aset-fn aset-double}})

(defn int->bytes
  "returns bytes value given an integer
 
   (int->bytes 4278854200)
   => [255 10 34 56]"
  {:added "3.0"}
  ([v]
   [(bit-and (bit-shift-right v 24) 255)
    (bit-and (bit-shift-right v 16) 255)
    (bit-and (bit-shift-right v  8) 255)
    (bit-and (bit-shift-right v  0) 255)]))

(defn bytes->int
  "returns the integer value for bytes
 
   (bytes->int [255 10 34 56])
   => 4278854200"
  {:added "3.0"}
  ([[b3 b2 b1 b0]]
   (bit-or (bit-shift-left (bit-and b3 255) 24)
           (bit-shift-left (bit-and b2 255) 16)
           (bit-shift-left (bit-and b1 255)  8)
           (bit-shift-left (bit-and b0 255)  0))))

(defn array-fn
  "returns the appropriate array function for the given inputs
 
   (array-fn [5 5 5])
   => short-array
 
   (array-fn [8 8 8 8])
   => int-array"
  {:added "3.0"}
  ([elems]
   (let [total (apply + elems)]
     (cond (<= total 8)  byte-array
           (<= total 16) short-array
           (<= total 32) int-array
           (<= total 64) long-array
           :else (throw (Exception. (str "Elements too long:" elems)))))))

(defn mask
  "returns the number representation of the binary bit mask of `n` digits
 
   (mask 5) => 31
   (mask 7) => 127"
  {:added "3.0"}
  ([n]
   (bit-and -1 (dec (bit-shift-left 1 n)))))

(defn form-params
  "returns the mask and start values given a list of elements
 
   (form-params [5 5 5])
   => '[(31 31 31) [0 5 10]]
 
   (form-params [8 8 8 8])
   => '[(255 255 255 255) [0 8 16 24]]"
  {:added "3.0"}
  ([elems]
   (let [masks  (map mask elems)
         starts (reduce (fn [out e]
                          (conj out (+ e (last out))))
                        [0]
                        (rest elems))]
     [masks starts])))

(defn <<form
  "returns the form for evaluating a set of elements
 
   (<<form [5 5 5])
   => '(clojure.core/fn [[i2 i1 i0]]
         (clojure.core/bit-or (clojure.core/bit-shift-left
                               (clojure.core/bit-and i2 31) 10)
                              (clojure.core/bit-shift-left
                               (clojure.core/bit-and i1 31) 5)
                              (clojure.core/bit-shift-left
                               (clojure.core/bit-and i0 31) 0)))"
  {:added "3.0"}
  ([elems]
   (let [[masks starts] (form-params elems)
         inputs (mapv #(symbol (str "i" %)) (reverse (range (count elems))))]
     `(fn [~inputs]
        (bit-or ~@(map (fn [input mask start]
                         `(bit-shift-left (bit-and ~input ~mask) ~start))
                       inputs
                       (reverse masks)
                       (reverse starts)))))))

(defn <<fn
  "returns the function that will output a value given a vector of inputs
 
   ((<<fn [5 5 5]) [10 20 30])
   => 10910"
  {:added "3.0"}
  ([elems]
   (eval (<<form elems))))

(defn <<
  "returns the low-level representation of data
 
   (-> (<< [[10 20 30]
            [10 20 30]] [5 5 5])
       vec)
   => [10910 10910]"
  {:added "3.0"}
  ([val elems]
   (let [f  (<<fn elems)]
     (cond (vector? (first val))
           (let [af (array-fn elems)]
             (af (map f val)))

           :else (f val)))))

(defn >>form
  "returns the form for turning an integer into representations
 
   (>>form [5 5 5])
   => '(clojure.core/fn [v]
         [(clojure.core/bit-and (clojure.core/bit-shift-right v 10) 31)
          (clojure.core/bit-and (clojure.core/bit-shift-right v 5) 31)
          (clojure.core/bit-and (clojure.core/bit-shift-right v 0) 31)])"
  {:added "3.0"}
  ([elems]
   (let [[masks starts] (form-params elems)]
     `(fn [~'v]
        ~(mapv (fn [mask start]
                 `(bit-and (bit-shift-right ~'v ~start) ~mask))
               (reverse masks)
               (reverse starts))))))

(defn >>fn
  "returns a vector representation given an input value
 
   ((>>fn [5 5 5]) 10910)
   => [10 20 30]"
  {:added "3.0"}
  ([elems]
   (eval (>>form elems))))

(defn >>
  "returns human readable version of the raw bytes
 
   (-> (short-array [10910 10910])
       (>> [5 5 5]))
   => [[10 20 30] [10 20 30]]"
  {:added "3.0"}
  ([val elems]
   (let [f (>>fn elems)]
     (cond (number? val)
           (f val)

           (coll? val) (mapv f val)

           (.isArray ^Class (type val))
           (mapv f val)

           :else (f elems)))))

(defn byte-argb->byte-gray
  "converts a byte-argb array to byte-gray
 
   (-> (byte-array [255 10 10 10 255 20 20 20])
       (byte-argb->byte-gray)
       (vec))
   => [10 20]"
  {:added "3.0"}
  ([bytes]
   (byte-argb->byte-gray bytes (/ (count bytes) 4)))
  ([bytes length]
   (let [grayscale-bytes (byte-array length)]
     (byte-argb->byte-gray bytes grayscale-bytes length)))
  ([^bytes from-bytes ^bytes to-bytes length]
   (dotimes [i length]
     (let [t (* 4 i)
           a (bit-and 255 (aget from-bytes t))
           r (aget from-bytes (+ t 1))
           g (aget from-bytes (+ t 2))
           b (aget from-bytes (+ t 3))]
       (aset-byte to-bytes
                  i
                  (-> (unchecked-add r g)
                      (unchecked-add b)
                      (unchecked-divide-int 3)
                      (unchecked-multiply a)
                      (unchecked-divide-int 255)
                      (unchecked-byte)))))
   to-bytes))

(defn int-argb->byte-gray
  "converts a int-argb array to byte-gray
 
   (->> [[255 10 10 10] [255 20 20 20]]
        (map bytes->int)
        (int-array)
        (int-argb->byte-gray)
        (vec))
   => [10 20]"
  {:added "3.0"}
  ([ints]
   (int-argb->byte-gray ints (count ints)))
  ([ints length]
   (let [grayscale-bytes (byte-array length)]
     (int-argb->byte-gray ints grayscale-bytes length)))
  ([^ints from-ints ^bytes to-bytes length]
   (dotimes [i length]
     (let [[a r g b] (int->bytes (aget from-ints i))]
       (aset-byte to-bytes
                  i
                  (-> (unchecked-add r g)
                      (unchecked-add b)
                      (unchecked-divide-int 3)
                      (unchecked-multiply a)
                      (unchecked-divide-int 255)
                      (unchecked-byte)))))
   to-bytes))

(defn byte-gray->int-argb
  "converts a byte-gray array to int-argb
 
   (->> (byte-array [10 20])
        (byte-gray->int-argb)
        (mapv int->bytes))
   => [[255 10 10 10] [255 20 20 20]]"
  {:added "3.0"}
  ([bytes]
   (byte-gray->int-argb bytes (count bytes)))
  ([bytes length]
   (let [color-ints (int-array length)]
     (byte-gray->int-argb bytes color-ints length)))
  ([^bytes from-bytes ^ints to-ints length]
   (dotimes [i length]
     (let [v (aget from-bytes i)]
       (aset-int to-ints
                 i
                 (unchecked-int
                  (bytes->int [255 v v v])))))
   to-ints))
