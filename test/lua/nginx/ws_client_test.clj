(ns lua.nginx.ws-client-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :basic
   :config {:program :resty}
   :require [[lua.nginx.ws-client :as ws-client]]})

(fact:global
 {:setup    [(l/rt:restart)
             (l/rt:scaffold :lua)
             (!.lua
              (:= ngxwsclient (require "resty.websocket.client")))]
  :teardown [(l/rt:stop)]})

^{:refer lua.nginx.ws-client/new :added "4.0"}
(fact "creates a new ws client"
  ^:hidden
  
  (!.lua
   (var ws (ws-client/new))
   (var url "wss://pusher.chain.so:443/app/e9f5cc20074501ca7395?client=lua-pusher&version=4.0.1&protocol=7")
   (var '[ok err] (ws-client/connect ws url {:ssl_verify false}))
   [ok err])
  => [1])
