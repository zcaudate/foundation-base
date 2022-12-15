(ns fx.gnuplot-sample-test
  (:require [fx.gnuplot :as gp]))

(comment

  ;; http://gnufx.sourceforge.net/demo_canvas_5.2/hidden2.html
  (gp/plot [{:input "f(x,y)" :with :pm3d}
            {:input "x*x-y*y" :with :lines :line-color [:rgb #{"black"}]}]
           {:command :splot
            :setup ["f(x,y) = sin(-sqrt((x+5)**2+(y-7)**2)*0.5)"]
            :isosamples '(25 25)
            :xyplane [:at 0]
            :key false
            :palette [:rgbformulae '(31 -11 32)]
            :style [:fill :solid 0.5]
            :cbrange "[-1:1]"
            :title "Mixing pm3d surfaces with hidden-line plots"
            :terminal [:qt]}))

(comment

  ;; http://gnufx.sourceforge.net/demo_canvas_5.2/controls.html
  (gp/plot {:input "s=.1,c(t),s=.3,c(t),s=.5,c(t),s=.7,c(t),s=.9,c(t),s=1.0,c(t),s=1.5,c(t),s=2.0,c(t)"}
           {:setup ["damp(t) = exp(-s*wn*t)/sqrt(1.0-s*s)"
                    "c(t) = 1-damp(t)*per(t)"
                    "per(t) = sin(wn*sqrt(1.0-s**2)*t - atan(-sqrt(1.0-s**2)/s))"
                    "wn = 1.0"]
            :xrange "[0:13]"
            :samples 50
            :dummy :t
            :key :box
            :terminal [:qt]}))

(comment
  (require '[fx.gnuplot :as gp]
           '[std.lib :as h]
           '[hara.shell :as sh])

  (def -out-
    (:output (gp/plot {:input ["[0:20]" "4*sin(1*x)+2"]}
                      {:terminal [:svg :size '(400 300)]
                       :display "HELLO"})))
  (gp/restart-gnuplot)
  (gp/kill-gnuplot)

  (get-html-viewer "hello")

  (h/string -out-)
  (def -rt- (sh/sh "pandoc -f html -t markdown" {:pipe true
                                                 :out {:return :all}}))
  (sh/in -rt- (h/string -out-))
  (sh/close -rt-)
  (h/string (sh/result -rt-))
  -rt-

  (gp/plot {:input ["[0:20]" "4*sin(1*x)+2"]}
           {:terminal :svg;;[:svg :size '(400 300)]
            :display "Hello"})

  (gp/plot {:input "6*sin(3*x)"}
           {:terminal [:svg :size '(400 300)]
            :display "Hello"})

  (gp/plot {:input ["[0:2*pi]" "sin(t), cos(t)"]}
           {:parametric true
            :size :square
            :samples 17
            :xrange "[-1:1]"
            :yrange "[-1:1]"
            :terminal [:svg :size '(400 400)]
            :display "Hello"})

  (gp/plot {:input ["[0:10*pi]" "t*sin(t), t*cos(t)"]}
           {:parametric true
            :size :square
            :xrange "[-10*pi:10*pi]"
            :yrange "[-10*pi:10*pi]"
            :terminal [:svg :size '(400 400)]
            :display "Hello"})

  (do (gp/cmd [[:reset :session]])
      (gp/plot {:input ["[0:2*pi]" "r(t)*sin(t), r(t)*cos(t)"]}
               {:setup [["r(t) = 1 + cos(t)"]]
                :parametric true
                :autoscale true
                :size :square
                :terminal [:svg :size '(400 400)]
                :display "Hello"}))

  (gp/restart-gnuplot)
  (gp/cmd [[:reset :session]])
  (gp/plot [{:input "0.02*(x**3)*(y**3)+ 10000" :title "Cubic"}
            {:input "(x**2)*(y**2)" :title "Quadratic"}]
           {:command :splot
            :ticslevel 0
            :terminal [:qt :size '(500 500)]
            :display "Hello"})

  (gp/plot {:input "2*sin(2*x)"}
           {:terminal [:svg :size '(300 300)]
            :save "output.svg"})

  (plot {:input "2*sin(2*x)"}
        {:terminal [:latex :size '(300 300)]
         :save "output.latex"})

  (plot {:input "2*sin(2*x)"}
        {:terminal [:postscript :size '(300 300)]
         :save "output.ps"})

  (plot {:input "2*sin(3*x)"}
        {:terminal [:canvas :size '(300 300)]
         :display "Hello"})

  (plot {:input "2*sin(3*x)"}
        {:terminal [:gif :size '(300 300)]
         :save "output.gif"})

  (plot {:input "2*sin(3*x)"}
        {:terminal [:png :size '(300 300)]
         :save "output.png"})

  (plot {:input "2*sin(3*x)"}
        {:terminal [:jpeg :size '(300 300)]
         :save "output.jpeg"})

  (plot {:input "2*sin(2*x)"}
        {:terminal [:png :size '(300 300)]
         :display "Hello"})

  (restart-gnuplot))