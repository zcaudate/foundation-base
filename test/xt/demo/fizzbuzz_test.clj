(ns xt.demo.fizzbuzz-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :js
  {:runtime :basic
   :require [[xt.demo.fizzbuzz :as fb]]})

^{:refer xt.demo.fizzbuzz/fizzbuzz :added "4.0"}
(fact "look at fizzbuzz"
  ^:hidden
  
  (l/with:print
    (fb/fizzbuzz 20))
  => [1 2 "Fizz" 4 "Buzz" "Fizz" 7 8 "Fizz" "Buzz" 11 "Fizz" 13 14 "FizzBuzz" 16 17 "Fizz" 19 "Buzz"])

