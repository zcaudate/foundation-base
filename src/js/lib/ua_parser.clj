(ns js.lib.ua-parser
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :js
  {:macro-only true
   :bundle {:default [["ua-parser-js" :as UAParser]]}
   :import [["ua-parser-js" :as UAParser]]})


(defmacro.js parseString
  []
  '(. (new UAParser)
      (getResult)))
