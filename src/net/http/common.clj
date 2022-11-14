(ns net.http.common)

(def +default-config+
  {:protocol "http"
   :host     "localhost"
   :port     8000
   :format   :edn})

(defmulti -write-value
  "writes the string value of the datastructure according to format
 
   (-write-value {:a 1} :edn)
   => \"{:a 1}\""
  {:added "0.5"}
  (fn [s format] format))

(defmethod -write-value :edn
  ([s _]
   (pr-str s)))

(defmulti -read-value
  "read the string value of the datastructure according to format
 
   (-read-value \"{:a 1}\" :edn)
   => {:a 1}"
  {:added "0.5"}
  (fn [s format] format))

(defmethod -read-value :edn
  ([s _]
   (read-string s)))

(defmethod -read-value :default
  ([s _]
   (read-string s)))

(defmulti -read-body
  "reads the body of the request can be expanded
 
   (-read-body \"{:a 1}\" :edn)
   => {:a 1}"
  {:added "0.5"}
  (fn [body format] (type body)))

(defmethod -read-body nil
  ([body _]
   {}))

(defmethod -read-body java.io.InputStream
  ([body format]
   (-read-value (slurp body) format)))

(defmethod -read-body String
  ([body format]
   (-read-value body format)))

(defmulti -create-server
  "multimethod entrypoint for server construction"
  {:added "0.5"}
  :type)
