(ns fx.gnuplot-test
  (:use code.test)
  (:require [fx.gnuplot :refer :all]))

^{:refer fx.gnuplot/generate-script :added "4.0"}
(fact "creates the gnuplot script")

^{:refer fx.gnuplot/gnuplot:create :added "3.0"}
(fact "creates a gnuplot"

  (gnuplot:create {}))

^{:refer fx.gnuplot/get-gnuplot :added "3.0"}
(comment "gets existing or creates a new gnuplot"

  (get-gnuplot))

^{:refer fx.gnuplot/prepare-input :added "3.0"}
(fact "prepares command input")

^{:refer fx.gnuplot/cmd :added "3.0"}
(fact "enter commands to gnuplot")

^{:refer fx.gnuplot/plot-prepare :added "3.0"}
(fact "prepares the plot data")

^{:refer fx.gnuplot/input-points :added "3.0"}
(fact "formats the input points")

^{:refer fx.gnuplot/plot-inputs :added "3.0"}
(fact "plots points form inputs")

^{:refer fx.gnuplot/plot-process :added "3.0"}
(fact "processes the plot input")

^{:refer fx.gnuplot/plot :added "3.0"}
(comment "plots data in gnuplot"
  
  (plot [{:title "Lag" :using "1:2:3" :with :yerrorbars :lt [:palette :cb -45]
          :data (mapv vector (range 50) (range 50) (take 50 (repeatedly #(* 5 (rand)))))}
         {:title "Time" :with :points :lt [:rgb #{"#FF00FF"}]
          :data (vec (take 50 (repeatedly #(rand-int 50))))}]
        {:autoscale true}) ^:hidden
  => "set autoscale\nplot '-'  using 1:2:3 title 'Lag' with yerrorbars lt palette cb -45,'-'  using 1:2 title 'Time' with points lt rgb '#FF00FF'")

(comment
  (./import)

  (plot [{:title "STUFF" :lt 5
          :data (take 100 (repeatedly #(rand-int 50)))}
         {:title "Lag" :lt [:rgb #{"#FF00FF"}]
          :data (vec (map vector (range 100) (repeatedly #(rand-int 50))))}]
        {:autoscale true
         :display "example"})

  (restart-gnuplot)
  (sh/stop)
  (sh/list)
  (restart-gnuplot)
  (sh/in (str (command/command '[[:plot ({:input ["[-2:2]" "2*sin(x)"] :title #{"2sin(x)"}}
                                         {:input "sin(x)"   :title #{"sin(x)"}})]])))

  (sh/in (str (command/command '[[:plot ({:title #{"2sin(x)"}} {:title #{"sin(x)"}})]])))

  (command/command [[:plot [#{-} {:with :lines}]]]))
