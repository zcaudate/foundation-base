(ns lua.nginx.ws-client-test
  (:use code.test)
  (:require [lua.nginx.ws-client :refer :all]))

^{:refer lua.nginx.ws-client/new :added "4.0"}
(fact "creates a new ws client")
