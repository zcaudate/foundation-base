(ns fx.gui
  (:require [std.image :as image]
            [fx.gui.platform :as platform]
            [fx.gui.display :as display]
            [std.lib :as h])
  (:import (javafx.fxml FXMLLoader)
           (javafx.scene Scene
                         Group)
           (javafx.stage Stage
                         WindowEvent)
           (javafx.application Platform)
           (javafx.scene.web WebView)
           (javafx.scene.image ImageView
                               Image)
           (javafx.embed.swing SwingFXUtils)
           (java.awt.image BufferedImage)))

(h/intern-in platform/callback
             platform/run-fx
             platform/call-fx
             platform/return-fx
             platform/wrap-fx
             platform/do-fx
             platform/event-handler
             platform/event-handler-object
             platform/change-listener
             platform/task

             display/fx-scene
             display/fx-prepare
             display/fx-display)

(defonce +init+
  (if (h/dev?) (platform/init!)))

(defn load-control
  "loads a control given class and fxml file"
  {:added "3.0"}
  ([url controller]
   (load-control url controller nil))
  ([^java.net.URL url controller {:keys [init] :or {init true}}]
   (let [controller (cond (class? controller)
                          (let [cstr (.getConstructor ^Class controller (make-array Class 0))]
                            (.newInstance cstr (make-array Object 0)))
                          :else controller)
         loader     (doto (FXMLLoader. url)
                      (.setController controller))
         root       (.load loader)
         vars       (reduce (fn [out [k v]]
                              (assoc out (keyword k) v))
                            {}
                            (.getNamespace loader))]
     [root controller loader])))

(defn display
  "displays a control"
  {:added "3.0"}
  ([root]
   (display root identity))
  ([root func]
   (Platform/runLater
    (fn []
      (try
        (doto (Stage.)
          (.setScene (Scene. root))
          func
          (.show)
          (.toFront))
        (catch Exception e
          (.printStackTrace e)))))))

(defn add-close-hook
  "adds close hook to window"
  {:added "3.0"}
  ([^Stage stage f]
   (platform/do-fx
    (doto (.getWindow (.getScene stage))
      (.addEventFilter WindowEvent/WINDOW_CLOSE_REQUEST
                       (platform/event-handler f))))))

(defn create-image
  "creates an image from bytes"
  {:added "3.0"}
  ([img]
   (cond (bytes? img)
         (create-image (image/read img nil :awt))

         (instance? BufferedImage img)
         (SwingFXUtils/toFXImage img nil)

         (instance? Image img)
         img

         :else
         (throw (ex-info "Not supported" {:input img})))))

(defn create-image-viewer
  "creates an image viewer"
  {:added "3.0"}
  ([title]
   (platform/do-fx
    (doto ^Stage (display/fx-display (ImageView.))
      (.setTitle title)))))

(defn display-image
  "displays the image"
  {:added "3.0"}
  ([img]
   (display-image img (create-image-viewer "") nil))
  ([img viewer size]
   (let [^ImageView imgview (-> (.getScene ^Stage viewer)
                                ^Group (.getRoot)
                                (.getChildren)
                                (first))]
     (platform/do-fx
      (let [img (create-image img)
            _  (doto imgview
                 (-> (.setImage img)))]
        (cond-> ^Stage viewer
          size (doto (.setWidth (first size)) (.setHeight (second size)))))))))

(defn create-html-viewer
  "creates a html viewer"
  {:added "3.0"}
  ([title]
   (platform/do-fx
    (doto ^Stage (display/fx-display (WebView.))
      (.setTitle title)))))

(defn display-html
  "displays the html content"
  {:added "3.0"}
  ([s]
   (display-html s (create-html-viewer "") [600 400]))
  ([s viewer size]
   (let [^WebView webview (.getRoot (.getScene ^Stage viewer))]
     (platform/do-fx
      (let [_  (doto webview
                 (-> (.getEngine)
                     (.loadContent s)))]
        (cond-> ^Stage viewer
          size (doto (.setWidth (first size)) (.setHeight (second size)))))))))
