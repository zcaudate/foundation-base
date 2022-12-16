(ns play.tui-004-layout-starter.main-panel
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core :as j]
              [js.react :as r]]
   :export   [MODULE]})

(defmacro.js ^{:style/indent 1}
  content-frame
  [label & content]
  `[:box ~(cond-> {:border {:type "none"}
                   :style  {:border {:fg "clear"}}})
    ~@content])

;;
;; Starter Section
;;


(defn.js Starter1Panel
  ([]
   (return (-/content-frame "Starter 1"
             [:box "Starter 1"]))))


(defn.js Starter2Panel
  ([]
   (return (-/content-frame "Starter 2"
             [:box "Starter 2"]))))


(defn.js Starter3Panel
  ([]
   (return (-/content-frame "Starter 3"
               [:box "Starter 3"]))))


(def.js StarterItems
  [{:label "Starter 1"  :name "section-1" :view Starter1Panel}
   {:label "Starter 2"  :name "section-2" :view Starter2Panel}
   {:label "Starter 3"  :name "section-3" :view Starter3Panel}])


;;
;; Alt Section
;;

(defn.js Alt1Panel
  ([]
   (return (-/content-frame "Alt 1"
             [:box "Alt 1"]))))


(defn.js Alt2Panel
  ([]
   (return (-/content-frame "Alt 2"
             [:box "Alt 2"]))))


(defn.js Alt3Panel
  ([]
   (return (-/content-frame "Alt 3"
             [:box "Alt 3"]))))


(def.js AltItems
  [{:label "Alt 1"  :name "section-1" :view Alt1Panel}
   {:label "Alt 2"  :name "section-2" :view Alt2Panel}
   {:label "Alt 3"  :name "section-3" :view Alt3Panel}])

(def.js MODULE (!:module))


