(ns lua.nginx.http-client-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.json :as json]
            [rt.nginx]))

(l/script- :lua
  {:runtime :basic
   :config  {:program :resty}
   :require [[xt.lang.base-lib :as k :include [:json]]
             [lua.nginx :as n]
             [lua.nginx.http-client :as http]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer lua.nginx.http-client/new :added "4.0"}
(fact "creates a new lua client"
  ^:hidden
  
  (!.lua
   (local ngxhttp (require "resty.http"))
   (http/new))
  => {"sock" {}, "keepalive" true})
