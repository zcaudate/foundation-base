(ns std.config.ext.toml-test
  (:use code.test)
  (:require [std.config.ext.toml :refer :all]
            [script.toml :as toml]))

^{:refer std.config.ext.toml/resolve-type-toml :added "3.0"}
(fact "resolves toml config"

  (resolve-type-toml nil (toml/write {:a 1 :b 2}))
  => {:a 1, :b 2})
