(ns jvm.protocol)

(defprotocol ILoader
  (-has-url?    [obj path])
  (-get-url     [obj path])
  (-all-urls    [obj])
  (-add-url     [obj path])
  (-remove-url  [obj path]))

(defmulti -load-class
  "loads a class from various sources"
  {:added "4.0"}
  (fn [x loader opts] [(type x) (type loader)]))

(defmulti -rep
  "multimethod definition for coercing to a rep"
  {:added "4.0"}
  type)

(defmulti -artifact
  "multimethod definition for coercing to an artifact type"
  {:added "4.0"}
  (fn [tag x] tag))
