(ns rt.javafx.common-test
  (:use code.test)
  (:require [rt.javafx.common :refer :all]))

^{:refer rt.javafx.common/download-assets :added "4.0"}
(fact "downloads lodash, react and react dom")

^{:refer rt.javafx.common/get-engine :added "4.0"}
(fact "gets the webengine")

^{:refer rt.javafx.common/instance-init :added "4.0"}
(fact "initialises the webview")

^{:refer rt.javafx.common/instance-list :added "4.0"}
(fact "lists all instances")

^{:refer rt.javafx.common/instance-get :added "4.0"}
(fact "gets an instance by id")

^{:refer rt.javafx.common/instance-display :added "4.0"}
(fact "displays the webview in javafx stage")

^{:refer rt.javafx.common/instance-start :added "4.0"}
(fact "starts the instance")

^{:refer rt.javafx.common/instance-stop :added "4.0"}
(fact "stops the instance")
