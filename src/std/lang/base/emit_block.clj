(ns std.lang.base.emit-block
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.emit-common :as common]))

;;
;; BLOCK BODY
;;

(defn emit-statement
  "emits a statement given grammer"
  {:added "3.0"}
  ([form grammer mopts]
   (let [[_ type] (common/form-key form grammer)
         {:keys [statement]} (get-in grammer [:default :common])
         body (common/*emit-fn*  form grammer mopts)]
     (if (#{:block :free :fn :def} type)
       body
       (str body statement)))))

(defn emit-do
  "emits a do block"
  {:added "3.0"}
  ([args grammer mopts]
   (let [spacing (or (get-in grammer [:default :common :line-spacing]) 1)
         cols    (repeat spacing (common/newline-indent))
         lines   (apply str cols)]
     (str/join lines
               (filter (comp not empty?)
                       (map (fn [form] (emit-statement form grammer mopts))
                            args))))))

(defn emit-do*
  "like do but removes the statment at the end, useful for macros"
  {:added "3.0"}
  ([args grammer mopts]
   (let [out (emit-do args grammer mopts)
         statement (get-in grammer [:default :common :statement])]
     (if (str/ends-with? out statement)
       (h/lsubs out (count statement))
       out))))

(defn block-options
  "gets the block options"
  {:added "3.0"}
  ([key block segment grammer]
   (merge (get-in grammer [:default :common])
          (get-in grammer [:default :block segment])
          (get-in grammer [:block key segment])
          (get-in block   [segment]))))

(defn emit-block-body
  "helper to emit a block body"
  {:added "3.0"}
  ([key block args grammer mopts]
   (let [{:keys [start end append]} (block-options key block :body grammer)]
     (str start
          (common/with-indent [(get-in grammer [:default :common :indent])]
            (str (common/newline-indent)
                 (emit-do args grammer mopts)))
          (if (not append)
            (common/newline-indent))
          end))))

;;
;; BLOCK PARAMS
;;

(defn parse-params
  "parses params for a block"
  {:added "3.0"}
  ([params]
   (cond (vector? params)
         (if (some vector? params)
           (mapv (fn [x] [:statement (if (vector? x) x [x])]) params)
           [:statement params])

         :else
         [:raw params])))

(defn emit-params-statement
  "emits the params for statement"
  {:added "3.0"}
  ([key block args grammer mopts]
   (let [acc (loop [acc  []
                    curr []
                    [x & more :as args] args]
               (cond (empty? args) (conj acc curr)
                     (keyword? x) (recur (conj acc curr) [x] more)
                     :else (recur acc (conj curr x) more)))
         acc (remove empty? acc)
         {:keys [sep space]} (block-options key block :parameter grammer)]
     (str/join space (map (fn [[k & more]]
                            (let [[k args] (if (keyword? k)
                                             [(.replaceAll (h/strn k) "-" " ") more]
                                             [nil (cons k more)])]
                              (cond->> (common/emit-array args grammer mopts)
                                :then (map str/trim-right)
                                :then (str/join (str sep space))
                                k  (str k space))))
                          acc)))))

(defn emit-params
  "constructs string to for loop args"
  {:added "3.0"}
  ([key block params grammer mopts]
   (let [parsed (parse-params params)
         {:keys [statement space start end]} (block-options key block :parameter grammer)
         pstr (case (first parsed)
                :raw (common/*emit-fn*  (second parsed) grammer mopts)
                :statement (emit-params-statement key block (second parsed) grammer mopts)
                (str/join (str statement space)
                          (map #(emit-params-statement key block % grammer mopts)
                               (map second parsed))))]
     (str start pstr end))))

;;
;; BLOCK CONTROLS
;;

(defn emit-block-control
  "emits a control form code"
  {:added "3.0"}
  ([ck {:keys [main] :as cblock} opts [tag & args] grammer mopts]
   (let [main (or main #{:parameter :body})
         [params body] (if (:parameter main)
                         [(first args) (rest args)]
                         [nil args])
         cblock (h/merge-nested cblock opts)]
     (str (or (:raw opts) tag)
          (if (:parameter main)
            (emit-params ck cblock params grammer mopts))
          (if (:body main)
            (emit-block-body ck cblock body grammer mopts))))))

(defn emit-block-controls
  "emits control blocks for a form"
  {:added "3.0"}
  ([key block control ctl-args grammer mopts]
   (let [{:keys [begin append main]} (merge (get-in grammer [:default :block])
                                            block)
         sep (cond append " "
                   :else (common/newline-indent))]
     (->> (mapcat (fn [[ck {:keys [required] :as cblock}]]
                    (let [margs (get ctl-args ck)
                          _     (if (and required (empty? margs))
                                  (h/error "Form is required in body" {:main key :control ck}))
                          opts  (merge (get-in grammer [:default :block])
                                       (get-in grammer [:block key :control :default])
                                       (get-in grammer [:block key :control ck]))]
                      (map (fn [args]
                             (emit-block-control ck cblock opts args grammer mopts))
                           margs)))
                  control)
          (str/join sep)
          (str (if (empty? main) "" sep))))))

;;
;; BLOCK
;;

(defn emit-block-setup
  "parses main and control blocks"
  {:added "4.0"}
  ([key {:keys [raw main control] :as block} [tag & args] grammer mopts]
   (let [main (or main #{:parameter :body})
         [params args] (if (:parameter main)
                         [(first args) (rest args)]
                         [nil args])
         ctl       (h/map-juxt [(comp symbol name) identity] (map first control))
         ctl-fn    (fn [form] (and (h/form? form) (ctl (first form))))
         ctl-args  (->> (filter ctl-fn args)
                        (group-by (comp ctl first)))
         args      (remove ctl-fn args)
         raw   (or (get-in grammer [:block key :raw])
                   raw
                   tag)
         {:keys [start end append] :as wrap} (get-in grammer [:block key :wrap])]
     [raw params args ctl-args wrap])))

(defn emit-block-inner
  "returns the inner block"
  {:added "4.0"}
  ([key {:keys [main control] :as block} form grammer mopts]
   (let [[raw params args ctl-args wrap] (emit-block-setup key block form grammer mopts)
         {:keys [start end append]} (block-options key block :body grammer)]
     (str (:start wrap)
          raw
          (if (:parameter main)
            (emit-params key block params grammer mopts))
          start
          (str/trim-right
           (common/with-indent [(get-in grammer [:default :common :indent])]
             (emit-block-controls key block control ctl-args grammer mopts)))
          (common/newline-indent)
          end
          (if (and wrap (not (:append wrap))) (common/newline-indent))
          
          (:end wrap)))))

(defn emit-block-standard
  "emits a generic block"
  {:added "3.0"}
  ([key {:keys [main control] :as block} form grammer mopts]
   (let [[raw params args ctl-args wrap] (emit-block-setup key block form grammer mopts)
         {:keys [start end append]} wrap]
     (str start
          raw
          (if (:parameter main)
            (emit-params key block params grammer mopts))
          (if (:body main)
            (emit-block-body key block args grammer mopts))
          (if control
            (emit-block-controls key block control ctl-args grammer mopts))
          (if (and wrap (not append)) (common/newline-indent))
          end))))

(defn emit-block
  "emits a minimal block expression"
  {:added "3.0"}
  ([key [sym & args :as form] {:keys [reserved] :as grammer} mopts]
   (let [{:keys [emit block macro] :as op} (get reserved sym)]
     (case emit
       :do    (emit-do  args grammer mopts)
       :do*   (emit-do* args grammer mopts)
       :macro (common/emit-macro key form grammer mopts)
       :inner (emit-block-inner key block form grammer mopts)
       (if (or (fn? emit) (var? emit))
         (emit form grammer mopts)
         (emit-block-standard key block form grammer mopts))))))


;;
;; TESTING
;;

(defn test-block-loop
  "emits with blocks"
  {:added "4.0"}
  [form grammer mopts]
  (common/emit-common-loop form
                           grammer
                           mopts
                           (assoc common/+emit-lookup+ :block emit-block)
                           common/emit-op))

(defn test-block-emit
  "emit with blocks"
  {:added "4.0"}
  [form grammer mopts]
  (binding [common/*emit-fn* test-block-loop]
    (test-block-loop form grammer mopts)))


(comment
  (./create-tests)
  )
