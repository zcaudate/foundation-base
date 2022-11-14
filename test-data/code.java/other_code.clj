(ns other-code)

(defn add [x y & more]
  (apply + x y more))