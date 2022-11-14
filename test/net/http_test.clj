(ns net.http-test
  (:use code.test)
  (:require [net.http :as http]
            [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :nginx.instance
   :require [[xt.lang.base-lib :as k]
             [lua.nginx :as n]]})

(fact:global
 {:setup     [(l/rt:restart)]
  :teardown  [(l/rt:stop)]})

^{:refer net.http/event-stream :added "4.0"
  :setup [(!.lua
           (local cjson (require "cjson"))
           (:= (.  DEBUG ["es_handler"])
               (fn []
                 (n/say "data: " (k/js-encode [1 2 3]))
                 (n/flush)
                 (n/say "data: " (k/js-encode [4 5 6]))
                 (n/flush)
                 (n/say "data: " (k/js-encode [7 8 9]))
                 (n/flush)))
           true)]}
(fact "creates a data-stream for checking errors"
  ^:hidden
  
  (def -ds- (http/event-stream (str "http://localhost:" (:port (l/rt :lua)) "/eval/es")))
  (Thread/sleep 100)
  
  @(:events -ds-)
  => ["[1,2,3]"
      "[4,5,6]"
      "[7,8,9]"])
