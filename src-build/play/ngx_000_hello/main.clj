(ns play.ngx-000-hello.main
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(fact:global
 {:prelim  [(require 'play.ngx-000-hello.build)
            (eval (std.make/run:init play.ngx-000-hello.build/PROJECT))]
  :setup   [(eval (std.make/run:dev play.ngx-000-hello.build/PROJECT))]})

(l/script :lua
  {:require [[xt.lang.base-lib :as k]]})

(def.lua thr ngx.thread)

(def.lua timer ngx.timer)

(defn.lua main []
  (ngx.say "\n---THREAD-TEST-")
  (thr.spawn
   (fn []
     (for:array [i (k/arr-range 10)]
       (ngx.sleep (/ (math.random) 5))
       (ngx.say "Thread 1: " i))))
  (thr.spawn
   (fn []
     (for:array [i (k/arr-range 10)]
       (ngx.sleep (/ (math.random) 5))
       (ngx.say "Thread 2: " i)))))

(defrun.lua __init__
  (do (:# (!:uuid))
      (-/main)))
