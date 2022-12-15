(ns rt.browser-test
  (:use code.test)
  (:require [rt.browser :as browser]
            [std.lib :as h]))

^{:refer rt.browser/goto :added "4.0"
  :setup [(def +browser+ (browser/browser {:port 19222}))]
  :teardown [(h/stop +browser+)]}
(fact "goto a given page"
  ^:hidden
  
  (browser/goto "https://www.baidu.com/" 4000 +browser+)
  => (contains-in {"targetInfo" {"attached" true, "url" "https://www.baidu.com/"}})
  
  @(browser/evaluate +browser+ "1+1")
  => {"value" 2, "type" "number", "description" "2"}

  (h/p:rt-raw-eval +browser+ "1+1")
  => 2)
