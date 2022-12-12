(ns scratch
  (:require [std.lib :as h]
            [std.lang :as l]))

(l/script :js
  {:runtime :basic})

(l/script :python
  {:runtime :basic})


(defn.py hello
  []
  (return
   (+ 1 2 3 4 5)))

(defn.py hello1
  []
  (return
   (+ (-/hello)
      (-/hello))))

(defn.py hello2
  []
  (return
   (+ (-/hello1)
      (-/hello1))))

(comment
  
  (!.js
   (var x (+  1 2 3 4))
   (+ x 1))

  (!.js
   (-/hello2))

  (!.py
   (-/hello2)))
