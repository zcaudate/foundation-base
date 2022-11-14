(ns xt.sys.conn-redis
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]
            [rt.redis]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export [MODULE]})

(defn script-tmpl
  "creates a script template"
  {:added "4.0"}
  ([[sym redis-sym]]
   (let [{:rt/keys [redis]
          :keys [form] :as entry}  @@(resolve redis-sym)
         {enc-in :in enc-out :out} (:encode redis)
         enc-in (set enc-in)
         args   (nth form 2)
         rargs  (map-indexed (fn [i arg]
                               (if (enc-in i)
                                 (list 'xt.lang.base-lib/js-encode arg)
                                 arg))
                             args)
         ret   (h/$ (xt.sys.conn-redis/eval-script
                     conn
                     (tab :sha sha :body body)
                     [~@rargs]
                     {:success (fn:> [ret]
                                     ~(cond->> 'ret
                                        enc-out (list 'xt.lang.base-lib/js-decode)))}))
         body  ((resolve 'rt.redis/generate-script) entry)
         sha   (h/sha1 body)
         lines (vec (str/split-lines ((resolve 'rt.redis/generate-script) entry)))
         {:keys [tag] :or {tag "xt"}} (h/template-meta)]
     (with-meta
       (h/$ (~(symbol (str "defn." tag)) ~(symbol (name sym))
              ~(vec (concat ['conn] args))
              (var sha ~sha)
              (var body (fn [] (return (xt.lang.base-lib/join "\n" ~lines))))
              (return ~ret)))
       (h/template-meta)))))

(defn.xt connect
  "connects to a datasource"
  {:added "4.0"}
  [m cb]
  (var #{constructor} m)
  (var success-fn (k/wrap-callback cb "success"))
  (var error-fn   (k/wrap-callback cb "error"))
  (k/for:return [[conn err] (constructor m (x:callback))]
    {:success (return (success-fn conn))
     :error   (return (error-fn err))
     :final   true}))

(defn.xt disconnect
  "disconnect redis"
  {:added "4.0"}
  [conn cb]
  (var disconnect-fn (k/get-key conn "::disconnect"))
  (var success-fn (k/wrap-callback cb "success"))
  (var error-fn   (k/wrap-callback cb "error"))
  (k/for:return [[res err] (disconnect-fn (x:callback))]
    {:success (return (success-fn res))
     :error   (return (error-fn err))
     :final   true}))

(defn.xt exec
  "executes a redis command"
  {:added "4.0"}
  [conn command args cb]
  (var exec-fn (k/get-key conn "::exec"))
  (var success-fn (k/wrap-callback cb "success"))
  (var error-fn   (k/wrap-callback cb "error"))
  (k/for:return [[conn err] (exec-fn command args (x:callback))]
    {:success (return (success-fn conn))
     :error   (return (error-fn err))
     :final   true}))

(defn.xt create-subscription
  "creates a subscription given channel"
  {:added "4.0"}
  [conn channels cb]
  (return (-/exec conn "subscribe" channels cb)))

(defn.xt create-psubscription
  "creates a pattern subscription given channel"
  {:added "4.0"}
  [conn channels cb]
  (return (-/exec conn "psubscribe" channels cb)))

(defn.xt eval-body
  "evaluates a the body"
  {:added "4.0"}
  [conn script args cb]
  (var #{sha body} script)
  (return (-/exec conn "eval" [body "0" (k/unpack args)]
                  cb)))

(defn.xt eval-script
  "evaluates sha, then body if errored"
  {:added "4.0"}
  [conn script args cb]
  (var #{sha body} script)
  (var err-fn
       (fn [err]
         (:= body (:? (k/fn? body) (body) body))
         (return (-/exec conn "eval" [body "0" (k/unpack args)]
                         cb))))
  (return (-/exec conn "evalsha" [sha "0" (k/unpack args)]
                  {:success (k/wrap-callback cb "success")
                   :error   err-fn})))

(def.xt MODULE (!:module))

(comment
  (./create-tests)
  (./create-tests)
  )
