(ns lib.jdbc.impl-test
  (:use code.test)
  (:require [lib.jdbc.impl :refer :all]))

^{:refer lib.jdbc.impl/uri->dbspec :added "4.0"}
(fact 
  "Parses a dbspec as uri into a plain dbspec. This function
  accepts `java.net.URI` or `String` as parameter.")

^{:refer lib.jdbc.impl/cursor->lazyseq :added "4.0"}
(fact "converts a cursor to a lazyseq")