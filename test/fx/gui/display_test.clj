(ns fx.gui.display-test
  (:use code.test)
  (:require [fx.gui.display :refer :all]
            [fx.gui.platform :as platform]
            [fx.gui :as gui]))

(fact:global
 {:setup [(platform/init!)]})

^{:refer fx.gui.display/fx-group :added "3.0"}
(fact "creates a group"

  (fx-group [])
  => javafx.scene.Group)

^{:refer fx.gui.display/fx-scene :added "3.0"}
(fact "creates a scene"

  (platform/return-fx
   (fn []
     (fx-scene (javafx.scene.control.Label. "hello"))))
  => javafx.scene.Scene)

^{:refer fx.gui.display/fx-prepare :added "3.0"}
(comment "constructs a stage"

  (platform/return-fx
   (fn [] (fx-prepare (javafx.scene.control.Label. "hello"))))
  => javafx.stage.Stage)

^{:refer fx.gui.display/fx-display :added "3.0"}
(comment "displays an item"

  (platform/return-fx
   (fn []
     (-> (fx-display (javafx.scene.control.Label. "hello"))
         (.close)))))

(comment
  (./import))
