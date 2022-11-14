(ns code.maven.task-test
  (:use code.test)
  (:require [code.maven.task :refer :all]))

^{:refer code.maven.task/make-project :added "3.0"}
(fact "makes a maven compatible project"

  (make-project)
  => map?)
