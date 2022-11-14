(ns std.protocol.binary)

(defprotocol IBinary
  (-to-bitstr [x])
  (-to-bitseq [x])
  (-to-bitset [x])
  (-to-bytes  [x])
  (-to-number [x]))

(defprotocol IByteSource
  (-to-input-stream [obj]))

(defprotocol IByteSink
  (-to-output-stream [obj]))

(defprotocol IByteChannel
  (-to-channel [obj]))
