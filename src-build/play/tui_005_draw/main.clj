^{:no-test true}
(ns play.tui-005-draw.main
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :js
  {:require  [[js.react :as r]
              [js.core :as j]
              [js.blessed :as b :include [:fn]]
              [js.blessed.layout :as layout]
              [js.lib.chalk :as chalk]
              [play.tui-005-draw.element-castle :as castle]
              [play.tui-005-draw.element-clock :as clock]
              [play.tui-005-draw.element-cube :as cube]
              [play.tui-005-draw.element-donut :as donut]
              [play.tui-005-draw.element-qr :as qr]]})


(def.js ScratchItems
  [{:label "Castle"  :name "castle" :view castle/Castle}
   {:label "Clock"   :name "clock"  :view clock/ClockPanel}
   {:label "Cube"    :name "cube"   :view cube/CubePanel}
   {:label "Donut"   :name "donut"  :view donut/Donut}
   {:label "QR"      :name "qr"     :view qr/QRCodePanel}])

(defn.js App
  []
  (return
   [:% layout/LayoutMain
    {:init "starter"
     :header {:menu [{:index "f1"
                      :route "scratch"
                      :label "Scratch"}]}
     :sections {:starter {:label "Scratch"
                          :items -/ScratchItems}}}]))

(defrun.js __init__
  (do (:# (!:uuid))
      (b/run  [:% -/App] "Js Blessed Scratch Demo")))



