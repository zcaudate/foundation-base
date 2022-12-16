(ns play.tui-005-draw.element-clock
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core :as j]
              [js.react :as r :include [:fn]]
              [js.blessed :as b :include [:drawille
                                          :bresenham]]]
   :export   [MODULE]})

(defn.js drawClock
  ([c]
   (. c (clear))
   (let [t   (new Date)
         sin (fn [i l]
               (return (j/floor (+ 80 (* l (j/sin (* i 2 j/PI)))))))
         cos (fn [i l]
               (return (j/floor (+ 80 (*  l (j/cos (* i 2 j/PI)))))))]
     (b/line 80 80
             (sin (/ (j/getHours t)
                     24)
                  30)
             (- 160 (cos (/ (j/getHours t)
                            24)
                         30))
             (. c.set (bind c)))

     (b/line 80 80
             (sin (/ (j/getMinutes t)
                     60)
                  50)
             (- 160 (cos (/ (j/getMinutes t)
                            1)
                         50))
             (. c.set (bind c)))
     
     (b/line 80 80
             (sin (+ (/ (j/getSeconds t)
                        60)
                     (/ (mod t 1000) 60000))
                  75)
             (- 160
                (cos (+ (/ (j/getSeconds t)
                           60)
                        (/ (mod t 1000) 60000))
                     75))
             (. c.set (bind c)))
     (return (c.frame)))))

(defn.js ClockPanel
  ([]
   (let [[view setView] (r/local nil)
         canvas         (r/const (new Drawille 160 160))
         isMounted (r/useIsMounted)]
     (r/useInterval (fn []
                      (when (isMounted)
                        (setView (-/drawClock canvas))))
                    100))
   (return [:box {:bg "black"
                  :width 80
                  :left "center"
                  :content view}])))

(def.js MODULE (!:module))
