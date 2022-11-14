(ns script.css-test
  (:use code.test)
  (:require [script.css :refer :all]))

^{:refer script.css/generate-style :added "3.0"}
(fact "creates a style string"

  (generate-style {:bold true})
  => "bold: true;")

^{:refer script.css/generate-css :added "3.0"}
(fact "creates a stylesheet"

  (generate-css [[:node {:bold true
                         :color "black"}]
                 [:h1  {:align :left}]])
  => "node {\n  bold: true;\n  color: black;\n}\nh1 {\n  align: left;\n}")

^{:refer script.css/parse-pair :added "3.0"}
(fact "parses a style pair"

  (parse-pair "bold: true")
  => [:bold "true"])

^{:refer script.css/parse-rule :added "3.0"}
(fact "helper function for parse-css")

^{:refer script.css/parse-css :added "3.0"}
(fact "reads a stylesheet entry from a string"

  (parse-css "node {\n  bold: true;\n  color: black;\n}\nh1 {\n  align: left;\n}")
  => [[:node {:bold "true", :color "black"}]
      [:h1 {:align "left"}]])

^{:refer script.css/parse-style :added "3.0"}
(fact "reads style map from string"

  (parse-style "bold: true;")
  => {:bold "true"})