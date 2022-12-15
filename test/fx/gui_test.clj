(ns fx.gui-test
  (:use code.test)
  (:require [fx.gui :refer :all]))

^{:refer fx.gui/load-control :added "3.0"}
(fact "loads a control given class and fxml file")

^{:refer fx.gui/display :added "3.0"}
(fact "displays a control")

^{:refer fx.gui/add-close-hook :added "3.0"}
(fact "adds close hook to window")

^{:refer fx.gui/create-image :added "3.0"}
(fact "creates an image from bytes")

^{:refer fx.gui/create-image-viewer :added "3.0"}
(fact "creates an image viewer")

^{:refer fx.gui/display-image :added "3.0"}
(fact "displays the image")

^{:refer fx.gui/create-html-viewer :added "3.0"}
(fact "creates a html viewer")

^{:refer fx.gui/display-html :added "3.0"}
(fact "displays the html content")

(comment
  (./import)
  (./reset))
