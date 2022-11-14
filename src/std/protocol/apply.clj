(ns std.protocol.apply)

(defprotocol IApplicable
  (-apply-in      [app rt args])
  (-apply-default [app])
  (-transform-in  [app rt args])
  (-transform-out [app rt args return]))
