(ns fx.lw-charts-test
  (:use code.test)
  (:require [fx.lw-charts :refer :all]))

^{:refer fx.lw-charts/lw-charts:download :added "4.0"}
(fact "downloads the js script")
