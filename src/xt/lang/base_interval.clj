(ns xt.lang.base-interval
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:export [MODULE]})

(defn.xt start-interval
  "sets a delay"
  {:added "4.0"}
  ([thunk ms]
   (return
    (x:start-interval thunk ms))))

(defn.xt stop-interval
  "sets a delay"
  {:added "4.0"}
  ([instance]
   (return
    (x:stop-interval instance))))

(def.xt MODULE (!:module))
