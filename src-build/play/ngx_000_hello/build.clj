(ns play.ngx-000-hello.build
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.make :as make :refer [def.make]]
            [play.ngx-000-hello.main]))

(def.make PROJECT
  {:github   {:repo "zcaudate/play.ngx-000-hello"
              :description "Simple Openresty Nxg Threads Example"}
   :orgfile  "Main.org"
   :sections {:setup  [{:type :makefile
                        :main [[:run ["resty main.lua"]]
                               [:dev ["echo main.lua | entr -r resty main.lua"]]]}]}
   :default  [{:type   :module.graph
               :lang   :lua
               :main   'play.ngx-000-hello.main
               :emit   {:code   {:label true}}}]})

(def +init+
  (do (make/triggers-set PROJECT '#{play.ngx-000-hello.main})))

^{:eval false
  ;;
  ;; BUILD SETUP
  ;;
  }
(fact "Code FOR PROJECT SETUP" 

  (make/build-all PROJECT))

^{:eval false
  ;;
  ;; BUILD SETUP
  ;;
  }
(fact "Code FOR PROJECT SETUP" 

  (make/run:dev PROJECT))

^{:eval false
  ;;
  ;; GH INITAL SETUP
  ;;
  :ui/action [:GITHUB :SETUP]}
(fact "initial setup of repo from github"

  (make/gh:dwim-init PROJECT))

^{:eval false
  ;;
  ;; GH PUSH NEWEST
  ;;
  :ui/action [:GITHUB :PUSH]}
(fact "pushes changes to github"

  (make/gh:dwim-push PROJECT))

