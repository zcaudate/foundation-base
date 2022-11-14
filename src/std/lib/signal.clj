(ns std.lib.signal
  (:require [std.lib.collection :as c]
            [std.lib.foundation :as h])
  (:import (clojure.lang Namespace Symbol)))

(defn new-id
  "creates a random id with a keyword base
   (new-id)
   ;;=> :06679506-1f87-4be8-8cfb-c48f8579bc00"
  {:added "3.0"}
  ([]
   (keyword (str (h/uuid)))))

(defn expand-data
  "expands shorthand data into a map
 
   (expand-data :hello)
   => {:hello true}
 
   (expand-data [:hello {:world \"foo\"}])
   => {:world \"foo\", :hello true}"
  {:added "3.0"}
  ([data]
   (cond (c/hash-map? data) data
         (keyword? data) {data true}
         (vector? data)  (apply merge (map expand-data data))
         :else (h/error "Input should be a keyword, hash-map or vector."
                        {:input data}))))

(defn check-data
  "checks to see if the data corresponds to a template
 
   (check-data {:hello true} :hello)
   => true
 
   (check-data {:hello true} {:hello true?})
   => true
 
   (check-data {:hello true} '_)
   => true
 
   (check-data {:hello true} #{:hello})
   => true"
  {:added "3.0"}
  ([data chk]
   (cond (c/hash-map? chk)
         (every? (fn [[k vchk]]
                   (let [vcnt (get data k)]
                     (cond (keyword? vchk) (= vchk vcnt)
                           (fn? vchk) (vchk vcnt)
                           :else (= vchk vcnt))))
                 chk)

         (vector? chk)
         (every? #(check-data data %) chk)

         (or (fn? chk) (keyword? chk))
         (chk data)

         (set? chk)
         (some #(check-data data %) chk)

         (= '_ chk) true

         :else
         (throw (ex-info "Not a valid checker" {:checker chk})))))

(defrecord Manager [id store options])

(defn manager
  "creates a new manager
   (manager)
   ;; => #std.lib.signal.Manager{:id :b56eb2c9-8d21-4680-b3e1-0023ae685d2b,
   ;;                               :store [], :options {}}"
  {:added "3.0"}
  ([] (Manager. (new-id) [] {}))
  ([id store options] (Manager. id store options)))

(defn remove-handler
  "adds a handler to the manager
   (-> (add-handler (manager) :hello {:id :hello
                                      :handler identity})
       (remove-handler :hello)
       (match-handlers {:hello \"world\"}))
   => ()"
  {:added "3.0"}
  ([manager id]
   (update manager :store (fn [arr]
                            (vec (remove #(-> % :id (= id)) arr))))))

(defn add-handler
  "adds a handler to the manager
   (-> (add-handler (manager) :hello {:id :hello
                                      :handler identity})
       (match-handlers {:hello \"world\"})
       (count))
   => 1"
  {:added "3.0"}
  ([manager handler]
   (let [handler (if (:id handler)
                   handler
                   (assoc handler :id (new-id)))]
     (-> manager
         (remove-handler (:id handler))
         (update-in [:store] conj handler))))
  ([manager checker handler]
   (let [handler (cond (fn? handler)
                       {:checker checker
                        :fn handler}

                       (map? handler)
                       (assoc handler :checker checker))]
     (add-handler manager handler))))

(defn list-handlers
  "list handlers that are present for a given manager
 
   (list-handlers (manager))
   => []"
  {:added "3.0"}
  ([manager]
   (:store manager))
  ([manager checker]
   (->> (list-handlers manager)
        (filter #(check-data (:checker %) checker)))))

(defn match-handlers
  "match handlers for a given manager
 
   (-> (add-handler (manager) :hello {:id :hello
                                      :handler identity})
       (match-handlers {:hello \"world\"}))
   => (contains-in [{:id :hello
                     :handler fn?
                     :checker :hello}])"
  {:added "3.0"}
  ([manager data]
   (filter #(check-data data (:checker %))
           (:store manager))))

(def ^:dynamic *manager* (atom (manager)))

(defn signal:clear
  "clears all signal handlers"
  {:added "3.0"}
  ([]
   (reset! *manager* (manager))))

(defn signal:list
  "lists all signal handlers"
  {:added "3.0"}
  ([]
   (list-handlers @*manager*))
  ([checker]
   (list-handlers @*manager* checker)))

(defn signal:install
  "installs a signal handler"
  {:added "3.0"}
  ([id checker handler]
   (swap! *manager*
          add-handler checker {:id id
                               :fn handler})))

(defn signal:uninstall
  "uninstalls a signal handler"
  {:added "3.0"}
  ([id]
   (do (swap! *manager* remove-handler id)
       (if-let [nsp (and (symbol? id)
                         (.getNamespace ^Symbol id)
                         (Namespace/find (symbol (.getNamespace ^Symbol id))))]
         (do (.unmap ^Namespace nsp (symbol (.getName ^Symbol id)))
             nsp)
         id))))

(defn signal
  "signals an event"
  {:added "3.0"}
  ([data]
   (signal data @*manager*))
  ([data manager]
   (let [ndata   (expand-data data)]
     (doall (for [handler (match-handlers manager ndata)]
              {:id (:id handler)
               :result ((:fn handler) ndata)})))))

(defmacro signal:with-temp
  "uses a temporary signal manager for testing"
  {:added "3.0" :style/indent 1}
  ([[checker handler] & body]
   `(binding [*manager* (atom (manager))]
      (signal:install :temp ~checker ~handler)
      ~@body)))
