(ns lua.core-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :basic
   :require [[lua.core :as u]
             [xt.lang.base-iter :as it]]})

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

^{:refer lua.core/reduce-iter :added "4.0"}
(fact "reduces iterate"
  ^:hidden
  
  (!.lua
   (u/reduce-iter (it/iter [1 2 3 4 5 6])
                  (fn [arr x]
                    (x:arr-push arr x)
                    (return arr))
                  []))
  => [1 2 3 4 5 6])

^{:refer lua.core/slurp :added "4.0"}
(fact "slurps the output from path"
  
  (!.lua
   (-> (u/io-popen "cd")
       (. (read "*l"))))
  
  (!.lua
   (+ 1 2 3))

  (!.lua
   (os.getenv "PWD"))
  
  (!.lua 
   (. (debug.getinfo 1)
      short_src))
  
  (!.lua
   (u/slurp "project.clj")))

^{:refer lua.core/spit :added "4.0"}
(fact "spits the output from path")


(comment
  (l/rt:restart))
