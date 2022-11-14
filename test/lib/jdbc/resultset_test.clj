(ns lib.jdbc.resultset-test
  (:use code.test)
  (:require [lib.jdbc.resultset :refer :all]))

^{:refer lib.jdbc.resultset/result-set->lazyseq :added "4.0"}
(fact 
  "Function that wraps result in a lazy seq. This function
  is part of public api but can not be used directly (you should pass
  this function as parameter to `query` function).

  Required parameters:
    rs: ResultSet instance.

  Optional named parameters:
    :identifiers -> function that is applied for column name
                    when as-arrays? is false
    :as-rows?    -> by default this function return a lazy seq of
                    records (map), but in certain circumstances you
                    need results as a lazy-seq of vectors. With this keywork
                    parameter you can enable this behavior and return a lazy-seq
                    of vectors instead of records (maps).")

^{:refer lib.jdbc.resultset/result-set->vector :added "4.0"}
(fact 
  "Function that evaluates a result into one clojure persistent
  vector. Accept same parameters as `result-set->lazyseq`.")