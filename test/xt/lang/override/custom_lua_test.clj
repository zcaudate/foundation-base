(ns xt.lang.override.custom-lua-test
  (:use code.test)
  (:require [xt.lang.override.custom-lua :refer :all]
            [std.lang :as l]))

(l/script :lua
  {:runtime :oneshot
   :layout :full
   :require [[xt.lang :as k]
             [xt.lang.base-lib :as lib :include [:fn]]]})

^{:refer xt.lang.override.custom-lua/pad-left :added "4.0"}
(fact "override for pad left"
  ^:hidden
  
  (!.lua
   (k/pad-left "000" 5 "-"))
  => "--000")

^{:refer xt.lang.override.custom-lua/pad-right :added "4.0"}
(fact "override for pad right"
  ^:hidden
  
  (!.lua
   (k/pad-right "000" 5 "-"))
  => "000--")
