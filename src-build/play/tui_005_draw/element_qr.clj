(ns play.tui-005-draw.element-qr
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core :as j]
              [js.react :as r]]
   :import   [["qrcode-terminal" :as qr]]
   :export   [MODULE]})

(defn.js QRCodePanel
  ([]
   (let [[view setView] (r/local "-")
         len  (. view (split #"\n") [0] length)
         tb   (r/ref)]
     (r/useEffect (fn []
                    (when (== "-" view)
                      (:= (. (r/curr tb) value) "https://www.google.com")
                      (qr.generate "https://www.google.com"
                                   (fn [code]
                                     (setView code))))))
     (return
      [:box
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
                               (qr.generate (. (r/curr tb) value)
                                            (fn [code]
                                              (setView code))))}]
       [:box {:top 3
              :width (/ len 5)
              :left "center"
              :content view}]]))))

(def.js MODULE (!:module))
