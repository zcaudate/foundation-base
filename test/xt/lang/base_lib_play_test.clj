(ns xt.lang.base-lib-play-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :js
  {:runtime :basic})

(defn.js add-ten
  [x]
  (return (+ x 10)))

(add-ten 10)
20
(add-ten (add-ten 10))
30

(defn.js add-twenty
  [x]
  (return
   (-/add-ten (-/add-ten x 10)
              10)))

(add-twenty 10)
30

;;
;; PYTHON
;;


(l/script- :python
  {:runtime :basic})

(defn.py add-ten
  [x]
  (return (+ x 10)))

(defn.py add-twenty
  [x]
  (return
   (-/add-ten (-/add-ten x 10)
              10)))
