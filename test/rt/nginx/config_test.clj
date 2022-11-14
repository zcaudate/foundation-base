(ns rt.nginx.config-test
  (:use code.test)
  (:require [rt.nginx.config :refer :all]))

^{:refer rt.nginx.config/create-resty-params :added "4.0"}
(fact "creates default resty params")

^{:refer rt.nginx.config/create-conf :added "4.0"}
(fact "cerates default conf")
