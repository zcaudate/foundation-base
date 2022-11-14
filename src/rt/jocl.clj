(ns rt.jocl
  (:require [rt.jocl.common :as common]
            [rt.jocl.exec :as exec]
            [rt.jocl.meta :as meta]
            [rt.jocl.type :as type]
            [rt.jocl.runtime :as rt]
            [std.lib :as h]))

(h/intern-in meta/platform:default
             meta/platform-info
             meta/device-info
             meta/kernel-info
             meta/queue-info
             meta/context-info
             meta/program-info

             meta/device:cpu
             meta/device:gpu

             exec/exec
             rt/jocl
             rt/jocl:create)

