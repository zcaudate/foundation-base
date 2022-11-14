(ns script.sql.expr-test
  (:use code.test)
  (:require [script.sql.expr :refer :all]
            [std.string :as str]))

^{:refer script.sql.expr/as-? :added "3.0"}
(fact "Given a hash map of column names and values, or a vector of column names,
  return a string of `?` placeholders for them.")

^{:refer script.sql.expr/as-cols :added "3.0"}
(fact "Given a sequence of raw column names, return a string of all the
  formatted column names.

  If a raw column name is a keyword, apply `:column-fn` to its name,
  from the options if present.

  If a raw column name is a vector pair, treat it as an expression with
  an alias. If the first item is a keyword, apply `:column-fn` to its
  name, else accept it as-is. The second item should be a keyword and
  that will have `:column-fn` applied to its name.

  This allows columns to be specified as simple names, e.g., `:foo`,
  as simple aliases, e.g., `[:foo :bar]`, or as expressions with an
  alias, e.g., `[\"count(*)\" :total]`.")

^{:refer script.sql.expr/as-keys :added "3.0"}
(fact "Given a hash map of column names and values, return a string of all the
  column names.

  Applies any `:column-fn` supplied in the options.")

^{:refer script.sql.expr/by-keys :added "3.0"}
(fact "Given a hash map of column names and values and a clause type
  (`:set`, `:where`), return a vector of a SQL clause and its parameters.

  Applies any `:column-fn` supplied in the options.")

^{:refer script.sql.expr/for-delete :added "3.0"}
(fact "Given a table name and either a hash map of column names and values or a
  vector of SQL (where clause) and its parameters, return a vector of the
  full `DELETE` SQL string and its parameters.

  Applies any `:table-fn` / `:column-fn` supplied in the options.

  If `:suffix` is provided in `opts`, that string is appended to the
  `DELETE ...` statement.")

^{:refer script.sql.expr/for-insert :added "3.0"}
(fact "Given a table name and a hash map of column names and their values,
  return a vector of the full `INSERT` SQL string and its parameters.

  Applies any `:table-fn` / `:column-fn` supplied in the options.

  If `:suffix` is provided in `opts`, that string is appended to the
  `INSERT ...` statement.")

^{:refer script.sql.expr/for-insert-multi :added "3.0"}
(fact "Given a table name, a vector of column names, and a vector of row values
  (each row is a vector of its values), return a vector of the full `INSERT`
  SQL string and its parameters.

  Applies any `:table-fn` / `:column-fn` supplied in the options.

  If `:suffix` is provided in `opts`, that string is appended to the
  `INSERT ...` statement.")

^{:refer script.sql.expr/for-update :added "3.0"}
(fact "Given a table name, a vector of column names to set and their values, and
  either a hash map of column names and values or a vector of SQL (where clause)
  and its parameters, return a vector of the full `UPDATE` SQL string and its
  parameters.

  Applies any `:table-fn` / `:column-fn` supplied in the options.

  If `:suffix` is provided in `opts`, that string is appended to the
  `UPDATE ...` statement.")

^{:refer script.sql.expr/for-upsert :added "0.1"}
(fact "generate upsert command"
  ^:hidden

  (for-upsert :user {:id "a" :account "12345"} {})
  => [(str/| "INSERT INTO user"
             " (id, account)"
             " VALUES (?, ?)"
             " ON CONFLICT (id)"
             " DO UPDATE SET (account)"
             " = ROW(EXCLUDED.account)")
      "a" "12345"])

^{:refer script.sql.expr/for-upsert-multi :added "0.1"}
(fact "generate upsert-multi command"
  ^:hidden

  (for-upsert-multi :user [:id :account]
                    [["a" "12345"]
                     ["b" "67890"]]
                    {})
  => [(str/| "INSERT INTO user"
             " (id, account)"
             " VALUES" " (?, ?),"
             " (?, ?)"
             " ON CONFLICT (id)"
             " DO UPDATE SET (account)"
             " = ROW(EXCLUDED.account)")
      "a" "12345" "b" "67890"])

^{:refer script.sql.expr/for-cas :added "3.0"}
(fact "generates cas command"
  ^:hidden

  (for-cas :account
           {:id "id0" :name "hello"}
           {:id "id0" :name "world"}
           {})
  => [(str/| "DO $$"
             " DECLARE v_id TEXT;"
             " BEGIN"
             " UPDATE account"
             " SET (id, name) = (?, ?)"
             " WHERE id = (SELECT id FROM account WHERE id = ? AND name = ? LIMIT 1)" " RETURNING id INTO v_id;"
             " IF count(v_id) = 0 THEN"
             "  RAISE EXCEPTION 'Cas Error for (:account, id0)';"
             " END IF;"
             "END; $$")
      "id0" "world" "id0" "hello"])
