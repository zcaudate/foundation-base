(ns lib.jdbc-test
  (:use code.test)
  (:require [lib.jdbc :refer :all]))

^{:refer lib.jdbc/connection :added "4.0"}
(fact 
  "Creates a connection to a database. As parameter accepts:

  - dbspec map containing connection parameters
  - dbspec map containing a datasource (deprecated)
  - URI or string (interpreted as uri)
  - DataSource instance

  The dbspec map has this possible variants:

  Classic approach:

  - `:subprotocol` -> (required) string that represents a vendor name (ex: postgresql)
  - `:subname` -> (required) string that represents a database name (ex: test)
    (many others options that are pased directly as driver parameters)

  Pretty format:

  - `:vendor` -> (required) string that represents a vendor name (ex: postgresql)
  - `:name` -> (required) string that represents a database name (ex: test)
  - `:host` -> (optional) string that represents a database hostname (default: 127.0.0.1)
  - `:port` -> (optional) long number that represents a database port (default: driver default)
    (many others options that are pased directly as driver parameters)

  URI or String format: `vendor://user:password@host:post/dbname?param1=value`

  Additional options:

  - `:schema` -> string that represents a schema name (default: nil)
  - `:read-only` -> boolean for mark entire connection read only.
  - `:isolation-level` -> keyword that represents a isolation level (`:none`, `:read-committed`,
                        `:read-uncommitted`, `:repeatable-read`, `:serializable`)

  Opions can be passed as part of dbspec map, or as optional second argument.
  For more details, see documentation.")

^{:refer lib.jdbc/prepared-statement? :added "4.0"}
(fact "Check if specified object is prepared statement.")

^{:refer lib.jdbc/prepared-statement :added "4.0"}
(fact 
  "Given a string or parametrized sql in sqlvec format
  return an instance of prepared statement.")

^{:refer lib.jdbc/execute :added "4.0"}
(fact 
  "Execute a query and return a number of rows affected.

      (with-open [conn (jdbc/connection dbspec)]
        (jdbc/execute conn \"create table foo (id integer);\"))

  This function also accepts sqlvec format.")

^{:refer lib.jdbc/fetch :added "4.0"}
(fact 
  "Fetch eagerly results executing a query.

  This function returns a vector of records (default) or
  rows (depending on specified opts). Resources are relased
  inmediatelly without specific explicit action for it.

  It accepts a sqlvec, plain sql or prepared statement
  as query parameter.")

^{:refer lib.jdbc/fetch-one :added "4.0"}
(fact "Fetch eagerly one restult executing a query.")

^{:refer lib.jdbc/fetch-lazy :added "4.0"}
(fact 
  "Fetch lazily results executing a query.

      (with-open [cursor (jdbc/fetch-lazy conn sql)]
        (doseq [item (jdbc/cursor->lazyseq cursor)]
          (do-something-with item)))

  This function returns a cursor instead of result.
  You should explicitly close the cursor at the end of
  iteration for release resources.")

^{:refer lib.jdbc/cursor->lazyseq :added "4.0"}
(fact 
  "Transform a cursor in a lazyseq.

  The returned lazyseq will return values until a cursor
  is closed or all values are fetched.")