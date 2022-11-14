(ns lib.jdbc.types-test
  (:use code.test)
  (:require [lib.jdbc.types :refer :all]))

^{:refer lib.jdbc.types/->connection :added "4.0"}
(fact 
  "Create a connection wrapper.

  The connection  wrapper is need because it
  implemens IMeta interface that is mandatory
  for transaction management.")

^{:refer lib.jdbc.types/->cursor :added "4.0"}
(fact "creates a cursor from prepared statement")