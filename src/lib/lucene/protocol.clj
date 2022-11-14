(ns lib.lucene.protocol)

(defprotocol IEngine
  (-index-add    [engine index entry opts])
  (-index-remove [engine index terms opts])
  (-index-update [engine index terms entry opts])
  (-search [engine index terms opts]))

(defmulti -create
  "creates a lucene object"
  {:added "4.0"}
  :type)

(comment
  (./reeval))
