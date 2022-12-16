(ns play.tui-004-layout-starter.main
  (:require [std.lang :as  lang]
            [std.lib :as h]))

(l/script :js
  {:require  [[js.react :as r]
              [js.core :as j]
              [js.lib.valtio :as v]
              [js.blessed :as b :include [:fn]]
              [js.blessed.layout :as layout]
              [play.tui-004-layout-starter.main-panel :as main-panel]
              [js.lib.chalk :as chalk]]})
              
(defglobal.js Route
  (v/make {:current "starter"}))

(defglobal.js RouteIndex
  (v/make {}))

(defn.js App
  []
  (let [[route setRoute] (v/useProxyField -/Route "current")
        [index setIndex] (v/useProxyField -/RouteIndex route)])
  (return
   [:% layout/LayoutMain
    {:route route 
     :setRoute setRoute
     :index index 
     :setIndex setIndex
     :header {:menu [{:index "f1"
                      :route "starter"
                      :label "Starter"}
                     {:index "f2"
                      :route "alt"
                      :label "Alt"}]}
     :footer {:menu [{:index "f1"
                      :route "starter"
                      :label "Starter"}
                     {:index "f2"
                      :route "alt"
                      :label "Alt"
                      :hidden true}]
              :toggle [{:label "D"
                        :active true}
                       {:label "T"
                        :active true}
                       {:label "G"
                        :active true}
                       {:type "separator"}
                       {:label "C"}]}
     :sections {:starter {:items main-panel/StarterItems
                          :label "Starter"}
                :alt     {:items main-panel/AltItems
                          :label "Alt"}}}]))

(defrun.js __init__
  (do (:# (!:uuid))
      (b/run  [:% -/App] "Js Blessed Starter Demo")))

(comment

  (h/make:roots-get)
  
  (h/make:init)
  (h/make:setup)
  (binding [std.lib.make/*tmux* false]
    (h/make:dev))
  (h/make:shell))

