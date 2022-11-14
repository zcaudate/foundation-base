(ns std.config.ext.json-test
  (:use code.test)
  (:require [std.config.ext.json :refer :all]
            [std.json :as json]))

^{:refer std.config.ext.json/resolve-type-json :added "3.0"}
(fact "resolves json config"

  (resolve-type-json nil (json/write {:a 1 :b 2}))
  => {:b 2, :a 1})
