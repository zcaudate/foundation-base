(ns rt.shell
  (:require [std.lang :as l]
            [std.lib :as h]
            [rt.shell.suite-core :as suite]
            [rt.shell.interface-basic :as basic]
            [rt.shell.interface-remote :as remote])
  (:refer-clojure :exclude [if cat]))

(h/intern-in suite/emit
             suite/ls
             suite/man
             suite/echo
             suite/cat
             suite/pwd
             suite/nc
             suite/nc:port-check
             suite/apropos
             suite/if
             suite/>>
             suite/!
             suite/notify-form
             suite/notify

             basic/with:single-line)
