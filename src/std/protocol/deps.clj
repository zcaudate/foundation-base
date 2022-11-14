(ns std.protocol.deps)

(defprotocol IDeps
  (-get-entry        [context id])
  (-get-deps         [context id])
  (-list-entries     [context]))

(defprotocol IDepsMutate
  (-add-entry        [context id entry deps])
  (-remove-entry     [context id])
  (-refresh-entry    [context id]))

(defprotocol IDepsCompile
  (-step-construct   [context acc id])
  (-init-construct   [context]))

(defprotocol IDepsTeardown
  (-step-deconstruct [context acc id]))

(defprotocol IDepsLibrary
  (-parse-native     [context input opts]))

(defprotocol IDepsProducer
  (-produce          [context opts]))

(defmulti -create
  "creates a context"
  {:added "3.0"}
  :path)




