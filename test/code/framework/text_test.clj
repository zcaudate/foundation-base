(ns code.framework.text-test
  (:use code.test)
  (:require [code.framework.text :refer :all]))

^{:refer code.framework.text/summarise-deltas :added "3.0"}
(fact "summary of what changes have been made")

^{:refer code.framework.text/deltas :added "3.0"}
(fact "returns a list of changes between the original and revised versions")

^{:refer code.framework.text/highlight-lines :added "3.0"}
(fact "highlights a segment of code in a different color")

^{:refer code.framework.text/->string :added "3.0"}
(fact "turns a set of results into a output string for printing")

(comment
  (code.manage/import {:write true}))
