(ns fx.gui.data.state)

(defonce ^:dynamic *viewers*
  (atom {}))

(defonce ^:dynamic *animations*
  (atom {}))

(defmulti output-fn
  "returns a function for processing various outputs"
  {:added "3.0"}
  identity)

(defmethod output-fn :default
  ([_]
   (fn [^bytes bytes display size])))