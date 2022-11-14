(ns std.math
  (:require [std.math.common :as common]
            [std.math.random :as random]
            [std.math.aggregate :as aggregate]
            [std.lib :as h])
  (:refer-clojure :exclude [abs rand rand-int rand-nth]))

(h/intern-in common/abs
             common/ceil
             common/factorial
             common/floor
             common/combinatorial
             common/log
             common/loge
             common/log10
             common/mean
             common/median
             common/mode
             common/variance
             common/stdev
             common/skew
             common/kurtosis
             common/histogram

             aggregate/aggregates

             random/rand-seed!
             random/rand
             random/rand-int
             random/rand-nth
             random/rand-digits
             random/rand-sample)
