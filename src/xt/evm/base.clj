(ns xt.evm.base
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export [MODULE]})



(def.xt MODULE (!:module))
