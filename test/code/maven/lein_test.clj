(ns code.maven.lein-test
  (:use code.test)
  (:require [code.maven.lein :refer :all]))

^{:refer code.maven.lein/deploy-lein :added "4.0"}
(fact "temporary hack to deploy by shelling out to leiningen")
