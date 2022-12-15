(ns fx.gnuplot.command
  (:require [std.string :as str]
            [std.lib :as h]))

(def ^:dynamic *prefix* "set")

(def ^:dynamic *level* 0)

(declare command-submap command-vector)

(defn command-element
  "constructs a command string from element
 
   (command-element '(0 [0 \"0\"] 0))
   => \"0,0 0,0\"
 
   (command-element #{\"hello\"})
   => \"'hello'\""
  {:added "3.0"}
  ([v]
   (cond (list? v)   (str/join "," (map command-element v))
         (set? v)    (str "'" (first v) "'")
         (map? v)    (command-submap v)
         (vector? v) (command-vector v)
         (nil? v)    ""
         :else (h/strn v))))

(defn command-vector
  "constructs a command string from vector
 
   (command-vector [0 [0 \"0\"] 0])
   => \"0 0 0 0\""
  {:added "3.0"}
  ([arr]
   (str/join " " (map command-element arr))))

(defn command-submap
  "constructs a string from a map"
  {:added "3.0"}
  ([{:keys [input using] :as m}]
   (reduce-kv (fn [out k v]
                (let [result (command-element v)]
                  (str (cond-> out
                         (not (empty? out)) (str " "))
                       (h/strn k)
                       (cond->> result
                         (not (empty? result)) (str " ")))))
              (cond-> (command-element input)
                using (str "  using " using))
              (dissoc m :input :using))))

(defn command-map
  "constructs a array of commands using a map"
  {:added "3.0"}
  ([m]
   (command-map m []))
  ([m acc]
   (reduce-kv (fn [out k v]
                (let [result (cond (map? v)
                                   (if (zero? *level*)
                                     (binding [*prefix* (str *prefix* " " (h/strn k))
                                               *level*  (inc *level*)]
                                       (command-map v []))
                                     (str *prefix* " " (h/strn k) " " (command-submap v)))

                                   (or (false? v)
                                       (nil? v))
                                   (str "un" *prefix* " " (h/strn k))

                                   (true? v)
                                   (str *prefix* " " (h/strn k))

                                   :else
                                   (str *prefix* " " (h/strn k) " " (command-element v)))]
                  (cond (string? result)
                        (conj out result)

                        (vector? result)
                        (apply conj out result))))
              []
              m)))

(defn command-raw
  "returns a list of commands"
  {:added "3.0"}
  ([x]
   (cond (vector? x)
         (cond (coll? (first x))
               (mapv command-raw x)

               :else
               (command-vector x))

         (map? x)
         (command-map x)

         :else
         x)))

(defrecord GnuCommand [lines]
  Object
  (toString [obj]
    (str/join "\n" lines)))

(defmethod print-method GnuCommand
  ([v ^java.io.Writer w]
   (.write w (str "\n" v))))

(defn command
  "returns the entire command"
  {:added "3.0"}
  ([lines]
   (GnuCommand. (flatten (command-raw lines)))))

