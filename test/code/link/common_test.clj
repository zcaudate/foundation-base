(ns code.link.common-test
  (:use code.test)
  (:require [code.link.common :refer :all]))

^{:refer code.link.common/-file-linkage :added "3.0"}
(fact "extendable function for `file-linkage`")

^{:refer code.link.common/file-linkage-fn :added "3.0"}
(fact "memoized function for `file-linkage` based on time")
