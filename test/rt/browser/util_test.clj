(ns rt.browser.util-test
  (:use code.test)
  (:require [rt.browser.util :as util]
            [rt.browser.connection :as conn]
            [rt.browser.connection-test :as conn-test]))

(fact:global
 {:setup    [(conn-test/restart-scaffold)]
  :teardown [(conn-test/stop-scaffold)]})

^{:refer rt.browser.util/runtime-evaluate :added "4.0"
  :setup [(def +conn+
            (conn/conn-create {:port (:port (conn-test/start-scaffold))}))]}
(fact "performs runtime eval on connection"
  ^:hidden
  
  @(util/runtime-evaluate +conn+ "1")
  => {"value" 1, "type" "number", "description" "1"})

^{:refer rt.browser.util/page-navigate :added "4.0"}
(fact "navigates to a new url"
  ^:hidden
  
  @(util/page-navigate +conn+ "about:blank")
  => (contains {"frameId" string?,
                "loaderId" string?}))

^{:refer rt.browser.util/page-capture-screenshot :added "4.0"}
(fact "captures a screenshot from the browser"
  ^:hidden
  
  @(util/page-capture-screenshot +conn+)
  => bytes?)

^{:refer rt.browser.util/target-info :added "4.0"}
(fact "gets the target info"
  ^:hidden
  
  @(util/target-info +conn+)
  => (contains-in {"targetInfo" {"attached" true, "url" "about:blank"}}))

^{:refer rt.browser.util/target-create :added "4.0"}
(fact "creates a new target"
  ^:hidden
  
  @(util/target-create +conn+ "about:blank")
  => (contains {"targetId" string?}))

^{:refer rt.browser.util/target-close :added "4.0"}
(fact "closes a current target"
  ^:hidden
  
  @(util/target-close +conn+
                      (get @(util/target-create +conn+ "about:blank")
                           "targetId"))
  => {"success" true})
