(ns std.text.index-test
  (:use code.test)
  (:require [std.text.index :refer :all]))

^{:refer std.text.index/make-index :added "3.0"}
(fact "creates a index for search"

  (make-index))

^{:refer std.text.index/add-entry :added "3.0"}
(fact "helper function for `index`")

^{:refer std.text.index/remove-entries :added "3.0"}
(fact "helper function for `unindex")

^{:refer std.text.index/index-text :added "3.0"}
(fact "adds text to index")

^{:refer std.text.index/unindex-text :added "3.0"}
(fact "removes text from index")

^{:refer std.text.index/unindex-all :added "3.0"}
(fact "clears index")

^{:refer std.text.index/query :added "3.0"}
(fact "queries index for results")

^{:refer std.text.index/merge-and :added "3.0"}
(fact "merges results using and")

^{:refer std.text.index/merge-or :added "3.0"}
(fact "merges results using or")

^{:refer std.text.index/search :added "3.0"}
(fact "searchs index for text")

^{:refer std.text.index/save-index :added "3.0"}
(fact "saves index to file")

^{:refer std.text.index/load-index :added "3.0"}
(fact "loads index from file")

(comment
  (./import))
