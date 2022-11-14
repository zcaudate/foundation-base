(ns std.contract
  (:require [std.contract.sketch :as sketch]
            [std.contract.type :as type]
            [std.contract.binding :as binding]
            [std.lib :as h]
            [malli.core :as mc])
  (:refer-clojure :exclude [fn remove]))

(h/intern-in  [maybe sketch/as:maybe]
              [opt   sketch/as:optional]
              [fn    sketch/func]
              sketch/lax
              sketch/opened
              sketch/tighten
              sketch/closed
              sketch/norm
              sketch/remove
              [as:sketch sketch/from-schema]
              [as:schema sketch/to-schema]

              type/defcase
              type/defmultispec
              type/defspec
              type/spec?
              type/common-spec
              type/multi-spec
              type/valid?

              binding/defcontract

              mc/schema
              mc/schema?)

