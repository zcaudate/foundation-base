(ns rt.basic.impl.process-xtalk
  (:require [std.lang.base.runtime :as default]
            [rt.basic.type-common :as common]
            [rt.basic.type-oneshot :as oneshot]
            [std.lang.model.spec-xtalk :as spec]
            [std.lang.base.impl :as impl]
            [std.lang.base.runtime :as rt]
            [std.lib :as h]
            [std.string :as str]))

(def +program-init+
  (common/put-program-options
   :xtalk  {:default  {:oneshot     :chez
                       :interactive false
                       :ws-client   false}
            :env      {:chez    {:exec "chez"
                                 :pipe true
                                 :stderr true
                                 :raw true
                                 :flags  {:oneshot ["-q"]
                                          :interactive false
                                          :ws-client false}}}}))

(defn read-output
  "read output for scheme"
  {:added "4.0"}
  [{:keys [exit out err]}]
  (if (not-empty err)
    (h/error err)
    (let [out (-> out
                  (str/replace #"#t" "true")
                  (str/replace #"#f" "false"))]
      (try (read-string out)
           (catch Throwable t
             (h/wrapped out))))))

(defn transform-form
  "transforms output from shell"
  {:added "4.0"}
  [form {:keys [bulk] :as opts}]
  (if bulk
    `((~'lambda [] ~@form))
    form))

(def +xtalk-oneshot-config+
  (common/set-context-options
   [:xtalk :oneshot :default]
   {:default  {:emit  {:body  {:transform #'transform-form}
                       :json  #'read-output}}}))

(def +xtalk-oneshot+
  [(rt/install-type!
    :xtalk :oneshot
    {:type :hara/rt.oneshot
     :instance {:create oneshot/rt-oneshot:create}})])
  
