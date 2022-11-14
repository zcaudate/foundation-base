(ns std.lib.stream.xform-test
  (:use code.test)
  (:require [std.lib.stream.xform :refer :all]))

^{:refer std.lib.stream.xform/x:map :added "3.0"}
(fact "map transducer" ^:hidden

  (sequence (x:map)
            (range 5))
  => '(0 1 2 3 4)

  (sequence (x:map inc)
            (range 5))
  => '(1 2 3 4 5))

^{:refer std.lib.stream.xform/x:map-indexed :added "3.0"}
(fact "map-indexed transducer" ^:hidden

  (sequence (x:map-indexed vector)
            (range 5))
  => '([0 0] [1 1] [2 2] [3 3] [4 4]))

^{:refer std.lib.stream.xform/x:filter :added "3.0"}
(fact "filter transducer" ^:hidden

  (sequence (x:filter)
            [1 nil 2 3])
  => '(1 2 3)

  (sequence (x:filter odd?)
            (range 5))
  => '(1 3))

^{:refer std.lib.stream.xform/x:remove :added "3.0"}
(fact "remove transducer" ^:hidden

  (sequence (x:remove)
            [1 nil 2 3])
  => '(1 2 3)

  (sequence (x:remove identity)
            [1 nil 2 3])
  => '(nil))

^{:refer std.lib.stream.xform/x:keep :added "3.0"}
(fact "keep transducer" ^:hidden

  (sequence (x:keep)
            (range 5))
  => '(0 1 2 3 4)

  (sequence (x:keep #(if (odd? %) (* 2 %)))
            (range 5))
  => '(2 6))

^{:refer std.lib.stream.xform/x:keep-indexed :added "3.0"}
(fact "keep-indexed transducer" ^:hidden

  (sequence (x:keep-indexed vector)
            (range 5))
  => '([0 0] [1 1] [2 2] [3 3] [4 4]))

^{:refer std.lib.stream.xform/x:prn :added "3.0"}
(fact "prn transducer" ^:hidden

  (with-out-str
    (into [] (x:prn) (range 5)))
  => "0\n1\n2\n3\n4\n")

^{:refer std.lib.stream.xform/x:peek :added "3.0"}
(fact "peek transducer" ^:hidden

  (with-out-str
    (into [] (x:peek print) (range 5)))
  => "01234")

^{:refer std.lib.stream.xform/x:delay :added "3.0"}
(fact "delay transducer" ^:hidden

  (sequence (x:delay 5)
            (range 5))
  => '(0 1 2 3 4)

  (sequence (x:delay #(rand-int 5)) ;; takes a function
            (range 5))
  => '(0 1 2 3 4))

^{:refer std.lib.stream.xform/x:mapcat :added "3.0"}
(fact "mapcat transducer" ^:hidden

  (sequence (x:mapcat)
            [[1 2] [3 4] [5 6]])
  => '(1 2 3 4 5 6))

^{:refer std.lib.stream.xform/x:pass :added "3.0"}
(fact "identity transducer" ^:hidden

  (sequence (x:pass)
            (range 5))
  => '(0 1 2 3 4))

^{:refer std.lib.stream.xform/x:apply :added "3.0"}
(fact "applies a reduction function"

  @(reduce (x:apply (fn
                      ([])
                      ([_])
                      ([_ x] (reduced x))))
           (range 5))
  => 1)

^{:refer std.lib.stream.xform/x:reduce :added "3.0"}
(fact "transducer for accumulating results" ^:hidden

  (sequence (x:reduce +)
            (range 5))
  => '(10))

^{:refer std.lib.stream.xform/x:take :added "3.0"}
(fact "take transducer" ^:hidden

  (sequence (x:take)
            (range 5))
  => '(0 1 2 3 4)

  (sequence (x:take 3)
            (range 5))
  => '(0 1 2))

^{:refer std.lib.stream.xform/x:take-last :added "3.0"}
(fact "take-last transducer" ^:hidden

  (sequence (x:take-last 3)
            (range 5))
  => '(2 3 4))

^{:refer std.lib.stream.xform/x:drop :added "3.0"}
(fact "drop transducer" ^:hidden

  (sequence (x:drop)
            (range 5))
  => '()

  (sequence (x:drop 3)
            (range 5))
  => '(3 4))

^{:refer std.lib.stream.xform/x:drop-last :added "3.0"}
(fact "drop-last transducer" ^:hidden

  (sequence (x:drop-last 3)
            (range 5))
  => '(0 1))

^{:refer std.lib.stream.xform/x:butlast :added "3.0"}
(fact "butlast transducer" ^:hidden

  (sequence (x:butlast)
            (range 5))
  => '(0 1 2 3))

^{:refer std.lib.stream.xform/x:some :added "3.0"}
(fact "some transducer" ^:hidden

  (sequence (x:some)
            [nil nil 1 nil 2])
  => '(1))

^{:refer std.lib.stream.xform/x:last :added "3.0"}
(fact "last transducer" ^:hidden

  (sequence (x:last)
            (range 5))
  => '(4))

^{:refer std.lib.stream.xform/x:count :added "3.0"}
(fact "count transducer" ^:hidden

  (sequence (x:count)
            (range 5))
  => '(5))

^{:refer std.lib.stream.xform/x:min :added "3.0"}
(fact "min transducer" ^:hidden

  (sequence (x:min)
            [1 3 5])
  => '(1))

^{:refer std.lib.stream.xform/x:max :added "3.0"}
(fact "max transducer" ^:hidden

  (sequence (x:max)
            [1 3 5])
  => '(5))

^{:refer std.lib.stream.xform/x:mean :added "3.0"}
(fact "mean transducer" ^:hidden

  (sequence (x:mean)
            [1 3 5])
  => '(3.0))

^{:refer std.lib.stream.xform/x:stdev :added "3.0"}
(fact "stdev transducer"

  (sequence (x:stdev)
            (range 5))
  => '(1.5811388300841898))

^{:refer std.lib.stream.xform/x:str :added "3.0"}
(fact "str transducer" ^:hidden

  (sequence (x:str)
            (range 5))
  => '("01234"))

^{:refer std.lib.stream.xform/x:sort :added "3.0"}
(fact "sort transducer" ^:hidden

  (sequence (x:sort >)
            (range 5))
  => '(4 3 2 1 0))

^{:refer std.lib.stream.xform/x:sort-by :added "3.0"}
(fact "sort-by transducer" ^:hidden

  (sequence (x:sort-by odd?)
            (range 5))
  => '(0 2 4 1 3))

^{:refer std.lib.stream.xform/x:reductions :added "3.0"}
(fact "reductions transducer" ^:hidden

  (sequence (x:reductions +)
            (range 5))
  => '(0 0 1 3 6 10))

^{:refer std.lib.stream.xform/x:wrap :added "3.0"}
(fact "wrap transducer" ^:hidden

  (sequence (x:wrap :start :end)
            (range 5))
  => '(:start 0 1 2 3 4 :end))

^{:refer std.lib.stream.xform/x:time :added "3.0"}
(fact "timing transducer" ^:hidden

  (with-out-str (sequence (x:time (map inc))
                          (range 5)))
  => string?)

^{:refer std.lib.stream.xform/x:window :added "3.0"}
(fact "returns a window of elements" ^:hidden

  (sequence (x:window 3)
            (range 5))
  => '([0] [0 1] [0 1 2] [1 2 3] [2 3 4]))
