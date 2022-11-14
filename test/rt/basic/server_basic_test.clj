(ns rt.basic.server-basic-test
  (:use code.test)
  (:require [rt.basic.server-basic :refer :all]))

^{:refer rt.basic.server-basic/get-port :added "4.0"}
(fact "gets the port given lang and id")

^{:refer rt.basic.server-basic/wait-ready :added "4.0"}
(fact "wait until server is ready")

^{:refer rt.basic.server-basic/run-basic-server :added "4.0"}
(fact "runs a basic socket server")

^{:refer rt.basic.server-basic/get-encoding :added "4.0"}
(fact "gets the encoding to use"

  (get-encoding :json)
  => map?)

^{:refer rt.basic.server-basic/get-relay :added "4.0"}
(fact "gets the relay associated with the server"

  (get-relay (start-server "test" :lua nil))
  => nil)

^{:refer rt.basic.server-basic/ping-relay :added "4.0"}
(fact "checks if the relay is still valid"

  (ping-relay (start-server "test" :lua nil))
  => false)

^{:refer rt.basic.server-basic/raw-eval-basic-server :added "4.0"}
(fact "performs raw eval")

^{:refer rt.basic.server-basic/create-basic-server :added "4.0"}
(fact "creates a basic server"

  (create-basic-server "test" :lua nil :json)
  => map?)

^{:refer rt.basic.server-basic/start-server :added "4.0"}
(fact "start server function"

  (start-server "test" :lua nil)
  => map?)

^{:refer rt.basic.server-basic/get-server :added "4.0"}
(fact "gets a server given id"

  (get-server "test" :lua)
  => map?)

^{:refer rt.basic.server-basic/stop-server :added "4.0"}
(fact "stops a server"

  (loop []
    (if (stop-server "test" :lua)
      (recur))))


(comment
  (./import))
