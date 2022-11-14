(ns xt.lang.base-client-test
  (:use code.test)
  (:require [xt.lang.base-client :refer :all]))

^{:refer xt.lang.base-client/client-basic :added "4.0"}
(fact "creates a basic client")

^{:refer xt.lang.base-client/client-ws :added "4.0"}
(fact "creates a basic websocket client")
