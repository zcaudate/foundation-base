(ns std.lang.base.impl-deps
  (:require [std.lang.base.util :as ut]
            [std.lang.base.emit :as emit]
            [std.lang.base.emit-preprocess :as preprocess]
            [std.lang.base.impl-entry :as entry]
            [std.lang.base.book :as b]
            [std.string :as str]
            [std.lib :as h]))

;;
;;
;;

(defn module-import-form
  "import form"
  {:added "4.0"}
  [{:keys [meta] :as book} name module opts]
  (if-let [f (:module-import meta)] (f name module opts)))

(defn module-export-form
  "export form"
  {:added "4.0"}
  [{:keys [meta] :as book} export opts]
  (if-let [f (:module-export meta)] (f export opts)))

(defn module-link-form
  "link form for projects"
  {:added "4.0"}
  [{:keys [meta] :as book} ns graph]
  (if-let [f (:module-link meta)] (f ns graph)))

(defn has-module-form
  "checks if module is available"
  {:added "4.0"}
  [{:keys [meta] :as book} module]
  (if-let [f (:has-module meta)] (f module)))

(defn setup-module-form
  "setup the module"
  {:added "4.0"}
  [{:keys [meta] :as book} module]
  (if-let [f (:setup-module meta)] (f module)))

(defn teardown-module-form
  "teardown the module"
  {:added "4.0"}
  [{:keys [meta] :as book} module]
  (if-let [f (:teardown-module meta)] (f module)))

(defn has-ptr-form
  "form to check if pointer exists"
  {:added "4.0"}
  [{:keys [meta] :as book} ptr]
  (if-let [f (:has-ptr meta)] (f ptr)))

(defn setup-ptr-form
  "form to setup pointer"
  {:added "4.0"}
  [{:keys [meta] :as book} ptr]
  (if-let [f (:setup-ptr meta)] (f ptr)))

(defn teardown-ptr-form
  "form to teardown pointer"
  {:added "4.0"}
  [{:keys [meta] :as book} ptr]
  (if-let [f (:teardown-ptr meta)] (f ptr)))

(defn collect-script-natives
  "gets native imported modules"
  {:added "4.0"}
  [modules initial]
  (reduce (fn [out {:keys [native id]}]
            (reduce-kv (fn [out k v]
                         (let [ov (get out k)]
                           (cond (nil? ov)
                                 (assoc out k v)

                                 (= ov v)
                                 out

                                 :else
                                 (h/error "Imports not of the same name"
                                          {:ns   id
                                           :key  k
                                           :curr ov
                                           :new  v}))))
                       out
                       native))
          (or initial {})
          modules))

(defn collect-script-entries
  "collects all entries"
  {:added "4.0"}
  ([{:keys [modules] :as book} sym-ids]
   (let [ids  (h/seqify sym-ids)
         ids  (:all (h/deps:resolve book ids))
         module-ids (set (map ut/sym-module ids)) 
         module-lu  (->> (h/deps:ordered book module-ids)
                         (map (fn [i id]
                                [id {:index i :module (get modules  id)}])
                              (range))
                         (into {}))
         
         ;; sort by module order and line number
         entries (->> (map (partial b/get-code-entry book) ids)
                      (sort-by (juxt (comp :index module-lu :module)
                                     :priority :line :time)))]
     [entries module-lu])))

(defn collect-script
  "collect dependencies given a form and book"
  {:added "4.0"}
  [book form mopts]
  (let [_ (assert book "Book required.")
        input (preprocess/to-input form)
        [form sym-ids]  (preprocess/to-staging input
                                               (:grammar book)
                                               (:modules book) mopts)
        [entries module-lu] (collect-script-entries book sym-ids)
        natives (collect-script-natives (map :module (vals module-lu)) {})]
    [form entries natives]))

(defn collect-script-summary
  "summaries the output of `collect-script`"
  {:added "4.0"}
  [[form entries natives]]
  [form (map ut/sym-full entries) natives])

;;
;;
;;

(defn collect-module
  "collects module
 
   (-> (deps/collect-module (lib/get-book +library-ext+ :lua)
                            (lib/get-module +library-ext+ :lua 'L.util)
                            {:root-ns 'L.script})
       (update :code (fn [arr]
                       (set (map :id arr)))))
   => '{:setup nil,
        :teardown nil,
        :code #{add-fn sub-fn},
       :native {\"cjson\" {:as cjson}},
        :link ([\"./core\" {:as u, :ns L.core}]),
        :export {:entry nil}}"
  {:added "4.0"}
  [book {:keys [native
                link
                export
                suppress]
         :as module}
   & [graph]]
  (let [setup    (setup-module-form book module)
        teardown (teardown-module-form book module)
        code    (->> (vals (dissoc (:code module)
                                   (:as export)))
                     (sort-by (juxt :priority :line :time)))
        link    (if graph
                  (keep (fn [[sym ns]]
                          (cond (= ns (:id module)) nil
                                (get suppress ns) nil
                                
                                :else
                                (let [link (module-link-form book ns graph)]
                                  [link (if (vector? sym)
                                          {:refer sym
                                           :ns ns}
                                          {:as sym
                                           :ns ns})])))
                        link)
                  {})]
    {:setup    setup
     :teardown teardown
     :code     code
     :native   native
     :link     link
     :export   (assoc export
                      :entry (get (:code module)
                                  (:as export)))}))
