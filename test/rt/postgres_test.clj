(ns rt.postgres-test
  (:use code.test)
  (:require [rt.postgres :as pg]))

^{:refer rt.postgres/purge-postgres :added "4.0"}
(fact "purges the rt.postgres library. Used for debugging")

^{:refer rt.postgres/purge-scratch :added "4.0"}
(fact "purges the rt.postgres scratch library. Used for debugging")

^{:refer rt.postgres/get-rev :added "4.0"}
(fact "formats access table")
