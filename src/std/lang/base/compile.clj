(ns std.lang.base.compile
  (:require [std.make.compile :as compile]
            [std.lang.base.emit :as emit]
            [std.lang.base.util :as ut]
            [std.lang.base.book :as book]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.impl :as impl]
            [std.lang.base.impl-lifecycle :as lifecycle]
            [std.lang.base.library :as lib]
            [std.lang.base.library-snapshot :as snap]
            [std.lib :as h]
            [std.string :as str]
            [std.fs :as fs]))

(defn compile-script
  "compiles a script"
  {:added "4.0"}
  [{:keys [header footer main root target name file] :as opts}]
  (let [opts  (merge {:layout :flat
                      :entry {:label true}}
                     opts)
        entry (compile/compile-resolve main)
        _ (if (not (book/book-entry? entry))
            (h/error "Not a library entry" {:main main}))
        meta   (ptr/ptr-invoke-meta entry
                                    (select-keys opts [:layout
                                                       :emit]))
        body   (impl/emit-script (:form entry) meta)
        full   (compile/compile-fullbody body opts)
        output (compile/compile-out-path opts)]
    (compile/compile-write output full)))

(def +install-script-fn+
  (compile/types-add :script #'compile-script))

(defn compile-module-single
  "compiles a single module"
  {:added "4.0"}
  ([{:keys [header lang footer main graph] :as opts}]
   (let [mopts   (last (impl/emit-options opts))
         body    (lifecycle/emit-module-setup main
                                              mopts)
         full    (compile/compile-fullbody body opts)
         output  (compile/compile-out-path opts)]
     (compile/compile-write output full))))

(def +install-module-single-fn+
  (compile/types-add :module.single #'compile-module-single))

(defn compile-module-graph
  "compiles a module graph"
  {:added "4.0"}
  ([{:keys [header footer lang main root target emit] :as opts}]
   (let [module-id main
         lib         (impl/runtime-library)
         snapshot    (lib/get-snapshot lib)
         book        (snap/get-book snapshot lang)
         deps        (-> (h/deps:resolve book [module-id])
                         :all)
         root-path   (-> (str/replace (name module-id) #"\." "/")
                         (fs/parent))
         root-output (if (empty? target)
                       root
                       (str root "/" target))
         parent-rel  (fn [path]
                       (subs (str path)
                             0
                             (.lastIndexOf (str path) ".")))
         files       (mapv (fn [dep]
                             (let [is-ext      (str/starts-with? (name dep) (parent-rel (str module-id)))
                                   ns-path     (if-not is-ext
                                                 (str/replace (parent-rel (name dep)) #"\." "/")
                                                 (->> (str/replace (name dep) #"\." "/")
                                                      (fs/parent)
                                                      (fs/relativize root-path)))
                                   ns-path     (if (= "" (str ns-path))
                                                 nil
                                                 ns-path)
                                   module      (book/get-module book dep)
                                   ns-file     (or (:file module)
                                                   (book/module-create-filename
                                                    book
                                                    (or (:id module)
                                                        (h/error "MODULE NOT FOUND" {:dep dep}))))
                                   output-path (str/join "/" (filter identity [root-output ns-path ns-file]))]
                               (compile-module-single {:lang  lang
                                                       :layout :module
                                                       :header header
                                                       :footer footer 
                                                       :output output-path
                                                       :main dep 
                                                       :emit (assoc emit
                                                                    :compile {:base dep :root-ns module-id}
                                                                    :static (:static module))
                                                       :snapshot snapshot})))
                           deps)]
     (compile/compile-summarise files))))

(def +install-module-graph-fn+
  (compile/types-add :module.graph #'compile-module-graph))

(defn compile-module-schema
  "compiles all namespaces into a single file (for sql)"
  {:added "4.0"}
  ([{:keys [header footer lang main root target] :as opts}]
   (let [mopts   (last (impl/emit-options opts))
         lib         (impl/runtime-library)
         snapshot    (lib/get-snapshot lib)
         book        (snap/get-book snapshot lang)
         deps        (h/deps:ordered book [main])
         full-arr    (map (fn [module-id]
                            (-> (lifecycle/emit-module-setup module-id
                                                             mopts)
                                (compile/compile-fullbody opts)))
                          deps)
         full        (str/join "\n\n" full-arr)
         output (compile/compile-out-path opts)]
     (compile/compile-write output full))))

(def +install-module-schema-fn+
  (compile/types-add :module.schema #'compile-module-schema))
