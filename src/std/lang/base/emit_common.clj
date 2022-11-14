(ns std.lang.base.emit-common
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.util :as ut]
            [std.lang.base.emit-preprocess :as preprocess]
            [std.lang.base.emit-helper :as helper]))

;; controls whether to fail with a stacktrace
(def ^:dynamic *explode* nil)

(def ^:dynamic *trace* nil)

(def ^:dynamic *indent* 0)

(def ^:dynamic *compressed* false)

(def ^:dynamic *multiline* false)

(def ^:dynamic *max-len* 60)

(def ^:dynamic *emit-internal* false)

(def ^:dynamic *emit-fn* helper/default-emit-fn)

(defmacro with:explode
  "form to control `explode` option"
  {:added "4.0"}
  ([& body]
   `(binding [*explode* true]
      ~@body)))

(defmacro with-trace
  "form to control `trace` option"
  {:added "4.0"}
  ([& body]
   `(binding [*trace* true]
      ~@body)))

(defmacro with-compressed
  "formats without newlines and indents"
  {:added "3.0"}
  ([& body]
   `(binding [*compressed* true]
      ~@body)))

(defmacro with-indent
  "adds indentation levels"
  {:added "3.0"}
  ([[& increment] & body]
   `(binding [*indent* (+ *indent* (or ~@increment 2))]
      ~@body)))

(defn newline-indent
  "returns a newline with indent"
  {:added "3.0"}
  []
  (if-not *compressed*
    (str "\n"
         (apply str (repeat *indent* " ")))
    " "))

(defn emit-reserved-value
  "emits a reserved value"
  {:added "4.0"}
  [form {:keys [reserved] :as grammer} mopts]
  (if-let [{:keys [emit value free raw] :as props} (get reserved form)]
    (or (and value
             (or free
                 raw
                 (h/error "No raw value found" props)))
        
        (h/error "Cannot use reserved token in body" props))))

(defn emit-free-raw
  "emits free value"
  {:added "4.0"}
  ([sep args grammer mopts]
   (let [str-array (binding [*indent* 0]
                     (mapv (fn [e]
                             [e (or (and (string? e) e)
                                    (and (keyword? e)
                                         (or (emit-reserved-value e grammer mopts)
                                             (h/strn e)))
                                    (*emit-fn* e grammer mopts))])
                           args))]
     (loop [[prev out]   (first str-array)
            [[curr s] :as input] (rest str-array)]
       (cond (empty? input)
             out
             
             (or (char? curr)
                 (char? prev))
             (recur [curr (str out s)]
                    (rest input))
             
             :else
             (recur [curr (str out (or sep " ") s)]
                    (rest input)))))))

(defn emit-free
  "emits string with multiline support"
  {:added "4.0"}
  ([{:keys [sep]} [tag & more :as form] grammer mopts]
   (let [{:keys [indent prefix full-body]} (meta form)
         prev    *indent*
         output  (emit-free-raw sep more grammer mopts)]
     (if (str/multi-line? output)
       (let [indent-fn (if full-body
                         str/indent
                         str/indent-rest)]
         (indent-fn output
                    (+ (or prev 0)
                       (or indent 0))))
       output))))

;;
;; COMMENT
;;

(defn emit-comment
  "emits a comment"
  {:added "4.0"}
  ([_ [tag & more :as form] grammer mopts]
   (let [{:keys [indent prefix full-body]} (meta form)
         prefix  (or prefix (-> grammer :default :comment :prefix))
         prev    *indent*
         output  (emit-free-raw " " more grammer mopts)]
     (if (str/multi-line? output)
       (let [indent-fn (if full-body
                         str/indent
                         str/indent-rest)]
         (str prefix " "
              (indent-fn output
                         0
                         {:custom (str (str/spaces (+ (or prev 0)
                                                      (or indent 0)))
                                       prefix
                                       " ")})))
       (str prefix " " output)))))

(defn emit-indent
  "emits an indented form"
  {:added "4.0"}
  ([_ [tag & more :as form] grammer mopts]
   (let [{:keys [indent prefix full-body]} (meta form)]
     (emit-comment nil
                   (with-meta form
                     (merge {:prefix " "}
                            (meta form)))
                   grammer mopts))))

;;
;; MACRO
;;

(defn emit-macro
  "emits form"
  {:added "4.0"}
  ([_ [tag & more :as form] grammer mopts]
   (let [{:keys [macro] :as m} (get-in grammer [:reserved tag])]
     (if m
       (binding [preprocess/*macro-opts* mopts
                 preprocess/*macro-grammer* grammer]
         (*emit-fn* (macro form) grammer mopts))
       (h/error "Not found: " {:input form})))))

;;
;; HELPERS
;;

(defn emit-array
  "returns an array of emitted strings"
  {:added "4.0"}
  ([array grammer mopts]
   (emit-array array grammer mopts *emit-fn*))
  ([array grammer mopts emit-fn]
   (map #(emit-fn % grammer mopts) array)))

(defn emit-wrappable?
  "checks if form if wrappable"
  {:added "4.0"}
  ([form grammer]
   (if (h/form? form)
     (let [infix? #{:infix :infix* :infix- :infix-if
                    :assign :bi
                    :prefix :postfix}
           {:keys [macro emit wrappable]} (get-in grammer [:reserved (first form)])]
       (boolean (or wrappable
                    (infix? emit)
                    (and (= :macro emit)
                         (emit-wrappable? (macro form) grammer)))))
     false)))

(defn emit-squash
  "emits a squashed representation"
  {:added "4.0"}
  [_ [tag & more] grammer mopts]
  (str/join (emit-array more grammer mopts)))

(defn emit-wrapping
  "emits a potentially wrapped form"
  {:added "4.0"}
  ([form grammer mopts]
   (let [result (*emit-fn* form grammer mopts)
         {:keys [start end]} (get-in grammer [:default :common])
         wrap? (emit-wrappable? form grammer)]
     (if wrap?
       (str start result end)
       result))))

(defn wrapped-str
  "wrapped string using `:start` and `:end` keys of grammer"
  {:added "3.0"}
  ([inner path grammer]
   (let [{:keys [start end]} (helper/get-options grammer path)]
     (str start inner end))))

;;
;; OP
;;

(defn emit-unit
  "emits a unit"
  {:added "4.0"}
  ([{:keys [raw default transform] :as props} [_ value] grammer mopts]
   (*emit-fn* (cond-> (or value default)
                transform (transform raw))
              grammer mopts)))

(defn emit-internal
  "emits string within the form"
  {:added "4.0"}
  ([[_ value] grammer mopts]
   (*emit-fn* value grammer mopts)))

(defn emit-internal-str
  "emits internal string"
  {:added "4.0"}
  ([[_ value] grammer mopts]
   (binding [*emit-internal* true]
     (cond (vector? value)
           (str/join "\n"
                     (mapv #(*emit-fn* % grammer mopts)
                           value))

           :else
           (*emit-fn* value grammer mopts)))))

(defn emit-pre
  "emits string before the arg"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (= 1 (count args)) (str "Single argument for " raw))
   (str raw (emit-wrapping (first args) grammer mopts))))

(defn emit-post
  "emits string after the arg"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (= 1 (count args)) (str "Single argument " raw))
   (str (emit-wrapping (first args) grammer mopts) raw)))


(defn emit-prefix
  "emits operator before the arg"
  {:added "4.0"}
  ([raw args grammer mopts]
   (assert (= 1 (count args)) (str "Single argument for " raw))
   (let [{:keys [space]} (get-in grammer [:default :common])]
     (str raw space (emit-wrapping (first args) grammer mopts)))))

(defn emit-postfix
  "emits operator before the arg"
  {:added "4.0"}
  ([raw args grammer mopts]
   (assert (= 1 (count args)) (str "Single argument " raw))
   (let [{:keys [space]} (get-in grammer [:default :common])]
     (str (emit-wrapping (first args) grammer mopts) space raw))))

(defn emit-infix
  "emits infix ops"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (pos? (count args)) (str "One or more arguments " raw))
   (let [{:keys [start end space]} (get-in grammer [:default :common])]
     (str/join (str space raw space)
               (emit-array args grammer mopts emit-wrapping)))))

(defn emit-infix-default
  "emits infix with a default value"
  {:added "3.0"}
  ([raw args default grammer mopts]
   (assert (pos? (count args)) (str "One or more arguments " raw))
   (let [args (case (count args)
                1 (cons default args)
                args)]
     (emit-infix raw args grammer mopts))))

(defn emit-infix-pre
  "emits infix with a default value"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (pos? (count args)) (str "One or more arguments " raw))
   (case (count args)
     1 (emit-pre raw args grammer mopts)
     (emit-infix raw args grammer mopts))))

(defn emit-infix-if-single
  "checks for infix in single
 
   (emit-infix-if '(:? true x y)
                  helper/+default+
                  {})
   => \"true ? x : y\""
  {:added "4.0"}
  ([[_ check then else] grammer mopts]
   (let [{:keys [start end space]} (get-in grammer [:default :common])
         inif (get-in grammer [:default :infix :if])]
     (h/-> (emit-array [check then else] grammer mopts emit-wrapping)
           (interleave [(str space (or (:check inif) "?") space)
                        (str space (or (:then inif) ":") space) ""])
           (str/join)))))

(defn emit-infix-if
  "emits an infix if string"
  {:added "3.0"}
  ([[_ & forms] grammer mopts]
   (let [[check & args] forms]
     (cond (>= 2 (count args))
           (emit-infix-if-single (apply list :? check args) grammer mopts)

           :else
           (let [pairs (map vec (partition 2 forms))
                 _ (if (not= :else (first (last pairs)))
                     (h/error "ternary cond has to end with :else" {:form (last pairs)}))]
             (emit-infix-if-single
              (reduce (fn [acc [q alt]]
                        (list :? q alt acc))
                      (last (last pairs))
                      (reverse (butlast pairs)))
              grammer mopts))))))

(defn emit-between
  "emits the raw symbol between two elems"
  {:added "3.0"}
  ([raw args grammer mopts]
   (str/join raw (emit-array args grammer mopts emit-wrapping))))

(defn emit-bi
  "emits infix with two args"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (= 2 (count args)) (str "Two arguments for " raw))
   (emit-infix raw args grammer mopts)))

(defn emit-assign
  "emits a setter expression"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (= 2 (count args)) (str "Two arguments for assign"))
   (let [{:keys [space assign]} (get-in grammer [:default :common])]
     (str/join (str space (or raw assign) space)
               (emit-array args grammer mopts emit-wrapping)))))

(defn emit-return-do
  "creates a return statement on `do` block"
  {:added "4.0"}
  ([arg grammer mopts]
   (let [{:keys [space]} (get-in grammer [:default :common])
         pre-form (butlast arg)
         ret-form (list 'return (last arg))]
     (*emit-fn* (apply list
                       (concat pre-form
                               [ret-form]))
                grammer mopts))))

(defn emit-return-base
  "return base type"
  {:added "4.0"}
  ([raw args grammer mopts]
   (let [{:keys [space sep]} (get-in grammer [:default :common])
         {:keys [multi]} (get-in grammer [:default :return])
         _ (if (not multi)
             (assert (>= 2 (count args)) (str "One or Zero arguments for " raw)))
         out (->> (emit-array args grammer mopts)
                  (str/join (str sep space)))]
     (cond (and (h/form? (first args))
                (:return/none (meta (first args))))
           out

           :else
           (str raw (if (not-empty args)
                      space)
                out)))))

(defn emit-return
  "creates a return type statement"
  {:added "3.0"}
  ([raw args grammer mopts]
   (let [val (first args)]
     (cond (not (h/form? val))
           (emit-return-base raw args grammer mopts)
           
           ('#{return} (first val))
           (emit-return raw (apply list (rest val)) grammer mopts)
           
           ('#{do do*} (first val))
           (emit-return-do val grammer mopts)

           :else
           (emit-return-base raw args grammer mopts)))))

;;
;; GLOBAL
;;

(defn emit-with-global
  "customisable emit function for global vars"
  {:added "4.0"}
  [_ [_ token] grammer mopts]
  (if-let [transform (get-in grammer [:token :symbol :global])]
    (*emit-fn* (transform token grammer mopts) grammer mopts)
    (apply str (helper/emit-symbol-full token (namespace token) grammer))))

;;
;; TOKEN
;;

(defn emit-symbol-classify
  "classify symbol given options
 
   (emit-symbol-classify 't/hello {:module {:alias '{t table}}})
   => '[:alias table]
 
   (emit-symbol-classify 't.n/hello {:module {:alias '{t table}}})
   => '[:unknown t.n]"
  {:added "3.0"}
  ([sym {:keys [lang module entry book snapshot] :as mopts}]
   (cond (not (namespace sym))
         [:unknown nil]

         :else
         (let [[sym-ns sym-id] (ut/sym-pair sym)]
           (or (if-let [ns (get (:alias module) sym-ns)]
                 [:alias ns])
               
               (let [link-ns (or (if (get (:internal module)
                                          sym-ns)
                                   sym-ns)
                                 
                                 (if (or (= (:id module) sym-ns)
                                         (= (:module entry) sym-ns))
                                   sym-ns)

                                 (get (:link module) sym-ns))
                     sym-ns  (or link-ns sym-ns) 
                     ext-module (if book
                                  (get-in book [:modules sym-ns])
                                  (get-in snapshot [lang :book :modules sym-ns]))]
                 (cond (and (not link-ns)
                            (not ext-module)) nil

                       (= :defglobal (get-in ext-module [:code sym-id :op-key]))
                       [:global sym-ns]

                       (or (= (:id module) sym-ns)
                           (= (:module entry) sym-ns))
                       [:self sym-ns]
                       
                       :else
                       [:link sym-ns]))
               
               (do [:unknown sym-ns]))))))

(defn emit-symbol-standard
  "emits a standard symbol
 
   (emit-symbol-standard 'print! helper/+default+ {:layout :full})
   => \"printf\"
 
   (emit-symbol-standard 'print!
                         {:token {:symbol    {:replace {}}
                                  :string    {:quote :single}}}
                         {:layout :full})
   => \"print!\""
  {:added "3.0"}
  ([sym grammer {:keys [lang layout module] :as mopts}]
   (let [[type ns] (emit-symbol-classify sym mopts)
         {:keys [link-fn] :as opts}  (get-in grammer [:token :symbol])
         dopts (get-in grammer [:default :common])
         module-symbol  (fn [sym]
                          (str (or (get (:internal module)
                                        ns)
                                   (namespace sym))
                               (:namespace dopts)
                               (name sym)))
         link-symbol (fn [sym]
                       (cond (= layout :flat)   (name sym)
                             (= layout :module) (if (= ns (:id module))
                                                  (name sym)
                                                  (module-symbol sym))
                             ;; #_#_
                             :else (module-symbol sym)

                             #_#_
                             :else (h/error "Not Implemented." {:input sym
                                                               :layout layout
                                                                :classify [type ns]})))
         sym  (cond (= type :alias)   (str ns (:namespace dopts) (name sym))

                    (= type :global)  (emit-with-global nil
                                                        [nil (symbol (str ns) (name sym))]
                                                        grammer mopts)
                    
                    (= type :unknown) (if ns
                                        (h/error "Unknown namespace."
                                                 {:lang lang
                                                  :namespace ns
                                                  :input sym
                                                  :layout layout
                                                  :classify [type ns]})
                                        (name sym))
                    
                    link-fn           (*emit-fn* (link-fn sym mopts)
                                                 grammer mopts)

                    (#{:full
                       :host}  layout)  (helper/emit-symbol-full sym ns grammer)
                    
                    (= type :self)     (name sym)
                    
                    (= type :link)     (link-symbol sym))]
     (if (not (and (not= type :unknown)
                   link-fn))
       (apply str (replace (:replace opts) sym))
       sym))))

(defn emit-symbol
  "emits symbol allowing for custom functions"
  {:added "4.0"}
  [sym grammer mopts]
  (let [{:keys [emit-fn]}  (get-in grammer [:token :symbol])]
    (if emit-fn
      (emit-fn sym grammer mopts)
      (emit-symbol-standard sym grammer mopts))))

(defn emit-token
  "customisable emit function for tokens"
  {:added "3.0"}
  ([key token grammer mopts]
   (let [{:keys [emit as custom]} (get-in grammer [:token key])
         custom-fn (case key
                     :string (cond *emit-internal*
                                   identity
                                   
                                   (= :single (get-in grammer [:token :string :quote]))
                                   helper/pr-single
                                   
                                   :else
                                   custom)
                     :symbol (or (if custom
                                   #(custom % grammer mopts))
                                 #(emit-symbol % grammer mopts))
                     custom)
         
         output (cond  custom-fn (custom-fn token)
                       as        (if (or (fn? as)
                                         (var? as))
                                   (as token) as)
                       emit      (emit token grammer mopts)
                       :else     (pr-str token))]
     output)))

(defn emit-with-decorate
  "customisable emit function for global vars"
  {:added "4.0"}
  [_ [_ opts form] grammer mopts]
  (*emit-fn* form grammer mopts))

(defn emit-with-uuid
  "injects uuid for testing"
  {:added "4.0"}
  [_ [_ & [seed0 seed1 :as seeds]] grammer mopts]
  (let [code-fn (fn [x] (cond (or (number? x) (string? x))
                              (h/hash-code x)

                              :else (h/hash-code (h/strn x))))
        uuid  (str (if (not-empty seeds)
                     (java.util.UUID. (code-fn (or seed0 (rand)))
                                      (code-fn (or seed1 (rand))))
                     (java.util.UUID/randomUUID)))]
    (str uuid)))

(defn emit-with-rand
  "injects uuid for testing"
  {:added "4.0"}
  [_ [_ & [type]] grammer mopts]
  (let [token (case type
                :int (rand-int Integer/MAX_VALUE)
                (rand))]
    (*emit-fn* token grammer mopts)))

;;
;; INVOKE
;;

(defn invoke-kw-parse
  "seperates standard and keyword arguments"
  {:added "3.0"}
  ([args]
   (let [idx (h/index-at keyword? args)]
     (if (neg? idx)
       [args []]
       [(take idx args) (partition 2 (drop idx args))]))))

(defn emit-invoke-kw-pair
  "emits a kw argument pair"
  {:added "3.0"}
  ([[k v] grammer mopts]
   (let [{:keys [assign]} (helper/get-options grammer [:default :invoke])]
     (str (str/snake-case (h/strn k)) assign (*emit-fn* v grammer mopts)))))

(defn emit-invoke-args
  "produces the string for invoke call"
  {:added "3.0"}
  ([args grammer mopts]
   (let [[args kwargs] (invoke-kw-parse args)
         cargs (concat (if (not-empty args)
                         (emit-array args grammer mopts))
                       (if (not-empty kwargs)
                         (map #(emit-invoke-kw-pair % grammer mopts) kwargs)))]
     cargs)))

(defn emit-invoke-layout
  "layout for invoke blocks
 
   (emit-invoke-layout [\"ab\\nc\"
                        \"de\\nf\"]
                       helper/+default+ {})
   => \"(ab\\nc,de\\nf)\""
  {:added "4.0"}
  ([str-array grammer mopts]
   (let [{:keys [sep space]} (helper/get-options grammer [:default :invoke])]
     (cond (or (some str/multi-line? str-array)
               (and (not *multiline*)
                    (< (apply + (map count str-array)) *max-len*)))
           (-> (str/join (str sep space) str-array)
               (wrapped-str [:default :invoke] grammer))
           
           :else
           (h/-> (str/join (str sep "\n" (str/spaces 2)
                                )
                           str-array)
                 (str "\n" (str/spaces 2) % "\n")
                 (wrapped-str [:default :invoke] grammer)
                 (str/indent-rest *indent*))))))

(defn emit-invoke-raw
  "invoke call for reserved ops"
  {:added "3.0"}
  ([raw args grammer mopts]
   (let [{:keys [sep space]} (helper/get-options grammer [:default :invoke])
         isep (str sep space)
         cargs (emit-invoke-args args grammer mopts)]
     (str raw  (emit-invoke-layout cargs grammer mopts)))))

(defn emit-invoke-static
  "generates a static call, alternat"
  {:added "3.0"}
  ([[sym & args] grammer mopts]
   (let [sns (namespace sym)
         sfn (name sym)
         {:keys [static]} (helper/get-options grammer [:default :invoke])]
     (emit-invoke-raw (str sns static sfn) args grammer mopts))))

(defn emit-invoke-typecast
  "generates typecast expression"
  {:added "3.0"}
  ([form grammer mopts]
   (assert (<= 2 (count form)) (str "At least two arguments for cast"))
   (let [{:keys [start end]} (helper/get-options grammer [:default :invoke])
         sym   (last form)
         casts (map name (butlast form))]
     (str start start (str/join " " casts) end (*emit-fn* sym grammer mopts) end))))

(defn emit-invoke
  "general invoke call, incorporating keywords"
  {:added "3.0"}
  ([_ [sym & args :as form] grammer mopts]
   (let [{:keys [custom keyword-fn]} (get-in grammer [:default :invoke])]
     (cond custom
           (custom form grammer mopts)

           (keyword? sym)
           (cond keyword-fn
                 (keyword-fn form grammer mopts)
                 
                 (namespace sym)
                 (emit-invoke-static form grammer mopts)
                 
                 :else
                 (emit-invoke-typecast form grammer mopts))
           
           :else
           (emit-invoke-raw (emit-wrapping sym grammer mopts) args grammer mopts)))))

;;
;; New Constructor Call
;;

(defn emit-new
  "invokes a constructor"
  {:added "3.0"}
  ([raw [sym & more :as args] grammer mopts]
   (assert (<= 1 (count args)) (str "One or more arguments for " raw))
   (let [sym (if (keyword? sym)
               (name sym)
               sym)]
     (str raw " " (emit-invoke raw (cons sym more) grammer mopts)))))

;;
;; INDEX
;;

(defn emit-index-entry
  "classifies the index entry"
  {:added "3.0"}
  ([v grammer mopts]
   (cond (symbol? v)
         (do (assert (nil? (namespace v)) "No namespace")
             (str "." (emit-symbol v grammer mopts)))

         (h/form? v)
         (do (assert (symbol? (first v)))
             (let [{:keys [apply]}   (helper/get-options grammer [:default :invoke])
                   braces (meta (first v))]
               (emit-invoke-raw (str apply (*emit-fn* (first v) grammer mopts)
                                     (if (not-empty braces)
                                       (*emit-fn* braces grammer mopts)
                                       ""))
                                (rest v) grammer mopts)))

         (vector? v)
         (do (assert (= 1 (count v)) "Only one index")
             (let [{:keys [start end]}   (helper/get-options grammer [:default :index])]
               (str start (*emit-fn* (first v) grammer mopts) end)))


         (set? v)
         (str "." (*emit-fn* v grammer mopts))
         
         :else (h/error "Not valid" {:input v}))))

(defn emit-index
  "creates an indexed expression"
  {:added "3.0"}
  ([raw args grammer mopts]
   (assert (<= 2 (count args)) (str "Two or more arguments for raw" raw))
   (let [[sym & more] args
         index (mapv #(emit-index-entry % grammer mopts) more)
         sym  (emit-wrapping sym grammer mopts)]
     (apply str sym index))))

;; Ops
;;

(defn emit-op
  "helper for the emit op"
  {:added "3.0"}
  ([key [sym & args :as form] {:keys [reserved] :as grammer} mopts & [expansion]]
   (let [{:keys [op emit transform raw default type] :as props} (get reserved sym)]
     (case emit
       
       ;; WHITESPACE
       :discard       ""
       :free          (emit-free props form grammer mopts)
       :squash        (emit-squash key form grammer mopts)
       :comment       (emit-comment key form grammer mopts)
       :indent        (emit-indent key form grammer mopts)
       
       ;; FORMS
       :token         raw
       :alias         (*emit-fn* (cons raw args) grammer mopts)
       :unit          (emit-unit props form grammer mopts)
       :internal      (emit-internal form grammer mopts)
       :internal-str  (emit-internal-str form grammer mopts)
       :pre           (emit-pre raw args grammer mopts)
       :post          (emit-post raw args grammer mopts)
       :prefix        (emit-prefix raw args grammer mopts)
       :postfix       (emit-postfix raw args grammer mopts)
       :infix         (emit-infix raw args grammer mopts)
       :infix-        (emit-infix-pre raw args grammer mopts)
       :infix*        (emit-infix-default raw args default grammer mopts)
       :infix-if      (emit-infix-if form grammer mopts)
       :bi            (emit-bi raw args grammer mopts)
       :between       (emit-between raw args grammer mopts)
       :assign        (emit-assign raw args grammer mopts)
       :invoke        (emit-invoke-raw raw args grammer mopts)
       :new           (emit-new raw args grammer mopts)
       :index         (emit-index raw args grammer mopts)
       :return        (emit-return raw args grammer mopts)
       :macro         (emit-macro key form grammer mopts)
       :template      (emit-macro key form grammer mopts)
       
       ;; LANGUAGE SPECIFIC GLOBAL
       :with-global   (emit-with-global key form grammer mopts)
       
       ;; LANGUAGE SPECIFIC DECORATIONS
       :with-decorate (emit-with-decorate key form grammer mopts)

       ;; LANGUAGE HELPERS
       :with-uuid     (emit-with-uuid key form grammer mopts)
       :with-rand     (emit-with-rand key form grammer mopts)
       
       ;; RESTRICTED SYMBOLS
       :throw         (h/error "Not allowed as op" {:symbol sym :form form})

       ;; HANDLE ERROR
       (if-let [f (get expansion emit)]
         (f key props form grammer mopts)
         (h/error "No matching clause"
                  {:emit emit
                   :key key
                   :symbol sym
                   :form form
                   :props props}))))))


(defn form-key
  "returns the key associated with the form"
  {:added "3.0"}
  ([form {:keys [banned reserved] :as grammer}]
   (let [key (helper/form-key-base form)
         check-fn  (fn [val input]
                     (if-let [m (get reserved val)]
                       [(:op m) (or (:type m) :statement)]
                       input))
         [key type check?]  (cond (vector? key) key
                                 
                                 :else
                                 (check-fn (first form)
                                           [:invoke :invoke]))
         _ (if (get banned key)
             (h/error "Disallowed in form" {:form form :key key :type type}))]
     [key type check?])))

;;
;;
;;

(def +emit-lookup+
  {:token    #'emit-token
   :invoke   #'emit-invoke
   :macro    #'emit-macro})

(defn emit-common-loop
  "emits the raw string"
  {:added "4.0"}
  ([form grammer mopts]
   (emit-common-loop form
                     grammer
                     mopts
                     +emit-lookup+
                     emit-op))
  ([form grammer mopts fn-lookup fn-default]
   (let [output-fn (fn [[key type]]
                     (let [emit-fn (or (get fn-lookup type)
                                       fn-default)]
                       (emit-fn key form grammer mopts)))
         [key type check? :as input] (form-key form grammer)]
     (cond check?
           (or (emit-reserved-value form grammer mopts)
               (output-fn input))

           :else
           (output-fn input)))))

(defn emit-common
  "emits a string based on grammer"
  {:added "4.0"}
  [form grammer mopts]
  (binding [*emit-fn* emit-common-loop]
    (emit-common-loop form grammer mopts)))


(comment
  
  (./import)
  (./create-tests))
