(ns code.manage.ns-rename-test
  (:use code.test)
  (:require [code.manage.ns-rename :refer :all]))

^{:refer code.manage.ns-rename/move-list :added "3.0"}
(fact "compiles a list of file movements")

^{:refer code.manage.ns-rename/change-list :added "3.0"}
(fact "compiles a list of code changes")

^{:refer code.manage.ns-rename/ns-rename :added "3.0"}
(fact "top-level ns rename function")