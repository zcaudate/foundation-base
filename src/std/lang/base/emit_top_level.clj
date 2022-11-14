(ns std.lang.base.emit-top-level
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.emit-data :as data]
            [std.lang.base.emit-assign :as assign]
            [std.lang.base.emit-block :as block]
            [std.lang.base.emit-special :as special]
            [std.lang.base.emit-fn :as fn]))

(defn transform-defclass-inner
  "transforms the body to be fn.inner and var.inner"
  {:added "4.0"}
  [body]
  (mapv (fn [form]
          (if (h/form? form)
            (cond (= (first form) 'fn)
                  (cons 'fn.inner (cons (with-meta (second form)
                                          (meta form))
                                        (drop 2 form)))

                  (= (first form) 'var)
                  (cons 'var.inner (rest form))

                  :else form)
            form))
        body))

(defn emit-def
  "creates the def string"
  {:added "3.0"}
  ([key [tag sym body :as form] grammer mopts]
   (let [{:keys [raw space assign statement] :as props} (helper/get-options grammer [:define key])
         typestr (fn/emit-def-type sym (or raw
                                           (h/strn tag))
                                   grammer mopts)
         sym-str   (case key
                     :defglobal (common/emit-with-global nil [nil sym] grammer mopts)
                     (common/*emit-fn* sym grammer mopts))]
     (str typestr
          (if (not-empty typestr) space)
          sym-str
          space assign space
          (common/*emit-fn* body grammer mopts)
          statement))))

(defn emit-declare
  "emits declared 
 
   (emit-declare :def
                 '(declare a b c)
                 +grammer+
                 {})
   => \"def a,b,c\""
  {:added "4.0"}
  ([key [tag & syms] grammer mopts]
   (let [{:keys [raw space sep]}  (helper/get-options grammer [:define key])
         pre (str (or raw tag))]
     (str pre space (str/join sep (common/emit-array (vec syms) grammer mopts))))))

(defn emit-top-level
  "generic define form
 
   (binding [common/*emit-fn* common/emit-common]
     (emit-top-level :defn
                     '(defn abc [a := 0]
                        (+ 1 2 3))
                     +grammer+
                     {}))
   => \"function abc(a = 0){\\n  1 + 2 + 3;\\n}\""
  {:added "3.0"}
  ([key [tag sym & more :as form] grammer mopts]
   (let [module-id (or (-> mopts :entry :module)
                       (-> mopts :module :id))
         sym (if (and
                  (not (:inner (meta sym)))
                  (or (#{:full :host} (:layout mopts))
                      (= key :defglobal)))
               (do (or module-id
                       (h/error "Module not found"
                                (select-keys mopts [:module :entry])))
                   (with-meta (symbol (name module-id)
                                      (name sym))
                     (meta sym)))
               sym)
         form (apply list tag sym more)]
     (case key
       :def         (emit-def key form grammer mopts)
       :defglobal   (emit-def key form grammer mopts)
       :defrun      (block/emit-do  (drop 2 form) grammer mopts)
       :defn        (fn/emit-fn  key form grammer mopts)
       :defgen      (fn/emit-fn  key form grammer mopts)
       :declare     (emit-declare key form grammer mopts)))))

(def +emit-lookup+
  (assoc common/+emit-lookup+
         :data  #'data/emit-data
         :block #'block/emit-block))

(defn emit-form
  "creates a customisable emit and integrating both top-level and statements
   ^:hiddn
   
   (emit-form :custom
              '(custom 1 2 3)
              (assoc-in +grammer+
                        [:reserved 'custom]
                        {:emit  (fn [_ _ _]
                                  'CUSTOM)})
             [])
   => 'CUSTOM"
  {:added "4.0"}
  ([key [sym & args :as form] {:keys [reserved] :as grammer} mopts]
   (let [{:keys [emit type] :as props} (get reserved sym)]
     (if common/*trace*
       (h/prn form))
     (try
       (cond (or (fn? emit)
                 (var? emit))
             (emit form grammer mopts)

             (keyword? emit)
             (common/emit-op key form grammer mopts
                             {:def-assign #'assign/emit-def-assign
                              :quote #'data/emit-quote
                              :table #'data/emit-table
                              :with-module #'special/emit-with-module
                              :with-lang  #'special/emit-with-lang
                              :with-eval  #'special/emit-with-eval
                              :with-deref #'special/emit-with-deref})
             
             :else
             (case type
               :fn         (fn/emit-fn key form grammer mopts)
               :def        (emit-top-level key form grammer mopts)
               :hard-link  (common/*emit-fn* (cons (:raw props) (rest form))
                                             grammer mopts)
               (h/error "Missing key" {:key key
                                       :symbol sym
                                       :props props
                                       :entry (get reserved sym)})))
       (catch Throwable t
         (if common/*explode*
           (h/prn :EMIT-ERROR form t))
         (throw t))))))

