(ns fx.gui.data.common
  (:require [fx.gui.data.state :as state]
            [fx.gui :as fx]
            [std.lib :as h]))

(defn get-image-viewer
  "creates the image viewer"
  {:added "3.0"}
  ([name]
   (or (get-in @state/*viewers* [:image name])
       (-> (swap! state/*viewers* assoc-in [:image name]
                  (doto (fx/create-image-viewer name)
                    (fx/add-close-hook
                     (fn [_]
                       (swap! state/*viewers* update :image dissoc name)))))
           (get-in [:image name])))))

(defn get-html-viewer
  "creates the html viewer"
  {:added "3.0"}
  ([name]
   (or (get-in @state/*viewers* [:html name])
       (-> (swap! state/*viewers* assoc-in [:html name]
                  (doto (fx/create-html-viewer name)
                    (fx/add-close-hook
                     (fn [_]
                       (swap! state/*viewers* update :html dissoc name)))))
           (get-in [:html name])))))

(defmethod state/output-fn :image
  ([_]
   (fn [^bytes bytes key [w h]]
     (fx/display-image bytes
                       (get-image-viewer (h/strn key))
                       [w h]))))

(defmethod state/output-fn :html
  ([_]
   (fn [^bytes bytes key [w h]]
     (fx/display-html (String. bytes)
                      (get-html-viewer (h/strn key))
                      [(or w 600)
                       (or h 400)]))))
