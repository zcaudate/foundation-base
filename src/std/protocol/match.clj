(ns std.protocol.match)

(defprotocol ITemplate
  (-match [template obj]))
