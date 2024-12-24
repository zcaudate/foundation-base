(ns rt.postgres.grammar.form-defn-test
  (:use code.test)
  (:require [rt.postgres.grammar.form-defn :refer :all]
            [rt.postgres.grammar :as g]
            [std.lang :as l]
            [std.lib :as h]))

^{:refer rt.postgres.grammar.form-defn/pg-defn-format :added "4.0"}
(fact "formats a defn form"
  ^:hidden
  
  (pg-defn-format
   '(defn ^{:- [:real]
            :%% :python}
      hello [] (return 1)))
  => '
     [{:doc "",
       :- [:real],
       :static/return [:real],
       :static/language :python,
       :static/props [],
       :static/input []}
      (defn hello [] (return 1))])

^{:refer rt.postgres.grammar.form-defn/pg-defn :added "4.0"}
(fact "creates the complete defn"
  ^:hidden
  
  (l/with:emit
   (pg-defn '(defn ^{:- [:real]
                     :static/language :python}
               sales-tax
               [:real subtotal
                :real fraction := 0.06]
               (return (* fraction subtotal)))
            g/+grammar+
            {:snapshot (l/get-snapshot (l/runtime-library))}))
  
  => (std.string/|
      "CREATE OR REPLACE FUNCTION sales_tax("
      "  subtotal REAL,"
      "  fraction REAL DEFAULT 0.06"
      ") RETURNS REAL AS $$"
      ""
      "  return fraction * subtotal"
      ""
      "$$ LANGUAGE 'plpython3u';")

  (l/with:emit
   (pg-defn '(defn ^{:- [:real]
                     :static/language :default}
               sales-tax
               [:real subtotal
                :real fraction := 0.06]
               (return (* fraction subtotal)))
            g/+grammar+
            {:snapshot (l/get-snapshot (l/runtime-library))}))
  => (std.string/|
      "CREATE OR REPLACE FUNCTION sales_tax("
      "  subtotal REAL,"
      "  fraction REAL DEFAULT 0.06"
      ") RETURNS REAL AS $$"
      "BEGIN"
      "  RETURN fraction * subtotal;"
      "END;"
      "$$ LANGUAGE 'plpgsql';"))
