(ns code.manage.ns-format-test
  (:use code.test)
  (:require [code.manage.ns-format :refer :all]))

^{:refer code.manage.ns-format/first-element :added "3.0"}
(fact "returns first element of list")

^{:refer code.manage.ns-format/key-order :added "3.0"}
(fact "gets key order")

^{:refer code.manage.ns-format/replace-nodes :added "3.0"}
(fact "replace nodes with new ones")

^{:refer code.manage.ns-format/sort-nodes :added "3.0"}
(fact "sorts nodes")

^{:refer code.manage.ns-format/merge-nodes :added "3.0"}
(fact "merge nodes")

^{:refer code.manage.ns-format/merge-eligible :added "3.0"}
(fact "merge nodes that are not whitespace")

^{:refer code.manage.ns-format/merge-forms :added "3.0"}
(fact "merge two forms")

^{:refer code.manage.ns-format/ns:merge-forms :added "3.0"}
(fact "merges in the `ns` form")

^{:refer code.manage.ns-format/ns:sort-forms :added "3.0"}
(fact "sorts in the `ns` form")

^{:refer code.manage.ns-format/use:merge-forms :added "3.0"}
(fact "merge in the `ns` :use form")

^{:refer code.manage.ns-format/require:vectify-forms :added "3.0"}
(fact "changes :require forms into a vectors")

^{:refer code.manage.ns-format/require:expand-shorthand :added "3.0"}
(fact "expands shorthand :require forms into a vectors")

^{:refer code.manage.ns-format/require:merge-forms :added "3.0"}
(fact "merge :require forms that are identical")

^{:refer code.manage.ns-format/import:listify-forms :added "3.0"}
(fact "changes :import forms into a lists")

^{:refer code.manage.ns-format/import:merge-forms :added "3.0"}
(fact "merge :import forms that are identical")

^{:refer code.manage.ns-format/import:merge-form-entries :added "3.0"}
(fact "merge :import form entries that are identical")

^{:refer code.manage.ns-format/all:sort-forms :added "3.0"}
(fact "sorts the entries by alphabetical order")

^{:refer code.manage.ns-format/ns-format :added "3.0"}
(fact "top-level ns-format form")