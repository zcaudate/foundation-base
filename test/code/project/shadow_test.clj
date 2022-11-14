(ns code.project.shadow-test
  (:use code.test)
  (:require [code.project.shadow :refer :all]))

^{:refer code.project.shadow/project :added "3.0"}
(comment "opens a shadow.edn file as the project"

  (project "../yin/shadow-cljs.edn"))
