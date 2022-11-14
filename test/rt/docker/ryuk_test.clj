(ns rt.docker.ryuk-test
  (:use code.test)
  (:require [rt.docker.ryuk :refer :all]
            [rt.docker.common :as common]))


^{:refer rt.docker.common/CANARY :guard true :adopt true :added "4.0"}
(fact "executes a shell command"
  ^:hidden
  
  (common/raw-exec ["docker" "--host" (or common/*host* "127.0.0.1") "ps"   "--format" "{{json .}}"]
                   {})
  => coll?)


^{:refer rt.docker.ryuk/start-ryuk :added "4.0"}
(fact "starts the reaper"
  ^:hidden
  
  (start-ryuk)
  => map?)

^{:refer rt.docker.ryuk/stop-ryuk :added "4.0"}
(fact "stops the reaper"
  ^:hidden
  
  (stop-ryuk))

^{:refer rt.docker.ryuk/start-reaped :added "4.0"}
(comment "starts a reaped container"
  ^:hidden

  (start-reaped {:id     "test"
                 :image  "node:16"
                 :ports  [6379]
                 :cmd    ["node"]})
  => map?)

^{:refer rt.docker.ryuk/stop-all-reaped :added "4.0"}
(fact "stops all reaped"
  ^:hidden
  
  (stop-all-reaped))
