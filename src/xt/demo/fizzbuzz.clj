(ns xt.demo.fizzbuzz
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export [MODULE]})

(defn.xt fizzbuzz
  "Generates FizzBuzz for numbers from 1 to n"
  {:added "1.0"}
  [n]
  ;; Initialize an empty array for results
  (var result [])
  ;; Iterate through numbers 1 to n
  (k/for:index [i [1 (+ n 1) 1]]
    (var value nil)
    ;; Check divisibility for FizzBuzz
    (if (and (== 0 (mod i 3)) (== 0 (mod i 5)))
      (:= value "FizzBuzz")
      (if (== 0 (mod i 3))
        (:= value "Fizz")
        (if (== 0 (mod i 5))
          (:= value "Buzz")
          (:= value i))))
    ;; Append the value to the result array
    (x:arr-push result value))
  ;; Return the result array
  (return result))

(def.xt MODULE (!:module))
