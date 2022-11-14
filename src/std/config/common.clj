(ns std.config.common)

(defmulti -resolve-directive
  "multimethod for resolving directives"
  {:added "3.0"}
  first)

(defmulti -resolve-type
  "utility method for resolve"
  {:added "3.0"}
  (fn [type content] type))
