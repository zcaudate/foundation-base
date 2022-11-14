(ns script.toml-test
  (:use code.test)
  (:require [script.toml :refer :all])
  (:refer-clojure :exclude [read]))

^{:refer script.toml/java->clojure :added "3.0"}
(fact "converts java object to map")

^{:refer script.toml/clojure->java :added "3.0"}
(fact "converts map to java object")

^{:refer script.toml/read :added "3.0"}
(fact "reads toml to map")

^{:refer script.toml/write :added "3.0"}
(fact "writes map to toml")
