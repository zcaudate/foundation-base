(ns std.config.ext.yaml-test
  (:use code.test)
  (:require [std.config.ext.yaml :refer :all]
            [script.yaml :as yaml]))

^{:refer std.config.ext.yaml/resolve-type-yaml :added "3.0"}
(fact "resolves yaml config"

  (resolve-type-yaml nil (yaml/write {:a 1 :b 2}))
  => {:a 1, :b 2})
