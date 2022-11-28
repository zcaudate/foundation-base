(ns xt.lang.base-interval-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

^{:refer xt.lang.base-client/client-basic :added "4.0"}
(fact "creates a basic client")

^{:refer xt.lang.base-client/client-ws :added "4.0"}
(fact "creates a basic websocket client")


^{:refer xt.lang.base-interval/start-interval :added "4.0"}
(fact "TODO")

^{:refer xt.lang.base-interval/stop-interval :added "4.0"}
(fact "TODO")