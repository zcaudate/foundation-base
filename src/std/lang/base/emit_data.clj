(ns std.lang.base.emit-data
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.book :as book]
            [std.lang.base.util :as ut]))

;;
;; DATA
;;

(defn default-map-key
  "emits a default map key"
  {:added "4.0"}
  [key grammer nsp]
  (let [key-str (if (keyword? key)
                  (ut/sym-default-str key)
                  key)]
    (common/*emit-fn* key-str grammer nsp)))

(defn emit-map-key
  "emits the map key"
  {:added "4.0"}
  ([form grammer mopts]
   (cond (keyword? form)
         (let [convert (or (get-in grammer [:data :map-entry :keyword])
                           :string)
               tok (case convert
                     :symbol  (symbol (h/strn form))
                     :string  (h/strn form)
                     :keyword form)]
           (common/*emit-fn* tok grammer mopts))
         
         :else
         (common/*emit-fn* form grammer mopts))))

(defn emit-map-entry
  "emits the map entry"
  {:added "3.0"}
  ([[k v] grammer mopts]
   (let [{:keys [start end sep space assign key-fn val-fn]
          :or {key-fn emit-map-key
               val-fn common/*emit-fn*}} (helper/get-options grammer [:data :map-entry])
         val-e (val-fn v grammer mopts)
         val-e (if (str/multi-line? val-e)
                 (str/indent-rest val-e 2)
                 val-e)]
     (str start (str/join (str assign space)
                          [(key-fn k grammer mopts)
                           val-e])
          end))))

(defn emit-singleline-array?
  "checks that array is all single lines
 
   (emit-singleline-array? [\"1\" \"2\" \"3\"])
   => true
 
   (emit-singleline-array? [\"1\" \"\\n2\" \"3\"])
   => false"
  {:added "4.0"}
  [body-arr]
  (and (every? str/single-line? body-arr)
       (and (not common/*multiline*)
            (< (apply + (map count body-arr)) common/*max-len*))))

(defn emit-maybe-multibody
  "checks that array is all single lines
 
   (emit-maybe-multibody [\"1\" \"2\"] \"hello\")
   => \"2hello\"
 
   (emit-maybe-multibody [\"1\" \"2\"] \"\\nhello\")
   => \"1  \\n  hello\""
  {:added "4.0"}
  [[prefix-yes prefix-no] body & [indent]]
  (if (str/multi-line? body)
    (str prefix-yes (str/indent body (or indent
                                     (+ 2 common/*indent*))))
    (str prefix-no body)))

(defn emit-coll-layout
  "constructs the collection"
  {:added "4.0"}
  ([key indent str-array grammer mopts]
   (let [{:keys [sep space tighten] :as opts} (helper/get-options grammer [:data key])]
     (cond (emit-singleline-array? str-array)
           (-> (str/join sep str-array)
               (common/wrapped-str [:data key] grammer))

           tighten
           (-> (str/join (str sep "\n  ") str-array)
               (common/wrapped-str [:data key] grammer)
               (str/indent indent))
           
           :else
           (-> (str "\n"
                    (str/indent 
                     (str "  "
                          (str/join (str sep "\n  ") str-array)
                          "\n")
                     indent))
               (common/wrapped-str [:data key] grammer))))))

(defn emit-coll
  "emits a collection"
  {:added "3.0"}
  ([key form grammer mopts]
   (emit-coll key form grammer mopts common/*emit-fn*))
  ([key form grammer mopts emit-fn]
   (let [indent     common/*indent*
         str-array (binding [common/*indent* 0]
                     (common/emit-array form grammer mopts emit-fn))]
     (emit-coll-layout key indent str-array grammer mopts))))

(defn emit-data-standard
  "emits either a custom string or default coll"
  {:added "4.0"}
  ([key form grammer mopts]
   (let [{:keys [custom]} (helper/get-options grammer [:data key])]
     (if custom
       (custom form grammer mopts)
       (emit-coll key form grammer mopts)))))


(defn emit-data
  "main function for data forms"
  {:added "3.0"}
  ([key form grammer mopts]
   (let [emit (get-in grammer [:data key :emit])]
     (cond emit
           (emit form grammer mopts)
           
           (= key :map-entry)
           (emit-map-entry form grammer mopts)

           :else
           (emit-data-standard key form grammer mopts)))))

(defn emit-quote
  "emit quote structures
 
   (emit-quote nil nil ''(1 2 3) +grammer+ {})
   => \"(1,2,3)\""
  {:added "4.0"}
  [key props [sym & args :as form] grammer mopts]
  (if (== 1 (count args))
    (let [coll (first args)]
      (cond (vector? coll)
            (emit-data :free coll grammer mopts)
            
            (h/form? coll)
            (emit-data :tuple coll  grammer mopts)

            :else
            (emit-data :tuple [coll] grammer mopts)))
    (emit-data :tuple args grammer mopts)))

(defn emit-table-group
  "gets table group
 
   (emit-table-group [:a 1 :b 2 :c :d])"
  {:added "4.0"}
  ([args]
   (let [is-key (fn [x] (or (keyword? x)
                             (and (h/form? x)
                                  (= :% (first x)))))]
     (loop [[e :as args] args
            out []]
       (cond (empty? args)
             out
             
             (is-key e)
             (cond (or (is-key (second args))
                       (empty? (rest args)))
                   (recur (rest args)
                          (conj out (symbol (h/strn e))))

                   :else
                   (recur (drop 2 args)
                          (conj out (vec (take 2 args)))))
             
             :else
             (recur (rest args)
                    (conj out e)))))))

(defn emit-table
  "emit quote structures
 
   (emit-table nil nil '(tab :a 1 :b 2) +grammer+ {})
   => \"{\\\"a\\\":1,\\\"b\\\":2}\""
  {:added "4.0"}
  ([_ _ [_ & args] grammer mopts]
   (let [args (emit-table-group args)
         entry-fn (fn [e]
                    (if (vector? e)
                      (emit-map-entry e grammer mopts)
                      (common/*emit-fn* e grammer mopts)))
         indent     common/*indent*
         str-array (binding [common/*indent* 0]
                     (mapv entry-fn args))]
     (emit-coll-layout :map indent str-array grammer mopts))))

;;
;; TESTING
;;

(defn test-data-loop
  "emit for data structures
 
   (test-data-loop '[(+ 1 2)]
                         +grammer+
                         {})
   => \"[(+ 1 2)]\"
 
   (test-data-loop '{:a (+ 1 2)}
                         +grammer+
                         {})
   => \"{[:a (+ 1 2)]}\"
 
   (test-data-loop '#{(+ 1 2)}
                         +grammer+
                         {})
   => throws
 
   
   [:quote]
   (test-data-loop ''((+ A B) C)
                         +grammer+
                         {})
   => \"((+ A B),C)\"
 
   (test-data-loop ''[(+ A B) C]
                         +grammer+
                         {})
   => \"(+ A B),C\"
   
   [:table]
   (test-data-loop '(tab :a (+ 1 2) :b 2)
                         +grammer+
                         {})
   => \"{\\\"a\\\":(+ 1 2),\\\"b\\\":2}\"
   
   (test-data-loop '(tab 1 (+ 1 2) 3 4 5)
                         +grammer+
                         {})
   => \"{1,(+ 1 2),3,4,5}\""
  {:added "4.0" :adopt true}
  [form grammer mopts]
  (common/emit-common-loop form
                           grammer
                           mopts
                           (assoc common/+emit-lookup+
                                  :data emit-data)
                           (fn [key form grammer mopts]
                             (common/emit-op key form grammer mopts
                                             {:quote emit-quote
                                              :table emit-table}))))

(defn test-data-emit
  [form grammer mopts]
  (binding [common/*emit-fn* test-data-loop]
    (test-data-loop form grammer mopts)))

(comment

  (comment
    :quote         (emit-quote args grammer mopts)
    :table         (emit-table form grammer mopts))
  
  )
