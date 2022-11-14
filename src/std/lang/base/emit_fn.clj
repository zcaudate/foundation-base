(ns std.lang.base.emit-fn
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.emit-data :as data]
            [std.lang.base.emit-block :as block]))

(defn emit-input-default
  "create input arg strings"
  {:added "3.0"}
  ([{:keys [force modifiers symbol value] :as arg} assign grammer mopts]
   (let [{:keys [start end]} (helper/get-options grammer [:default :index])
         {:keys [reversed hint type]}  (helper/get-options grammer [:default :invoke])
         {vmod true kmod false} (if (:vector-last (helper/get-options grammer [:default :modifier]))
                                  (group-by vector? modifiers)
                                  {false modifiers})
         modarr (mapv (fn [mod]
                        (if (keyword? mod)
                          (cond-> (name mod)
                            (:uppercase type) (str/upper-case))
                          (common/*emit-fn* mod grammer mopts)))
                      kmod)
         symstr (if symbol (common/*emit-fn* symbol grammer mopts))
         symstr (if (and (not-empty modarr)
                         reversed)
                  (str symstr hint)
                  symstr)
         prearr (if reversed
                  (cons symstr modarr)
                  (conj modarr symstr))
         prestr (str/join " " (filter identity prearr))]
     (str prestr
          (if (not-empty vmod)
            (str/join (map (fn [arr]
                             (str start
                                  (if-let [n (first arr)]
                                    (common/*emit-fn* n grammer mopts))
                                  end))
                           vmod)))
          (if (or force value)
            (str " " assign " " (common/*emit-fn* value grammer mopts)))))))

(defn emit-hint-type
  "emits the return type"
  {:added "4.0"}
  ([type name suffix grammer mopts]
   (let [arr  (cond-> (:- (meta name))
                :then vec
                (not-empty suffix) (conj suffix))]
     (str/join " " (map (fn [v]
                          (cond (or (keyword? v)
                                    (vector? v)
                                    (string? v))
                                (cond-> (h/strn v)
                                  (:uppercase type) (str/upper-case))

                                :else (common/*emit-fn* v grammer mopts)))
                        arr)))))

(defn emit-def-type
  "emits the def type"
  {:added "4.0"}
  ([name suffix grammer mopts]
   (let [{:keys [type]} (helper/get-options grammer [:default :define])]
     (emit-hint-type type name suffix grammer mopts))))

(defn emit-fn-type
  "returns the function type"
  {:added "3.0"}
  ([name suffix grammer mopts]
   (let [{:keys [type]} (helper/get-options grammer [:default :invoke])]
     (emit-hint-type type name suffix grammer mopts))))

(defn emit-fn-block
  "gets the block options for a given grammer"
  {:added "4.0"}
  [key grammer]
  (h/merge-nested (get-in grammer [:default :function])
                  (get-in grammer [:function key])))

(defn emit-fn-preamble
  "constructs the function preamble"
  {:added "4.0"}
  ([[key name args] block grammer mopts]
   (let [args  (helper/emit-typed-args args grammer)
         {:keys [sep space assign start end multiline]}
         (h/merge-nested (helper/get-options grammer [:default :function :args])
                         (get-in grammer [:function key :args]))
         iargs (map #(emit-input-default % assign grammer mopts)
                    args)]
     (str (if name (common/*emit-fn* name grammer mopts))
          space
          (cond (empty? iargs) (str start end)

                multiline
                (str start
                     (common/with-indent [2]
                       (str (common/newline-indent)
                            (str/join (str sep (common/newline-indent))
                                      iargs)))
                     (common/newline-indent)
                     end)

                :else
                (str start (str/join (str sep space) iargs) end))))))

(defn emit-fn
  "emits a function template"
  {:added "3.0"}
  ([key [tag & body] grammer mopts]
   (let [[name body] (if (symbol? (first body))
                       [(first body) (rest body)]
                       [nil body])
         [args & body] body
         header-only?  (:header (meta name))
         {:keys [compressed] :as block} (emit-fn-block key grammer)
         typestr (emit-fn-type name (or (:raw block) (h/strn tag)) grammer mopts)
         prestr  (emit-fn-preamble [key name args] block grammer mopts)]

     (str (if (not-empty typestr)
            (str typestr " "))
          prestr
          (if header-only?
            (get-in grammer [:default :common :statement])
            (binding [common/*compressed* compressed]
              (block/emit-block-body key block body grammer mopts)))))))

;;
;;
;;

(defn test-fn-loop
  "add blocks, fn, var and const to emit"
  {:added "4.0"}
  [form grammer mopts]
  (common/emit-common-loop form
                           grammer
                           mopts
                           (assoc common/+emit-lookup+
                                  :data data/emit-data
                                  :block block/emit-block)
                           (fn [key form grammer mopts]
                             (case key
                               :fn (emit-fn :function form grammer mopts)
                               (common/emit-op key form grammer mopts
                                               {:quote data/emit-quote
                                                :table data/emit-table})))))

(defn test-fn-emit
  "add blocks, fn, var and const to emit"
  {:added "4.0"}
  [form grammer mopts]
  (binding [common/*emit-fn* test-fn-loop]
    (test-fn-loop form grammer mopts)))
