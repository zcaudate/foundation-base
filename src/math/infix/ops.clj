(ns math.infix.ops)

(defmacro defunary
  "helper for `java.lang.Math` unary functions"
  {:added "3.0"}
  ([func-name & [alias]]
   (let [arg (gensym "x__")]
     `(defn ~(or alias func-name)
        ([~(with-meta arg {:tag 'double})]
         (~(symbol (str "Math/" func-name)) ~arg))))))

(defmacro defbinary
  "helper for `java.lang.Math` binary functions"
  {:added "3.0"}
  ([func-name & [alias]]
   (let [arg1 (gensym "x__")
         arg2 (gensym "y__")]
     `(defn ~(or alias func-name)
        ([~(with-meta arg1 {:tag 'double})
          ~(with-meta arg2 {:tag 'double})]
         (~(symbol (str "Math/" func-name)) ~arg1 ~arg2))))))

(def | bit-or)
(def & bit-and)
(def ¬ bit-not)
(def >> bit-shift-right)
(def >>> unsigned-bit-shift-right)
(def << bit-shift-left)

(defunary abs)
(defunary signum)
(defunary sqrt)
(defunary sqrt √)
(defunary exp)
(defunary log)
(defbinary pow)
(defbinary pow **)

(def product *)
(def sum +)

(defn divide
  "computes the divisor with infinity"
  {:added "3.0"}
  ([a b]
   (if (zero? b)
     (if (neg? a)
       Double/NEGATIVE_INFINITY
       Double/POSITIVE_INFINITY)
     (/ a b))))

(def ÷ divide)

(defn root
  "computes the nth root"
  {:added "3.0"}
  ([a b]
   (pow b (/ 1 a))))

(defn gcd
  "computes the greatest common denominator"
  {:added "3.0"}
  ([a b]
   (if (zero? b)
     a
     (recur b (rem a b)))))

(defn lcm
  "computes the lowest common multiple"
  {:added "3.0"}
  ([a b]
   (/ (* a b) (gcd a b))))

(defn fact
  "computes the factorial"
  {:added "3.0"}
  ([n]
   (if (zero? n)
     1
     (apply * (range 1 (inc n))))))

;; QUANTITIES    

(def φ (/ (inc (√ 5)) 2))
(def e Math/E)
(def π Math/PI)
(def pi Math/PI)


;; TRIGONOMETRY    


(defunary sin)
(defunary cos)
(defunary tan)

(defunary asin)
(defunary acos)
(defunary atan)
(defbinary atan2)

(defunary sinh)
(defunary cosh)
(defunary tanh)

;; Additional trig functions not found in JDK java.lang.Math
(defn sec
  "compute secant, given the angle in radians"
  {:added "3.0"}
  ([θ]
   (÷ 1 (Math/cos θ))))

(defn csc
  "compute cosecant, given the angle in radians"
  {:added "3.0"}
  ([θ]
   (÷ 1 (Math/sin θ))))

(defn cot
  "compute cotangent, given the angle in radians"
  {:added "3.0"}
  ([θ]
   (÷ 1 (Math/tan θ))))

(defn asec
  "compute arcsecant, given the number, returns the arcsecant in radians"
  {:added "3.0"}
  ([value]
   (Math/acos (÷ 1 value))))

(defn acsc
  "compute arccosecant, given the number, returns the arccosecant in radians"
  {:added "3.0"}
  ([value]
   (Math/asin (÷ 1 value))))

(defn acot
  "compute arccotangent, given the number, returns the arccotangent in radians"
  {:added "3.0"}
  ([value]
   (Math/atan (÷ 1 value))))


