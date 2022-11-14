(ns math.infix.core
  (:require [math.infix.ops]))

(def operator-alias
  (atom
   {'&&     'and
    '||     'or
    '!=     'not=
    '<<     'bit-shift-left
    '>>     'bit-shift-right
    '>>>    'unsigned-bit-shift-right
    '!      'not
    '&      'bit-and
    '|      'bit-or
    '.      '*
    'abs    'Math/abs
    'signum 'Math/signum
    '**     'Math/pow
    'sin    'Math/sin
    'cos    'Math/cos
    'tan    'Math/tan
    'asin   'Math/asin
    'acos   'Math/acos
    'atan   'Math/atan
    'sinh   'Math/sinh
    'cosh   'Math/cosh
    'tanh   'Math/tanh
    'sec    'math.infix.ops/sec
    'csc    'math.infix.ops/csc
    'cot    'math.infix.ops/cot
    'asec   'math.infix.ops/asec
    'acsc   'math.infix.ops/acsc
    'acot   'math.infix.ops/acot
    'exp    'Math/exp
    'log    'Math/log
    'e      'Math/E
    'π      'Math/PI
    'φ      'math.infix.ops/φ
    'sqrt   'Math/sqrt
    '√      'Math/sqrt
    '÷      'math.infix.ops/divide
    'root   'math.infix.ops/root
    'gcd    'math.infix.ops/gcd
    'lcm    'math.infix.ops/lcm
    'fact   'math.infix.ops/fact
    'sum    'math.infix.ops/sum
    '∑      'math.infix.ops/sum
    'product 'math.infix.ops/product
    '∏      'math.infix.ops/product}))

(def operator-precedence
  ; From https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages
  ; Lowest precedence first
  [;; binary operators
   'or 'and 'bit-or 'bit-xor 'bit-and 'not= '= '== '>= '> '<= '<
   'unsigned-bit-shift-right 'bit-shift-right 'bit-shift-left
   '+ '- '* '/ 'math.infix.ops/divide 'Math/pow 'mod

   ;; unary operators
   'not
   'Math/sin  'Math/cos  'Math/tan
   'Math/asin 'Math/acos 'Math/atan
   'Math/sinh 'Math/cosh 'Math/tanh
   'Math/sqrt 'Math/exp  'Math/log
   'Math/abs  'Math/signum])

(defn- bounded? [sym]
  (if-let [v (resolve sym)]
    (bound? v)
    false))

(defn resolve-alias
  "Attempt to resolve any aliases: if not found just return the original term"
  {:added "3.0"}
  ([term]
   (if (and (symbol? term) (bounded? term))
     term
     (get @operator-alias term term))))

(defn- empty-arglist? [xs]
  (let [elem (fnext xs)]
    (and (seq? elem) (empty? elem))))

(defn rewrite
  "Recursively rewrites the infix-expr as a prefix expression, according to
    the operator precedence rules"
  {:added "3.0"}
  ([infix-expr]
   (cond
     (not (seq? infix-expr))
     (resolve-alias infix-expr)

     (and (seq? (first infix-expr)) (= (count infix-expr) 1))
     (rewrite (first infix-expr))

     (empty? (rest infix-expr))
     (first infix-expr)

     :else
     (let [infix-expr (map resolve-alias infix-expr)]
       (loop [ops operator-precedence]
         (if-let [op (first ops)]
           (let [idx (.lastIndexOf ^java.util.List infix-expr op)]
             (if (pos? idx)
               (let [[expr1 [op & expr2]] (split-at idx infix-expr)]
                 (list op (rewrite expr1) (rewrite expr2)))
               (recur (next ops))))

           (if (empty-arglist? infix-expr)
             (list (rewrite (first infix-expr)))
             (list (rewrite (first infix-expr)) (rewrite (next infix-expr))))))))))

(def base-env
  (merge
    ; wrapped java.lang.Math constants & functions
   (->>
    ['math.infix.ops]
    (mapcat ns-publics)
    (map (fn [[k v]] (vector (keyword k) v)))
    (into {}))

    ; Basic ops
   {:== ==
    := =
    :!= not=
    :+ +
    :- -
    :* *
    :/ /}))
