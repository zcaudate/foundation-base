(ns std.protocol.return)

(defprotocol IReturn
  (-get-value      [obj])
  (-get-error      [obj])
  (-has-error?     [obj])
  (-get-status     [obj])
  (-get-metadata   [obj])
  (-is-container?  [obj]))
