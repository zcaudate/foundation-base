(ns std.lang.dev
  (:require [std.lib.link :as l]))

(defn reload-specs
  "reloads the specs"
  {:added "4.0"}
  []
  (require 'std.lang.base.grammer-spec :reload)
  (require 'std.lang.base.grammer-xtalk :reload)
  (require 'std.lang.base.grammer :reload)
  (require 'std.lang.model.spec-xtalk.com-js :reload)
  (require 'std.lang.model.spec-xtalk.com-lua :reload)
  (require 'std.lang.model.spec-xtalk.com-python :reload)
  (require 'std.lang.model.spec-xtalk.com-r :reload)
  (require 'std.lang.model.spec-xtalk.fn-js :reload)
  (require 'std.lang.model.spec-xtalk.fn-lua :reload)
  (require 'std.lang.model.spec-xtalk.fn-python :reload)
  (require 'std.lang.model.spec-xtalk.fn-r :reload)
  (require 'std.lang.model.spec-js :reload)
  (require 'std.lang.model.spec-lua :reload)
  (require 'std.lang.model.spec-python :reload)
  (require 'std.lang.model.spec-r :reload)
  (require 'xt.lang.base-lib :reload))

(def +init+
  (l/link {:ns .} reload-specs))

(comment
  (./reload-specs)
  )
