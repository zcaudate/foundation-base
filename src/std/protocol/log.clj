(ns std.protocol.log)

(defmulti -create
  "creates a logger"
  {:added "3.0"}
  :type)

(defprotocol ILogger
  (-logger-write [logger entry]))

(defprotocol ILoggerProcess
  (-logger-process [logger entries]))
