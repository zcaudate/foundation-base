(ns rt.docker.common-test
  (:use code.test)
  (:require [rt.docker.common :refer :all]
            [rt.docker.ryuk :as ryuk]))

^{:refer rt.docker.common/raw-exec :guard true :added "4.0"}
(fact "executes a shell command"
  ^:hidden
  
  (raw-exec ["docker" "--host" (or *host* "127.0.0.1") "ps"   "--format" "{{json .}}"]
            {})
  => coll?)

^{:refer rt.docker.common/raw-command :added "4.0"}
(fact "executes a docker command"
  ^:hidden
  
  (raw-command ["ps"])
  => vector?)

^{:refer rt.docker.common/get-ip :added "4.0"
  :setup [(ryuk/start-ryuk)]}
(fact "gets the ip of a container"
  ^:hidden
  
  (get-ip (:container-id (ryuk/start-ryuk)))
  => string?)

^{:refer rt.docker.common/list-containers :added "4.0"}
(fact "gets all local containers"

  (list-containers)
  => vector?)

^{:refer rt.docker.common/has-container? :added "4.0"}
(fact "checks that a container exists"
  ^:hidden
  
  (has-container? (ryuk/start-ryuk))
  => true)

^{:refer rt.docker.common/start-container :added "4.0"}
(fact "starts a container")

^{:refer rt.docker.common/stop-container :added "4.0"}
(fact "stops a container")
