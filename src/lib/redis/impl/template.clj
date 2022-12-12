(ns lib.redis.impl.template
  (:require [std.concurrent :as cc]
            [std.lib :as h]))

(defn redis-pipeline
  "constructs a pipeline for `opts`"
  {:added "3.0"}
  ([return m]
   (redis-pipeline return m nil))
  ([return {:keys [post pre chain raw string namespace]} gopts]
   (let [gopts (or 'opts gopts)
         post (if (and (symbol? return)
                       (not raw))
                (vec (cons `(fn [data#] (~return data# ~gopts)) post))
                post)]
     (cond-> nil
       pre       (assoc :pre pre)
       post      (assoc :post post)
       chain     (assoc :chain chain)
       namespace (assoc :namespace namespace)
       (some? string) (assoc :string string)
       (= return :data) (assoc :deserialize true)
       (or (symbol? return)
           raw) (assoc :deserialize false)))))

(defn redis-template
  "creates a redis form from data"
  {:added "3.0"}
  ([fsym var]
   (redis-template fsym var {}))
  ([fsym var {:keys [post pre chain raw string default namespace inputs debug
                     allow-empty] :as custom}]
   (let [{:keys [arglists]
          :redis/keys [return multiple]} (meta var)
         return   (or (:return custom)
                      return)
         multiple (or (:multiple custom)
                      multiple)
         [fargs dargs] arglists
         ilen  (count inputs)
         fargs (or (-> custom :args :symbols)
                   (vec (drop ilen fargs)))
         dargs (or (-> custom :args :display)
                   (vec (drop ilen dargs)))
         iargs (reduce-kv (fn [iargs i prepend]
                            (update iargs i #(concat (h/seqify prepend) [%])))
                          fargs
                          (-> custom :args :format))
         redis 'redis
         opts 'opts
         gredis (gensym redis)
         gopts (gensym opts)
         pipeline (redis-pipeline return custom gopts)
         form  `(cc/req ~gredis ~gcmd
                        ~(if pipeline
                           `(cc/req:opts ~gopts
                                         ~pipeline)
                           gopts))
         opts-pipeline (keep (fn [[k val]]
                               (if (some? val) `(assoc ~k ~val)))
                             (select-keys pipeline [:namespace :format]))
         qfsym (symbol (-> *ns* ns-name name) (name fsym))
         gcmd (gensym 'cmd)]
     `(defn ~fsym
        {:arglists [[~redis ~@fargs]
                    [~redis ~@(butlast dargs) ~opts]]}
        ([~gredis ~@fargs]
         (~qfsym ~gredis ~@fargs {}))
        ([~gredis ~@(butlast dargs) ~gopts]
         (let [~@(if (not-empty opts-pipeline)
                   [gopts `(-> ~gopts ~@opts-pipeline)])
               ~gcmd  (~(h/var-sym var) ~@inputs ~@iargs ~gopts)]
           ~@(if debug
               [`(h/prn :REDIS
                        '~fsym
                        '~pipeline
                        ~return
                        (cc/req:opts ~gopts
                                     ~pipeline)
                        ~gopts
                        ~gcmd)])
           ~(if (and multiple
                     (not allow-empty))
              (let [varg (if (integer? multiple)
                           (get fargs multiple)
                           (last fargs))]
                `(if (empty? ~varg)
                   (common/return-default ~default ~'opts)
                   ~form))
              form)))))))
