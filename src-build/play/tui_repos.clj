(ns play.tui-repos
  (:require [play.tui-000-counter.main  :as tui-000]
            [play.tui-000-counter.build :as tui-000-build]
            [play.tui-001-fetch.main    :as tui-001]
            [play.tui-001-fetch.build   :as tui-001-build]
            [play.tui-002-game-of-life.main    :as tui-002]
            [play.tui-002-game-of-life.build   :as tui-002-build]
            [play.tui-003-sensor.main    :as tui-003]
            [play.tui-003-sensor.build   :as tui-003-build]
            [play.tui-004-layout-starter.main    :as tui-004]
            [play.tui-004-layout-starter.build   :as tui-004-build]
            [play.tui-005-draw.main    :as tui-005]
            [play.tui-005-draw.build   :as tui-005-build]
            [std.make :as make]))

(defn build-projects
  []
  (mapv make/build-all
        [tui-000-build/PROJECT
         tui-001-build/PROJECT
         tui-002-build/PROJECT
         tui-003-build/PROJECT
         tui-004-build/PROJECT
         tui-005-build/PROJECT]))

(defn push-projects
  []
  (mapv (fn [project]
          (future (make/gh:dwim-init project)))
        [tui-000-build/PROJECT
         tui-001-build/PROJECT
         tui-002-build/PROJECT
         tui-003-build/PROJECT
         tui-004-build/PROJECT
         tui-005-build/PROJECT]))

(comment
  (build-projects)
  (push-projects))
