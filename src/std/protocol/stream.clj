(ns std.protocol.stream)

(defprotocol ISink
  (-collect [sink xf supply]))

(defprotocol ISource
  (-produce [source]))

(defprotocol IBlocking
  (-take-element [source]))

(defprotocol IStage
  (-stage-unit      [stage])
  (-stage-realized? [stage])
  (-stage-realize   [stage]))
