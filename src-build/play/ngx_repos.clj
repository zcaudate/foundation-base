(ns play.ngx-repos
  (:require [play.ngx-000-hello.main  :as ngx-000]
            [play.ngx-000-hello.build :as ngx-000-build]
            [play.ngx-001-eval.main    :as ngx-001]
            [play.ngx-001-eval.build   :as ngx-001-build]
            [std.make :as make]))

(defn build-projects
  []
  (mapv make/build-all
        [ngx-000-build/PROJECT
         ngx-001-build/PROJECT]))

(defn push-projects
  []
  (mapv (fn [project]
          (future (make/gh:dwim-init project)))
        [ngx-000-build/PROJECT
         ngx-001-build/PROJECT]))

(comment
  (build-projects)
  (push-projects))
