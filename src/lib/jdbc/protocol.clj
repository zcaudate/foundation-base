(ns lib.jdbc.protocol)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Internal Protocols
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol IConnection
  "Represents a connection like object that wraps
  a raw jdbc connection with some other data."
  (-connection [_] "Create or obtain existing connection"))

(defprotocol IExecute
  (-execute [q conn opts] "Execute a query and return a number of rows affected."))

(defprotocol IFetch
  (-fetch [q conn opts] "Fetch eagerly results executing query."))

(defprotocol IDatabaseMetadata
  "Allows uniform database metadata extraction."
  (-get-database-metadata [_] "Get metadata instance."))

(defprotocol IPreparedStatement
  "Responsible of building prepared statements."
  (-prepared-statement [_ connection options] "Create a prepared statement."))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SQL Extension Protocols
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol ISQLType
  "Protocol that exposes uniform way for convert user
  types to sql/jdbc compatible types and uniform set parameters
  to prepared statement instance. Default implementation available
  for Object and nil values."

  (-as-sql-type [_ conn] "Convert user type to sql type.")
  (-set-stmt-parameter! [this conn stmt index] "Set value to statement."))

(defprotocol ISQLResultSetReadColumn
  "Protocol that exposes uniform way to convert values
  obtained from result set to user types. Default implementation
  available for Object, Boolean, and nil."

  (-from-sql-type [_ conn metadata index] "Convert sql type to user type."))
