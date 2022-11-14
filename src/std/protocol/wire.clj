(ns std.protocol.wire)

(defprotocol IWire
  (-read      [remote])
  (-write     [remote command])
  (-close     [remote]))

(defmulti -as-input
  "convert object to input"
  {:added "3.0"}
  (fn [val type] type))

(defmulti -serialize-bytes
  "convert object to bytes"
  {:added "3.0"}
  (fn [val type] type))

(defmulti -deserialize-bytes
  "convert bytes to object"
  {:added "3.0"}
  (fn [bytes type] type))
