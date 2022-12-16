(ns play.tui-005-draw.element-donut
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core :as j]
              [js.react :as r]]
   :export   [MODULE]})

(defn.js donut-frame
  ([A B]
   (let [b []
         z []
         cA (j/cos A)
         sA (j/sin A)
         cB (j/cos B)
         sB (j/sin B)
         k  0
         j  0
         i  0]
     (for [(:= k 0) (< k 1760) (:++ k)]
       (:= (. b [k]) (:? (== (mod k 80) 79) ["\n" " "]))
       (:= (. z [k]) 0))
     (for [(:= j 0) (< j 6.28) (:+= j 0.07)]
       (let [ct (j/cos j)
             st (j/sin j)]
         (for [(:= i 0) (< i 6.28) (:+= i 0.02)]
           (let [cp (j/cos i)
                 sp (j/sin i)
                 h  (+ ct 2)           ;; R1 + R2*cos(theta)
                 D (/ 1 (+ (* sp h sA) ;; 1/z
                           (* st cA)
                           5))
                 t (- (* sp h cA)
                      (* st sA))

                 x (b:| 0 (+ 40 (* 30 D
                                   (- (* cp h cB)
                                      (* t sB)))))
                 y (b:| 0 (+ 12 (* 15 D
                                   (+ (* cp h sB)
                                      (* t cB)))))
                 o (+ x (* 80 y))
                 N (b:| 0 (* 8
                             (- (* cB (- (* st sA)
                                         (* sp ct cA)))
                                (* sp ct sA)
                                (* st cA)
                                (* cp ct sB))))]
             (if (and (< y 22)
                      (>= y 0)
                      (>= x 0)
                      (< x 79)
                      (> D (. z [o])))
               (do (:= (. z [o]) D)
                   (:= (. b [o])
                       (. ".,-~:;=!*#$@"
                          [(:? (> N 0)
                               [N 0])]))))))))
     (return (b.join "")))))

(defn.js Donut
  ([]
   (let [[P setP] (r/local [1 1])]
     (r/run []
       (let [id (j/delayed [100]
                  (setP [(+ (. P [0]) 0.07)
                         (+ (. P [1]) 0.03)]))]
         (return (fn []
                   (j/clearTimeout id)))))
     (return
      [:box {:bg "black"
             :width 80
             :content (-/donut-frame (. P [0])
                                     (. P [1]))}]))))

(def.js MODULE (!:module))


