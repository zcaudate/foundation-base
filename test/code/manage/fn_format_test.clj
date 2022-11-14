(ns code.manage.fn-format-test
  (:use code.test)
  (:require [code.manage.fn-format :refer :all]))

^{:refer code.manage.fn-format/list-transform :added "3.0"}
(fact "transforms `(.. [] & body)` to `(.. ([] & body))`")

^{:refer code.manage.fn-format/fn:list-forms :added "3.0"}
(fact "query to find `defn` and `defmacro` forms with a vector")

^{:refer code.manage.fn-format/fn:defmethod-forms :added "3.0"}
(fact "query to find `defmethod` forms with a vector")

^{:refer code.manage.fn-format/fn-format :added "3.0"}
(fact "function to refactor the arglist and body")
