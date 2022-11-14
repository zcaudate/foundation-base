(ns js.lib.metamask-test
  (:use code.test)
  (:require [js.lib.metamask :refer :all]))

^{:refer js.lib.metamask/onboarding :added "4.0"}
(fact "creates the onboarding object")

^{:refer js.lib.metamask/detectProvider :added "4.0"}
(fact "detects the metamask provider")

^{:refer js.lib.metamask/hasMetaMask? :added "4.0"}
(fact "checks that platform contains metamask")

^{:refer js.lib.metamask/request :added "4.0"}
(fact "creates a metamask request")
