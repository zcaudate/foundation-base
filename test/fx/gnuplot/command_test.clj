(ns fx.gnuplot.command-test
  (:use code.test)
  (:require [fx.gnuplot.command :refer :all]))

^{:refer fx.gnuplot.command/command-element :added "3.0"}
(fact "constructs a command string from element"

  (command-element '(0 [0 "0"] 0))
  => "0,0 0,0"

  (command-element #{"hello"})
  => "'hello'")

^{:refer fx.gnuplot.command/command-vector :added "3.0"}
(fact "constructs a command string from vector"

  (command-vector [0 [0 "0"] 0])
  => "0 0 0 0")

^{:refer fx.gnuplot.command/command-submap :added "3.0"}
(fact "constructs a string from a map" ^:hidden

  (command-submap {:circle  {:radius [:graph 0.02]}
                   :fill    [:empty :border]
                   :rectangle {:back []
                               :linewidth 1.2}})
  => "circle radius graph 0.02 fill empty border rectangle back linewidth 1.2")

^{:refer fx.gnuplot.command/command-map :added "3.0"}
(fact "constructs a array of commands using a map" ^:hidden

  (command-map {:dummy  (:x :y)
                :offset '(0 0 0 0)
                :format {:x  #{"% h"}
                         :y  #{"% h"}
                         :x2 #{"% h"}}
                :grid false})
  => ["unset dummy" "set offset 0,0,0,0" "set format x '% h'" "set format y '% h'" "set format x2 '% h'" "unset grid"])

^{:refer fx.gnuplot.command/command-raw :added "3.0"}
(fact "returns a list of commands" ^:hidden

  (command-raw '[{:dummy  (:x :y)
                  :offset (0 0 0 0)
                  :style  {:circle  {:radius [:graph 0.02]}
                           :fill    [:empty :border]
                           :rectangle {:back []
                                       :linewidth 1.2}}
                  :format {:x  #{"% h"}
                           :y  #{"% h"}
                           :x2 #{"% h"}}
                  :grid false}
                 ("a = 5")
                 [:plot "a*sin(x)**2" :title #{"5sin^2(x)"}]])
  => '[["set dummy x,y" "set offset 0,0,0,0"
        "set style circle radius graph 0.02"
        "set style fill empty border"
        "set style rectangle back linewidth 1.2"
        "set format x '% h'"
        "set format y '% h'"
        "set format x2 '% h'"
        "unset grid"]
       ("a = 5")
       "plot a*sin(x)**2 title '5sin^2(x)'"])

^{:refer fx.gnuplot.command/command :added "3.0"}
(fact "returns the entire command" ^:hidden

  (command '[{:dummy  (:x :y)
              :offset (0 0 0 0)
              :style  {:circle  {:radius [:graph 0.02]}
                       :fill    [:empty :border]
                       :rectangle {:back []
                                   :linewidth 1.2}}
              :format {:x  #{"% h"}
                       :y  #{"% h"}
                       :x2 #{"% h"}}
              :grid false}
             ("a = 5")
             [:plot "a*sin(x)**2" :title #{"5sin^2(x)"}]])
  => fx.gnuplot.command.GnuCommand)

(comment

  (./import)

  (def command
    (->> (filter #(or (.startsWith % "set")
                      (.startsWith % "unset"))
                 (str/split-lines (slurp "/Users/chris/plotexp.plt")))
         (map #(rest (str/split % #" ")))
         (group-by first)))

  (keys (map/filter-vals (comp #{1} count) command))
  ("mcbtics" "x2tics" "object" "lmargin" "timefmt" "fit" "psdir" "origin" "dummy" "datafile" "micro" "y2range" "urange" "pointintervalbox" "arrow" "grid" "zzeroaxis" "boxwidth" "isosamples" "xrange" "x2data" "label" "border" "parametric" "encoding" "rgbmax" "errorbars" "y2tics" "zdata" "zero" "loadpath" "zrange" "tmargin" "rmargin" "x2zeroaxis" "locale" "surface" "vrange" "pointsize" "rrange" "jitter" "logscale" "mxtics" "fontpath" "yzeroaxis" "y2zeroaxis" "trange" "polar" "yrange" "contour" "my2tics" "hidden3d" "mapping" "bmargin" "theta" "xdata" "mx2tics" "xyplane" "y2data" "raxis" "cntrlabel" "size" "angles" "xzeroaxis" "decimalsign" "mztics" "nomttics" "mrtics" "offsets" "x2range" "cbrange" "mytics" "samples" "ydata" "minussign")

  (keys (map/filter-vals #(< 1 (count %)) command))
  ("xlabel" "pm3d" "ttics" "ylabel" "clip" "key" "cntrparam" "timestamp" "tics" "style" "colorbox" "rlabel" "title" "cbtics" "ztics" "cblabel" "palette" "y2label" "rtics" "ytics" "view" "zlabel" "x2label" "format" "xtics"))

