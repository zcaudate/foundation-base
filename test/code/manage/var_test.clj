(ns code.manage.var-test
  (:use code.test)
  (:require [code.manage.var :refer :all]))

^{:refer code.manage.var/create-candidates :added "3.0"}
(fact "creates candidates for search")

^{:refer code.manage.var/find-candidates :added "3.0"}
(fact "finds candidates in current namespace")

^{:refer code.manage.var/find-usages :added "3.0"}
(fact "top-level find-usage query")