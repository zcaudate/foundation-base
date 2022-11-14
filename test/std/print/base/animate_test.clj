(ns std.print.base.animate-test
  (:use code.test)
  (:require [std.print.base.animate :refer :all]))

^{:refer std.print.base.animate/print-animation :added "3.0"}
(comment "outputs an animated ascii file"

  (print-animation "test-data/std.print/plane.ascii"))
