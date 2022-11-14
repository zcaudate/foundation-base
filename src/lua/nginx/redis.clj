(ns lua.nginx.redis
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]
            [rt.redis :as redis]))

(l/script :lua
  {:bundle {:default [["resty.redis" :as ngxredis]]}
   :import  [["resty.redis" :as ngxredis]]
   :require [[lua.nginx :as n :include [:sha1 :string]]
             [xt.lang.base-lib :as k]]
   :export [MODULE]})

(h/template-entries [l/tmpl-macro {:base "redis"
                                   :inst "rds"
                                   :tag "lua"}]
  [[connect          [host port] {:optional [ops]}]
   [set_timeout      [time]]
   [set_timeouts     [connect send read]]
   [set_keepalive    [max-timeout pool-size]]
   [get_reused_times []]
   [close            []]
   [init_pipeline    [] {:optional [n]}]
   [commit_pipeline  []]
   [cancel_pipeline  []]
   [read_reply       []]
   [add_commands     [cmd] {:vargs cmds}]
   [subscribe        [key]]
   [psubscribe       [key]]])

(defn.lua new-connection
  "creates a new connetion"
  {:added "4.0"}
  [m]
  (local conn (. ngxredis (new)))
  (local #{host port} m)
  (local '[ok err] (-/connect conn
                             (or host "127.0.0.1")
                             (or port "6379")))
  (when err (error err))
  (return conn))

(defn.lua new-subscription
  "creates a new subscription"
  {:added "4.0"}
  [m channels]
  (local conn (-/new-connection m))
  (local '[ok err] (-/subscribe conn (unpack channels)))
  
  (when err (error err))
  (return conn))

(defn.lua new-psubscription
  "creates a new patten subscription"
  {:added "4.0"}
  [m channels]
  (local conn (-/new-connection m))
  (local '[ok err] (-/psubscribe conn (unpack channels)))
  
  (when err (error err))
  (return conn))

(defn.lua eval-script
  "evaluate script from sha/script"
  {:added "4.0"}
  [conn script ...]
  (local #{sha body} script)
  (local '[res err] (. conn (evalsha sha 0 ...)))
  (when err
    (when (k/starts-with? err "NOSCRIPT")
      (if (k/fn? body)
        (:= body (body)))
      (local '[tres terr] (. conn (eval body 0 ...)))
      (if terr
        (error terr)
        (return tres)))
    (do (n/log n/ERR
               (cjson.encode
                {:message err
                 :args [...]}))
        (error err)))
  (return res))

(defn script-tmpl
  "creates the script function templates"
  {:added "4.0"}
  ([[sym redis-sym]]
   (let [{:rt/keys [redis]
          :keys [form] :as entry}  @@(resolve redis-sym)
         {enc-in :in enc-out :out} (:encode redis)
         enc-in (set enc-in)
         args   (nth form 2)
         rargs  (map-indexed (fn [i arg]
                               (if (enc-in i)
                                 (list 'cjson.encode arg)
                                 arg))
                             args)
         ret   (cond->> (h/$ (lua.nginx.redis/eval-script
                              conn
                              (tab :sha sha :body body)
                              ~@rargs))
                 enc-out (list 'xt.lang.base-lib/js-decode))
         body  (redis/generate-script entry)
         sha   (h/sha1 body)
         lines (vec (str/split-lines (redis/generate-script entry)))]
     (with-meta
       (h/$ (defn.lua ~(symbol (name sym)) ~(vec (cons 'conn args))
              (local sha ~sha)
              (local body (fn [] (return (xt.lang.base-lib/join "\n" ~lines))))
              (return ~ret)))
       (h/template-meta)))))

(def.lua MODULE (!:module))

(comment
  (./create-tests)
  (./create-tests)
  )
