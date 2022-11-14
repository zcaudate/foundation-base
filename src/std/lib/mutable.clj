(ns std.lib.mutable)

(defprotocol IMutable
  (-set-new [this k v])
  (-set [this k v])
  (-clone [this])
  (-fields [this]))

(defmacro defmutable
  "allows definition of a mutable datastructure
 
   (def -h- (Hello. \"dog\" 1))
   
   (get -h- :name) => \"dog\""
  {:added "3.0"}
  ([tp-name fields & protos] 
   {:pre [(symbol? tp-name)
         (every? symbol? fields)]} (let [fields (mapv (fn [sym]
                       (with-meta sym
                                  (assoc (meta sym) :volatile-mutable true)))
                     fields)]
    
    `(deftype ~tp-name ~fields
       IMutable
       (-set [~'this ~'k ~'v]
         (case ~'k
           ~@(mapcat
              (fn [x]
                `[~(keyword (name x))
                  (~'set! ~x ~'v)])
              fields))
         ~'this)
       
       (-set-new [~'this ~'k ~'v]
         (assert (not (~'k ~'this)) (str ~'k " is already set."))
         (case ~'k
           ~@(mapcat
              (fn [x]
                `[~(keyword (name x))
                  (~'set! ~x ~'v)])
              fields))
         ~'this)

       (-fields [~'this]
         ~(mapv (comp keyword name) fields))

       (-clone [~'this]
         ~(let [cstr (symbol (str tp-name "."))]
            `(~cstr ~@fields)))
       
       clojure.lang.ILookup
       (~'valAt [~'this ~'k ~'default]
         (case ~'k
           ~@(mapcat
               (fn [x]
                 `[~(keyword (name x))
                   ~x])
               fields)
           ~'default))
       (~'valAt [~'this ~'k]
         (.valAt ~'this ~'k nil))
       ~@protos))))

(defn mutable:fields
  "returns all the fields of
 
   (mutable:fields (Hello. \"dog\" 1))
   => [:name :value]"
  {:added "3.0"}
  ([obj]
   (-fields obj)))

(defn mutable:set
  "sets the value of a given field
 
   (-> (mutable:set -h- :name \"cat\")
       :name)
   => \"cat\""
  {:added "3.0"}
  ([obj m]
   (reduce-kv (fn [out k v]
                (-set out k v))
              obj
              m))
  ([obj k v]
   (-set obj k v)))

(defn  mutable:set-new
  "sets the value of a given field only when it is nil
 
   (-> (Hello. \"cat\" nil)
       (mutable:set-new :name \"dog\"))
   => (throws)"
  {:added "3.0"}
  ([obj m]
   (reduce-kv (fn [out k v]
                (-set-new out k v))
              obj
              m))
  ([obj k v]
   (-set-new obj k v)))

(defn mutable:update
  "applies a function to a given field
 
   (-> (Hello. \"dog\" 123)
       (mutable:update :value inc)
       :value)
   => 124"
  {:added "3.0"}
  ([obj k f & args] 
   (let [v   (get obj k)
        res (apply f v args)]
    (mutable:set obj k res))))

(defn mutable:clone
  "creates a copy of the object
 
   (-> (Hello. \"dog\" 123)
       (mutable:clone)
       ((juxt :name :value)))
   => [\"dog\" 123]"
  {:added "3.0"}
  ([obj] 
   (-clone obj)))

(defn mutable:copy
  "copies values from a given source
 
   (-> (Hello. \"dog\" 123)
       (mutable:copy (Hello. \"cat\" 456))
       ((juxt :name :value)))
   => [\"cat\" 456]"
  {:added "3.0"}
  ([obj source]
   (mutable:copy obj source (mutable:fields source)))
  ([obj source ks]
   (reduce (fn [out k]
             (mutable:set obj k (get source k)))
           obj
           ks)))
