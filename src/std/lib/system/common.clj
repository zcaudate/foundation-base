(ns std.lib.system.common
  (:require [std.lib.foundation :as h]))

(defprotocol ISystem)

(defn system?
  "checks if a component extends ISystem"
  {:added "3.0"}
  ([obj]
   (satisfies? ISystem obj)))

(defn primitive?
  "checks if a component is a primitive type"
  {:added "3.0"}
  ([x]
   (or (string? x)
       (number? x)
       (boolean? x)
       (h/regexp? x)
       (uuid? x)
       (uri? x)
       (h/url? x))))
