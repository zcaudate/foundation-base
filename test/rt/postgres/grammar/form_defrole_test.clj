(ns rt.postgres.grammar.form-defrole-test
  (:use code.test)
  (:require [rt.postgres.grammar.form-defrole :refer :all]))

^{:refer rt.postgres.grammar.form-defrole/pg-defrole-access :added "4.0"}
(fact "creates defrole access form")

^{:refer rt.postgres.grammar.form-defrole/pg-defrole :added "4.0"}
(fact "creates defrole form")
