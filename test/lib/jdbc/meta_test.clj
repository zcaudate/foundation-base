(ns lib.jdbc.meta-test
  (:use code.test)
  (:require [lib.jdbc.meta :refer :all]))

^{:refer lib.jdbc.meta/vendor-name :added "4.0"}
(fact "Get connection vendor name.")

^{:refer lib.jdbc.meta/catalog-name :added "4.0"}
(fact "Given a connection, get a catalog name.")

^{:refer lib.jdbc.meta/schema-name :added "4.0"}
(fact "Given a connection, get a schema name.")

^{:refer lib.jdbc.meta/is-readonly? :added "4.0"}
(fact "Returns true if a current connection is in read-only model.")

^{:refer lib.jdbc.meta/is-valid? :added "4.0"}
(fact "Given a connection, return true if connection has not ben closed it still valid.")

^{:refer lib.jdbc.meta/network-timeout :added "4.0"}
(fact "Given a connection, get network timeout.")

^{:refer lib.jdbc.meta/isolation-level :added "4.0"}
(fact "Given a connection, get a current isolation level.")

^{:refer lib.jdbc.meta/db-major-version :added "4.0"}
(fact "Given a connection, return a database major version number.")

^{:refer lib.jdbc.meta/db-minor-version :added "4.0"}
(fact "Given a connection, return a database minor version number.")

^{:refer lib.jdbc.meta/db-product-name :added "4.0"}
(fact "Given a connection, return a database product name.")

^{:refer lib.jdbc.meta/db-product-version :added "4.0"}
(fact "Given a connection, return a database product version.")

^{:refer lib.jdbc.meta/driver-name :added "4.0"}
(fact "Given a connection, return a current driver name")

^{:refer lib.jdbc.meta/driver-version :added "4.0"}
(fact "Given a connection, return a current driver version")