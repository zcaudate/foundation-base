(ns rt.libpython
  (:require [std.protocol.component :as protocol.component]
            [std.protocol.context :as protocol.context]
            [rt.basic.impl.process-js :as js]
            [rt.basic.impl.process-python :as python]
            [std.lang.base.impl :as impl]
            [std.lang.base.runtime :as default]
            [std.lang.base.pointer :as ptr]
            [libpython-clj2.python :as lp]))

(defn eval-raw
  "performs an exec expression
 
   (str (eval-raw +js+ \"1 + 1\"))
   => \"2\""
  {:added "3.0"}
  ([ctx string]
   (let [lang (raw-lang ctx)]
     (.eval ^Context ctx (name lang) string))))

(defn eval-libpython
  "evals body in the runtime
 
   (str (eval-libpython (l/rt :js)
                    \"1+1\"))
   => \"2\""
  {:added "4.0"}
  ([{:keys [raw]} string]
   (eval-raw @raw string)))

(def +options+
  (delay
    {:js      {:bootstrap (impl/emit-entry-deps
                           k/return-eval
                           {:lang :js
                            :layout :flat})
               :main  {:out   str}
               :emit  {:body  {:transform #'default/return-transform}}}
     :python  {:bootstrap  (impl/emit-entry-deps
                            k/return-eval
                            {:lang :python
                             :layout :flat})
               :main  {:out   (comp json/read str)}
               :emit  {:body  {:transform (fn [forms mopts]
                                            (default/return-transform
                                             forms mopts {:format-fn identity
                                                          :wrap-fn (fn [forms]
                                                                     (apply list 'do forms))}))}}}}))

(defn invoke-libpython
  "invokes a pointer in the runtime
 
   (invoke-libpython (l/rt :js)
                 k/sub
                 [1 2])
   => -1"
  {:added "4.0"}
  ([{:keys [state lang layout] :as rt} ptr args]
   (let [{:keys [main emit]} (get @+options+ lang)]
     (default/default-invoke-script
      rt ptr args eval-libpython
      {:main main
       :emit emit
       :json :full}))))

(defn start-libpython
  "starts the graal runtime"
  {:added "3.0"}
  ([{:keys [lang resource system raw] :as graal}]
   (if (nil? @raw)
     (reset! raw (make-raw {:lang lang
                            :resource resource
                            :system system})))
   (eval-libpython graal (get-in @+options+ [lang :bootstrap]))
   graal))

(defn stop-libpython
  "stops the graal runtime"
  {:added "3.0"}
  ([{:keys [lang raw] :as graal}]
   (if-not (nil? @raw)
     (h/swap-return! raw
                     (fn [ctx]
                       (close-raw ctx)
                       [ctx nil])))
   graal))

(defn- rt-libpython-string
  ([graal]
   (str "#rt.graal" (into {} graal))))

(defimpl RuntimeGraal [lang raw]
  :string rt-libpython-string
  :protocols [std.protocol.component/IComponent
              :exclude [-kill]
              :suffix "-libpython"
              protocol.context/IContext
              :prefix "default/default-"
              :method {-raw-eval eval-libpython
                       -invoke-ptr invoke-libpython}])

(defn rt-libpython:create
  "creates a graal runtime
 
   (h/-> (rt-libpython:create {:lang :js})
         (h/start)
         (h/stop))
   => rt-libpython?"
  {:added "4.0"}
  [{:keys [id]
    :or {id (h/sid)}
    :as m}]
  (map->RuntimeGraal (assoc m
                            :id id
                            :raw (atom nil))))

(defn rt-libpython
  "creates and starts a graal runtime"
  {:added "3.0"}
  ([m]
   (-> (rt-libpython:create m)
       (h/start))))

(defn rt-libpython?
  "checks that object is a graal runtime"
  {:added "3.0"}
  [obj]
  (instance? RuntimeGraal obj))

;;
;; Hooks for lang
;;

(def +init+
  [(default/install-type!
    :python :graal
    {:type :hara/rt.graal
     :config {:lang :python :layout :full}
     :instance {:create rt-libpython:create}})
   (default/install-type!
    :js :graal
    {:type :hara/rt.graal
     :config {:lang :js :layout :full}
     :instance {:create rt-libpython:create}})])
