(ns fx.gui.data.common-test
  (:use code.test)
  (:require [fx.gui.data.common :refer :all]))

^{:refer fx.gui.data.common/get-image-viewer :added "3.0"}
(fact "creates the image viewer")

^{:refer fx.gui.data.common/get-html-viewer :added "3.0"}
(fact "creates the html viewer")
