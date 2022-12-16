(ns play.ngx-001-eval.build
  (:use [code.test :exclude [-main]])
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.make :as make :refer [def.make]]
            [std.string :as str]
            [play.ngx-001-eval.main :as main]
            [rt.nginx :as nginx]))

(def.make PROJECT
  {:github   {:repo "zcaudate/play.ngx-001-eval"
              :description "Example Openresty Evaluation Server"}
   :orgfile  "Main.org"
   :sections
   {:setup  [{:type :gitignore
              :main ["*_temp" "runtime"]}
             {:type :makefile
              :main [[:dev     ["ls nginx.conf | entr -r make restart"]]
                     [:start   ["nginx" "-p" "runtime" "-c" "../nginx.conf"]]
                     [:stop    ["nginx" "-s" "stop"]]
                     [:refresh ["nginx" "-s" "reload"]]
                     [:restart
                      ["@nginx" "-s" "stop"]
                      ["nginx" "-p" "runtime" "-c" "../nginx.conf"]
                      ["@alerter" "-message" "'NGINX RESTARTED'" "-title" "'STATSNET'" "-timeout" "2" ">" "/dev/null"]]]}
             {:type :blank
              :file "runtime/logs/error.log"}]}
   :default  [{:type :nginx.conf
               :main #'main/+nginx-conf+}]})

(def +init+
  (do (make/triggers-set PROJECT '#{play.ngx-001-eval.main})))

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

  (make/run:start PROJECT))

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


(comment
  (nginx/kill-all-nginx)
  (nginx/all-nginx-ports)
  )
