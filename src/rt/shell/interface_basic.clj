^{:no-test true}
(ns rt.shell.interface-basic
  (:require [std.protocol.component :as protocol.component]
            [std.protocol.context :as protocol.context]
            [std.concurrent :as cc]
            [std.string :as str]
            [std.lib :as h :refer [defimpl]]
            [std.lang.base.runtime :as default]
            [rt.basic.impl.process-bash :as process]))

(def ^:dynamic *single-line* false)

(defmacro with:single-line
  "allows reading of a single line (faster)"
  {:added "4.0"}
  [& body]
  `(binding [*single-line* true]
     ~@body))

(defn raw-eval-basic
  "basic evaluation for the bash runtime"
  {:added "4.0"}
  ([{:keys [relay] :as shell} body]
   (str/trim-right (:output @(cc/send relay
                                      (if (nil? body)
                                        {:op (if *single-line*
                                               :line
                                               :string)}
                                        body))))))

(defn invoke-ptr-basic
  "basic invoke for a pointer and arguments"
  {:added "4.0"}
  ([shell ptr args]
   (default/default-invoke-script
    shell
    ptr
    args
    raw-eval-basic
    {:emit  {:body  {:transform #'process/default-body-transform}}
     :json identity})))

(defn- shell-basic-string
  [{:keys [id lang]}]
  (str "#shell.basic" [id]))

(defimpl ShellBasic [relay error]
  :string shell-basic-string
  :protocols [protocol.component/IComponent
              :body   {-start  (do (h/comp:start relay)
                                   component)
                       -stop   (h/comp:stop relay)
                       -kill   (h/comp:kill relay)}
              protocol.context/IContext
              :prefix "default/default-"
              :method {-raw-eval    raw-eval-basic
                       -invoke-ptr  invoke-ptr-basic}])

(defn shell-basic:create
  "creates a basic shell"
  {:added "4.0"}
  ([]
   (shell-basic:create {}))
  ([{:keys [process] :as m}]
   (let [err   (atom {})
         relay (cc/relay:create (-> {:type :process
                                     :args ["bash"]
                                     :options {:in  {:quiet true}
                                               :err {:return :passive
                                                     :watch  err}}}
                                    (h/merge-nested process)))]
     (map->ShellBasic (merge (dissoc m :process)
                             {:relay relay :error err})))))


(defn shell-basic
  "creates and starts a basic shell"
  {:added "4.0"}
  ([]
   (shell-basic {}))
  ([m]
   (-> (shell-basic:create m)
       (h/start))))

(def +bash-basic+
  [(default/install-type!
    :bash :basic
    {:type :hara/rt.shell.basic
     :instance {:create #'shell-basic:create}
     :config {:layout :full}})])

(comment
  (./import)
  (shell-basic)
  (./create-tests)
  (def )(h/start (create-relay))
  (def -sh- (create-shell ["lua" "-i"]))
  
  (h/start (:shell -sh-))
  (h/stop (:shell -sh-))

  (cmd-exists? "lua")
  (cmd-exists? "lua -i"))
