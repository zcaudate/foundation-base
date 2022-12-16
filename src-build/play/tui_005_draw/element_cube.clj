(ns play.tui-005-draw.element-cube
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :js
  {:require  [[js.core :as j]
              [js.lib.valtio :as v]
              [js.react :as r :include [:fn]]
              [js.blessed :as b :include [:drawille
                                          :bresenham]]]
   :import   [["gl-matrix" :as glMatrix :refer [mat4 vec3]]]
   :export   [MODULE]})

;;
;; Cube
;; 

(def.js points
  [[-1 -1 -1]
   [-1 -1  1]
   [ 1 -1  1]
   [ 1 -1 -1]
   [-1  1 -1]
   [-1  1  1]
   [ 1  1  1]
   [ 1  1 -1]])

(def.js quads
  [[0 1 2 3] 
   [0 4 5 1] 
   [1 5 6 2] 
   [2 6 7 3] 
   [3 7 4 0] 
   [4 7 6 5]])

(def.js cube
  (quads.map
   (fn [quad]
     (return (quad.map (fn [v]
                         (return (vec3.fromValues.apply nil (. points [v])))))))))

(defn.js drawTransform
  ([out a m]
   (let [x (. a [0])
         y (. a [1])
         z (. a [2])
         w (+ (* (. m [3]) x)
              (* (. m [7]) y)
              (* (. m [11]) z)
              (. m [15]))
         _ (:= w (or w 1.0))]
     (do (:= (. out [0])
             (/ (+ (* (. m [0]) x)
                   (* (. m [4]) y)
                   (* (. m [8]) z)
                   (. m [12]))
                w))
         (:= (. out [1])
             (/ (+ (* (. m [1]) x)
                   (* (. m [5]) y)
                   (* (. m [9]) z)
                   (. m [13]))
                w))
         (:= (. out [2])
             (/ (+ (* (. m [2]) x)
                   (* (. m [6]) y)
                   (* (. m [10]) z)
                   (. m [14]))
                w)))
     (return out))))

(def.js projection
  ((fn []
     (let [proj (mat4.create)]
       (mat4.perspective proj (/ j/PI 3) 1 1 1000)
       (return proj)))))

(defn.js drawCube
  ([c p]
   (let [_ (c.clear)
         t (Date.now)
         view (mat4.create)
         _ (mat4.lookAt view
                        (vec3.fromValues (. p ["a00"])
                                         (. p ["a01"])
                                         (. p ["a02"]))
                        (vec3.fromValues (. p ["a10"])
                                         (. p ["a11"])
                                         (. p ["a12"]))
                        (vec3.fromValues (. p ["a20"])
                                         (. p ["a21"])
                                         (. p ["a22"])))
         
         _ (mat4.rotateX view view (/ (* j/PI 2 t)
                                      9000))
         transformed (cube.map (fn [quad]
                                 (return
                                  (quad.map
                                   (fn [v]
                                     (let [m   (mat4.create)
                                           out (vec3.create)
                                           _ (mat4.mul m projection view)
                                           _ (drawTransform out v m)]
                                       (return {:x (j/floor (+ (* (. out [0])
                                                                     40)
                                                                  80))
                                                :y (j/floor (+ (* (. out [1])
                                                                     40)
                                                                  80))})))))))]
     (transformed.forEach (fn [quad]
                            (quad.forEach
                             (fn [v i]
                               (let [n (. quad [(mod (+ i 1) 4)])]
                                 (b/line v.x v.y n.x n.y (c.set.bind c)))))))
     (return (c.frame)))))

(defglobal.js CubeParams
  (v/make
   {:a00 -2
    :a01 1.3
    :a02 1.2
    :a10 0
    :a11 0
    :a12 0.1
    :a20 0.1
    :a21 0.1
    :a22 0.5
    :paused false}))

(defn.js CubePanel
  ([]
   (let [[view setView] (r/local nil)
         canvas         (r/const (new Drawille 160 160))
         [params] (v/use -/CubeParams)
         #{paused} params
         isMounted (r/useIsMounted)]
     (r/useStep (fn [setDone]
                   (setView (-/drawCube canvas params))
                   (setDone true)))
     (r/useInterval  (fn []
                       (if (and (not paused) (isMounted))
                         (setView (-/drawCube canvas params))))
                     100)
     (return [:box {:bg "black"
                    :top 0
                    :content view}]))))

(def.js MODULE (!:module))

