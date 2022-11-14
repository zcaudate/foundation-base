(ns std.protocol.dispatch)

(defprotocol IDispatch
  (-submit     [dispatch entry])
  (-bulk?      [dispatch]))

(defmulti -create
  "creates an executor"
  {:added "3.0"}
  :type)
