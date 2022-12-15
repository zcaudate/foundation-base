(ns rt.browser.impl-test
  (:use code.test)
  (:require [rt.browser.impl :as impl]
            [std.lang :as l]
            [std.lib :as h]
            [rt.basic.type-bench :as bench]
            [rt.browser.util :as util]))

(l/script- :js
  {:runtime :browser.instance
   :require [[xt.lang.base-lib :as k]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer rt.browser.impl/CANARY :adopt true :added "4.0"}
(fact "performs preliminary checks"
  
  (!.js (+ 1 2 3))
  => 6)

^{:refer rt.browser.impl/start-browser-bench :added "4.0"
  :setup [(def +rt+ (impl/browser:create {:lang :js}))]
  :teardown (bench/stop-bench-process (:port +rt+))}
(fact "starts the browser bench"
  ^:hidden

  (impl/start-browser-bench +rt+)
  => (contains {:type :bench/basic}))

^{:refer rt.browser.impl/start-browser-container :added "4.0"}
(fact "starts a browser container")

^{:refer rt.browser.impl/start-browser :added "4.0"}
(fact "starts the browser bench and connection")

^{:refer rt.browser.impl/stop-browser-raw :added "4.0"}
(fact "stops the browser")

^{:refer rt.browser.impl/raw-eval-browser :added "4.0"}
(fact "evaluates the browser"
  ^:hidden
  
  (impl/raw-eval-browser (l/rt :js)
                         "1 + 1")
  => 2)

^{:refer rt.browser.impl/invoke-ptr-browser :added "4.0"}
(fact "invokes the browser pointer"
  ^:hidden
  
  (impl/invoke-ptr-browser (l/rt :js)
                           k/identity
                           [1])
  => 1

  (l/with:input
    (impl/invoke-ptr-browser (l/rt :js)
                             k/identity
                             [1]))
  => string?)

^{:refer rt.browser.impl/browser:create :added "4.0"}
(fact "creates a browser")

^{:refer rt.browser.impl/browser :added "4.0"}
(fact "starts the browser")

^{:refer rt.browser.impl/wrap-browser-state :added "4.0"}
(fact "wrapper for the browser"
  ^:hidden
  
  @((impl/wrap-browser-state util/target-info)
    (l/rt :js))
  => (contains-in {"targetInfo" {"attached" true, "url" "about:blank"}}))
