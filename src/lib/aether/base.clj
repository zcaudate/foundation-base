(ns lib.aether.base
  (:require [lib.aether.listener :as listener]
            [lib.aether.session :as session]
            [lib.aether.system :as system]))

(defonce +defaults+
  {:listeners {:transfer listener/blank-transfer-listener
               :repository listener/blank-repository-listener}
   :repositories [{:id "clojars"
                   :type "default"
                   :url "https://clojars.org/repo"}
                  {:id "central"
                   :type "default"
                   :url "https://repo1.maven.org/maven2/"}]})

(defrecord Aether [])

(defmethod print-method Aether
  ([v ^java.io.Writer w]
   (.write w (str "#aether" (into {} v)))))

(defn aether
  "creates an `Aether` object
 
   (aether)
   => (contains-in
       {:repositories (contains [{:id \"clojars\",
                                  :type \"default\",
                                  :url \"https://clojars.org/repo\"}
                                 {:id \"central\",
                                  :type \"default\",
                                  :url \"https://repo1.maven.org/maven2/\"}]
                               :in-any-order
                                :gaps-ok),
        :system org.eclipse.aether.RepositorySystem
        :session org.eclipse.aether.RepositorySystemSession})"
  {:added "3.0"}
  ([] (aether {}))
  ([config]
   (let [system  (system/repository-system)
         session (->> (select-keys config [:local-repo])
                      (session/session system))]
     (-> +defaults+
         (merge {:system system
                 :session session}
                (select-keys config [:repositories]))
         (map->Aether)))))
