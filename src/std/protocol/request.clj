(ns std.protocol.request)

(defprotocol IRequest
  (-request-single  [client command opts])
  (-process-single  [client output opts])
  (-request-bulk    [client commands opts])
  (-process-bulk    [client inputs outputs opts]))

(defprotocol IRequestTransact
  (-transact-start    [client])
  (-transact-end      [client])
  (-transact-combine  [client commands]))
