(ns lib.lucene.impl.index-test
  (:use code.test)
  (:require [lib.lucene.impl.index :refer :all]
            [lib.lucene.impl.analyzer :as analyzer]))

^{:refer lib.lucene.impl.index/directory :added "3.0"}
(fact "creates a lucene directory (store"

  (directory {:store :memory})
  => org.apache.lucene.store.ByteBuffersDirectory)

^{:refer lib.lucene.impl.index/directory-memory :added "3.0"}
(fact "creates a ram directory"

  (directory-memory {})
  => org.apache.lucene.store.ByteBuffersDirectory)

^{:refer lib.lucene.impl.index/directory-disk :added "3.0"}
(fact "creates a disk based directory"

  (directory-disk {:path "test-scratch/lib.lucene/index"})
  => org.apache.lucene.store.NIOFSDirectory)

^{:refer lib.lucene.impl.index/writer :added "3.0"}
(fact "creates an IndexWriter"

  (writer (directory {:store :memory}))
  => org.apache.lucene.index.IndexWriter)

^{:refer lib.lucene.impl.index/reader :added "3.0"}
(fact "creates an IndexReader"

  (reader (doto (directory {:store :memory})
            (add-entry-directory (analyzer/analyzer {:type :standard})
                                 {}
                                 {:id "hello"}
                                 nil)))
  => org.apache.lucene.index.StandardDirectoryReader)

^{:refer lib.lucene.impl.index/close :added "3.0"}
(fact "closes the writer or reader")

^{:refer lib.lucene.impl.index/add-entry :added "3.0"}
(fact "adds an entry to the index")

^{:refer lib.lucene.impl.index/add-entry-writer :added "3.0"}
(fact "adds an entry given writer")

^{:refer lib.lucene.impl.index/add-entry-directory :added "3.0"}
(fact "adds an entry given directory")

^{:refer lib.lucene.impl.index/query-term :added "3.0"}
(fact "creates a query term"

  (query-term {:id "hello"})
  => org.apache.lucene.index.Term)

^{:refer lib.lucene.impl.index/update-entry :added "3.0"}
(fact "updates an entry to the index")

^{:refer lib.lucene.impl.index/update-entry-writer :added "3.0"}
(fact "updates an entry given writer")

^{:refer lib.lucene.impl.index/update-entry-directory :added "3.0"}
(fact "updates an entry given directory")

^{:refer lib.lucene.impl.index/remove-entry :added "3.0"}
(fact "removes an entry to the index")

^{:refer lib.lucene.impl.index/remove-entry-writer :added "3.0"}
(fact "removes an entry given writer")

^{:refer lib.lucene.impl.index/remove-entry-directory :added "3.0"}
(fact "removes an entry given directory")

^{:refer lib.lucene.impl.index/search :added "3.0"}
(fact "search through the index")

^{:refer lib.lucene.impl.index/search-reader :added "3.0"}
(fact "search using the reader")

^{:refer lib.lucene.impl.index/search-directory :added "3.0"}
(fact "search using the directory")

(comment

  (./import))
