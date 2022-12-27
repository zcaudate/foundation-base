(ns lua.torch.optim
  (:require [std.lib :as h]
            [std.lang :as l]
            [std.string :as str])
  (:refer-clojure :exclude []))

(l/script :lua
  {:macro-only true
   :bundle  {:default [["optim" :as optim]]}})

(def +optim+
  ["rmsprop"
   "cmaes"
   "lbfgs"
   "adadelta"
   "FistaLS"
   "polyinterp"
   "adamax"
   "adagrad"
   "Logger"
   "sgd"
   "ConfusionMatrix"
   "checkgrad"
   "lswolfe"
   "rprop"
   "adam"
   "cg"
   "de"
   "nag"
   "asgd"])

(h/template-entries [l/tmpl-entry {:type :fragment
                                   :base "optim"
                                   :tag "lua"
                                   :shrink true}]
  +optim+)