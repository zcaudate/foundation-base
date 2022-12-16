(ns play.tui-002-game-of-life.build
  (:use [code.test :exclude [-main]])
  (:require [std.lang :as  l]
            [std.lib :as h]
            [std.make :as make :refer [def.make]]
            [js.webpack :as webpack]
            [play.tui-002-game-of-life.main :as main]))

(def.make PROJECT
  {:github   {:repo "zcaudate/play.tui-002-game-of-life"
              :description "Conway's Game of Life for React Blessed"}
   :orgfile  "Main.org"
   :sections {:setup  [webpack/+node-basic+
                       webpack/+node-makefile+
                       webpack/+node-gitignore+]}
   :default  [{:type   :module.graph
               :lang   :js
               :target "src"
               :main   'play.tui-002-game-of-life.main
               :emit   {:code   {:label true}}}]})

(def +init+
  (do (make/triggers-set PROJECT '#{play.tui-002-game-of-life.main})))

(defn -main
  []
  (make/build-all PROJECT)
  (make/gh:dwim-init PROJECT))

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

  (make/run:init PROJECT))

^{:eval false
  ;;
  ;; RUN DEV
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

