(ns js-blessed.scratch-test.example.art
  (:require [std.lang :as  lang]
            [std.lib :as h]
            [std.string :as str]))

(lang/script :js
  {:import   [["react" :as React]
              ["lodash" :as _]
              ["chalk" :as chalk]
              ["qrcode-terminal" :as qr]]
   :require  [[js.core :as j]
              [statsapp.core.global :as g]
              [statsapp.node.ui :as ui]
              [statsapp.node.form :as form]
              [statsapp.node.layout :as layout]
              [statsapp.node.element.art :as art]
              [statsapp.node.element.cube :as cube]]
   :export   [MODULE]})

(defn.js CastlePanelToobar
  ([]
   (return [:box {:top 2
                  :right 1
                  :style {:bold true
                          :bg "black"}
                  :content (chalk.inverse.magenta " CASTLE ASCII ART ")}])))

(defn.js CastlePanel
  ([]
   (g/usePageToolbar CastlePanelToobar)
   (return
    [:box {:top "center"
           :bg "black"}
     [:% art/Castle]])))

(defn.js DonutPanel
  ([]
   (return
    [:box {:bg "black"                    
           :top "12%"}
     [:% art/Donut]])))

(defn.js QRCodePanel
  ([]
   (let [[view setView] (js/useState "-")
         len  (. view (split #"\n") [0] length)
         tb   (js/useRef)]
     (js/useEffect (fn []
                     (when (== "-" view)
                       (:= (r/curr tb).value "https://www.statstrade.io")
                       (qr.generate "https://www.statstrade.io"
                                    (fn [code]
                                      (setView code))))))
     (return
      (ui/content-frame "QR Code"
        [:textbox {:ref tb
                   :left  "center"
                   :width 80
                   :top 1
                   :height 1
                   :style {:bg "gray"}
                   :keys true
                   :mouse true
                   :align "center"
                   :inputOnFocus true
                   :on-submit (fn []
                                (qr.generate (r/curr tb).value
                                             (fn [code]
                                               (setView code))))}]
        [:box {:top 3
               :width (/ len 5)
               :left "center"
               :content view}])))))

(def.js ArtItems
  [{:label "Castle Demo"   :name "castle"   :view CastlePanel}
   {:label "Clock Demo"    :name "clock"    :view cube/ClockPanel}
   {:label "Cube Demo"     :name "cube"     :view cube/CubePanel}
   {:label "Donut"         :name "donut"    :view DonutPanel}])

(defn.js ArtPanel
  ([]
   (return [:% form/TabViewEntry
            {:record g/ScratchRoute
             :field  "art"
             :color "yellow"
             :items ArtItems}])))

(def.js MODULE
  #{ArtPanel})

(h/make:build-roots)
