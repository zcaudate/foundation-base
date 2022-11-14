(ns lib.redis.script
  (:require [lib.redis.impl.common :as common]
            [lib.redis.impl.generator :as gen]
            [lib.redis.impl.template :as t]
            [std.lib :as h]))

(def +scripting+
  (gen/select-commands {:group [:scripting]}))

(h/template-entries [gen/command-tmpl]
  +scripting+)

(h/template-vars [t/redis-template]
  (script:load        in:script-load  {})
  (script:flush       in:script-flush {})
  (script:exists      in:script-exists {})
  (script:kill        in:script-kill {})
  (script:eval        in:eval {:allow-empty true :return :any})
  (script:evalsha     in:evalsha {:allow-empty true :return :any}))
