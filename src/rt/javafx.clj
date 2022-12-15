(ns rt.javafx
  (:require [std.protocol.context :as protocol.context]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.impl :as impl]
            [std.lang.base.runtime :as default]
            [std.lib :as h :refer [defimpl]]
            [std.json :as json]
            [fx.gui :as fx]
            [rt.javafx.common :as common])
  (:import (netscape.javascript JSObject)))

(defn start-javafx
  "starts the javafx"
  {:added "4.0"}
  ([{:keys [id state] :as rt}]
   (if (not @state)
     (let [inst (common/instance-start (-> (dissoc rt :state)
                                           (assoc :callback {:stop (fn [] (reset! state nil))})))
           _    (reset! state inst)]))
   rt))

(defn stop-javafx
  "stops the javafx"
  {:added "4.0"}
  ([{:keys [id state] :as rt}]
   (when @state
     (common/instance-stop @state)
     (reset! state nil))
   rt))

(defn- rt-javafx-string
  ([rt]
   (str "#rt.javafx" (into {} rt))))

(defn raw-eval-javafx
  "evaluates an expression in the runtime"
  {:added "4.0"}
  ([{:keys [state] :as rt} body]
   (let [webview (or @state (h/error "Javafx not started."))]
     (common/instance-exec webview body))))

(defn invoke-ptr-javafx
  "evaluates an expression in the runtime"
  {:added "4.0"}
  ([{:keys [state lang] :as rt} ptr args]
   (default/default-invoke-script
    rt ptr args raw-eval-javafx
    {:main {:in (fn [body]
                  (impl/emit-as
                   :js [(list 'return-eval body)]))}
     :emit {:native {:suppress true}
            :body {:transform default/return-transform}
            :lang/jsx false}
     :json :full})))

(defimpl RuntimeJavaFx [id state]
  :string rt-javafx-string
  :protocols [std.protocol.component/IComponent
              :suffix "-javafx"
              :method {-kill stop-javafx}
              protocol.context/IContext
              :prefix "default/default-"
              :method {-raw-eval   raw-eval-javafx
                       -invoke-ptr invoke-ptr-javafx}])
  
(defn rt-javafx:create
  "creates the javafx"
  {:added "4.0"}
  [{:keys [id options] :as m
    :or {id  (h/sid)}}]
  (map->RuntimeJavaFx
   (h/merge-nested {:id id
                    :runtime :javafx
                    :title (str (h/ns-sym))
                    :display false
                    :state (atom nil)}
                   m)))

(defn rt-javafx
  "creates and starts the javafx"
  {:added "4.0"}
  ([]
   (rt-javafx {}))
  ([m]
   (-> (rt-javafx:create m)
       (h/start))))

(def +rt-javafx-init+
  [(default/install-type!
    :js :javafx
    {:type :hara/rt.javafx
     :config {:layout :full
              :emit {:jsx false}}
     :instance {:create rt-javafx:create}})])

(comment
  
  
  (def +init+
    (do (script/lang-rt-add
         )
        
        (script/lang-rt-add
         :js :javafx.shared
         {:type :hara/rt.javafx.shared
          :config   {:bootstrap false :layout :full}
          :instance
          {:create (fn [m]
                     (-> {:rt/client {:type :hara/rt.javafx.shared
                                      :constructor javafx:create}}
                         (merge m)
                         (shared/rt-shared:create)))}}))))
