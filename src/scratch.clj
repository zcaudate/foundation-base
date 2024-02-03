(ns hello
  (:require [std.lang :as l]
            [std.lib :as h]))


(l/script :js)

(!.js
 (fn []
   (var a 1)
   (var b 2)
   (return
    (+ 1 2 3))))


(defn.js add-10
  [x]
  (return (+ x 10)))



(l/script :python
  {:runtime :basic
   :require [[xt.lang.base-lib :as k]]})

(defn.py add-20
  [y]
  (return (+ y 20)))




(comment
  (add-10 10)
  (add-20 10)

  )
