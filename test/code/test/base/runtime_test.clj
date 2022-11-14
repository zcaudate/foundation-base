(ns code.test.base.runtime-test
  (:use code.test)
  (:require [code.test.base.runtime :refer :all]
            [std.lib :as h]))

(defn fsym []
  (first (list-facts)))

^{:refer code.test.base.runtime/purge-all :added "3.0"}
(fact "purges all facts from namespace")

^{:refer code.test.base.runtime/get-global :added "3.0"}
(fact "gets the global settings for namespace"

  (get-global))

^{:refer code.test.base.runtime/set-global :added "3.0"}
(fact "sets the global settings for namespace"

  (set-global {:check {:setup '[(prn "hello")]}})
  => '{:check {:setup [(prn "hello")]}})

^{:refer code.test.base.runtime/update-global :added "3.0"}
(fact "updates global data")

^{:refer code.test.base.runtime/list-links :added "3.0"}
(fact "lists ns links")

^{:refer code.test.base.runtime/clear-links :added "3.0"}
(fact "clear ns links")

^{:refer code.test.base.runtime/add-link :added "3.0"}
(fact "add ns link")

^{:refer code.test.base.runtime/remove-link :added "3.0"}
(fact "remove ms link")

^{:refer code.test.base.runtime/all-facts :added "3.0"}
(fact "retrieves a list of all the facts in a namespace"

  (keys (all-facts)))

^{:refer code.test.base.runtime/list-facts :added "3.0"}
(fact "lists all facts in current namespace"

  (first (list-facts))
  => 'test-code_test_base_runtime__purge_all)

^{:refer code.test.base.runtime/purge-facts :added "3.0"}
(comment "purges all facts in the namespace (for reload)"

  (purge-facts)
  (list-facts)
  => [])

^{:refer code.test.base.runtime/parse-args :added "3.0"}
(fact "helper function for variable args")

^{:refer code.test.base.runtime/get-fact :added "3.0"}
(fact "gets a fact"

  (get-fact (fsym) :refer)
  => 'code.test.base.runtime/purge-all ^:hidden

  (get-fact (fsym) :function :setup)
  => fn?

  (get-fact (h/ns-sym) (fsym) :function :setup)
  => fn?)

^{:refer code.test.base.runtime/set-fact :added "3.0"}
(fact "sets the entire data on a fact")

^{:refer code.test.base.runtime/set-in-fact :added "3.0"}
(fact "sets the property on a fact"

  (set-in-fact (fsym) [:function :other] (fn [])))

^{:refer code.test.base.runtime/get-flag :added "3.0"}
(fact "checks if the setup flag has been set"

  (get-flag (fsym) :setup))

^{:refer code.test.base.runtime/set-flag :added "3.0"}
(fact "sets the setup flag"

  (set-flag (fsym) :setup true))

^{:refer code.test.base.runtime/update-fact :added "3.0"}
(fact "updates a fact given a function")

^{:refer code.test.base.runtime/remove-fact :added "3.0"}
(fact "removes a fact from namespace")

^{:refer code.test.base.runtime/teardown-fact :added "3.0"
  :teardown [(+ 1 2 3)]}
(fact "runs the teardown hook"

  (teardown-fact (:id (find-fact {:line (h/code-line)})))
  => 6)

^{:refer code.test.base.runtime/setup-fact :added "3.0"
  :setup [(+ 1 2 3)]}
(fact "runs the setup hook"

  (setup-fact (:id (find-fact {:line (h/code-line)})))
  => 6)

^{:refer code.test.base.runtime/exec-thunk :added "3.0"}
(fact "executes the fact thunk (only the check")

^{:refer code.test.base.runtime/exec-slim :added "3.0"}
(fact "executes the fact slim (only the body")

^{:refer code.test.base.runtime/no-dots :added "3.0"}
(fact "removes dots and slash from the string")

^{:refer code.test.base.runtime/fact-id :added "3.0"}
(fact "returns an id from fact data"

  (fact-id {:refer 'code.test.base.runtime/fact-id})
  => 'test-code_test_base_runtime__fact_id)

^{:refer code.test.base.runtime/find-fact :added "3.0"}
(fact "the fact that is associated with a given line"

  (:id (find-fact {:line (h/code-line)}))
  => 'test-code_test_base_runtime__find_fact)

^{:refer code.test.base.runtime/run-op :added "3.0"}
(fact "common runtime functions for easy access"

  (run-op {:line (h/code-line)}
          :self))
