(ns lua.core-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :oneshot
   :require [[lua.core :as u]]})

^{:refer lua.core/render :added "4.0"}
(fact "renders a template"
  ^:hidden
  
  (!.lua
   (local lustache (require "lustache"))

   (u/render "123 {{hello}} {{world}}"
             {:hello 456
              :world 789}))
  => "123 456 789")

^{:refer lua.core/ffi:C :added "4.0"}
(fact "allow ffi calls"
  ^:hidden
  
  (!.lua
   (local ffi (require "ffi"))
   
   (u/ffi-cdef
    (\\ "[["
     ^{:indent 2}
     (\\
      \\ (@.c
          (do (fn ^{:- [:float]
                    :header true}
                powf [:float x :float y])
              (fn ^{:- [:double]
                    :header true}
                exp [:double x]))))
     \\ "]]"))
   
   (local math (u/ffi-load "m"))
   (local [n m] [2.5 3.5])
   [(math.powf  n m)
    (math.exp  m)])
  => [24.705293655396
      33.115451958692]

  (!.lua
   (local ffi (require "ffi"))
   (u/ffi-cdef
    (%.c (fn ^{:- [:float]
               :header true}
           powf [:float x :float y])
         (fn ^{:- [:double]
               :header true}
           exp [:double x])))
   
   (local math (u/ffi-load "m"))
   (local [n m] [2.5 3.5])
   [(math.powf  n m)
    (math.exp  m)])
  => [24.705293655396
      33.115451958692])

(comment
  (l/with:input []
    (!.lua
     (@.c
      (do (fn ^{:- [:float]
                :header true}
            powf [:float x :float y])
          (fn ^{:- [:double]
                :header true}
            exp [:double x]))))))
