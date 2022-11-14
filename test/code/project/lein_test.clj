(ns code.project.lein-test
  (:use code.test)
  (:require [code.project.lein :refer :all]))

^{:refer code.project.lein/project :added "3.0"}
(fact "returns the root project map"

  (project)
  => map?)

^{:refer code.project.lein/project-name :added "3.0"}
(fact "returns the project name"

  (project-name)
  => symbol?)
