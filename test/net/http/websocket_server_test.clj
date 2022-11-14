(ns net.http.websocket-server-test
  (:use code.test)
  (:require [net.http.websocket-server :refer :all]))

^{:refer net.http.websocket-server/run-basic-server :added "4.0"}
(fact "runs the basic wss server (unfinished)")

^{:refer net.http.websocket-server/handle-connection :added "4.0"}
(fact "handles the wss connection (unfinished)")
