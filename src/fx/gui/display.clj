(ns fx.gui.display
  (:import  (javafx.stage Screen
                          Stage
                          Window)
            (javafx.scene Parent
                          Scene
                          Group
                          Node)))

(defn fx-group
  "creates a group
 
   (fx-group [])
   => javafx.scene.Group"
  {:added "3.0"}
  ([^java.util.Collection coll]
   (Group. coll)))

(defn fx-scene
  "creates a scene
 
   (platform/return-fx
    (fn []
      (fx-scene (javafx.scene.control.Label. \"hello\"))))
   => javafx.scene.Scene"
  {:added "3.0"}
  ([^Node ui]
   (cond  (instance? Parent ui)
          (Scene. ui)

          :else
          (Scene. (fx-group [ui])))))

(defn ^Stage fx-prepare
  "constructs a stage
 
   (platform/return-fx
    (fn [] (fx-prepare (javafx.scene.control.Label. \"hello\"))))
   => javafx.stage.Stage"
  {:added "3.0"}
  ([ui]
   (cond (instance? Stage  ui)   ui
         (instance? Scene  ui)  (doto (Stage.) (.setScene ui))
         :else (fx-prepare (fx-scene ui)))))

(defn ^Stage fx-display
  "displays an item
 
   (platform/return-fx
    (fn []
     (-> (fx-display (javafx.scene.control.Label. \"hello\"))
          (.close))))"
  {:added "3.0"}
  ([ui]
   (doto ^Stage (fx-prepare ui)
     (.show)
     (.requestFocus))))
