(ns std.lang.model.spec-rust
  (:require [std.lang.base.emit :as emit]
            [std.lang.base.grammar :as grammar]
            [std.lang.base.util :as ut]
            [std.lang.base.book :as book]
            [std.lang.base.script :as script]
            [std.string :as str]
            [std.lib :as h]))

(defn rst-defenum
  "transforms a defenum call"
  {:added "4.0"}
  [[_ sym values]]
  (list :% (list :- "enum" sym
                 (list :- "{")
                 (list 'quote (vec values))
                 (list :- "}"))))

(defn rst-deftrait
  "transforms a defenum call"
  {:added "4.0"}
  [[_ sym values]]
  (list :% (list :- "trait" sym
                 (list :- "{")
                 (list 'quote (vec values))
                 (list :- "}"))))

(defn rst-defstruct
  "transforms a defstruct call"
  {:added "4.0"}
  [[_ sym & args]]
  (let [vars (map #(apply list 'var %) args)]
    (cond (empty? args)
          (list :% (list :- "struct" sym ";"))

          (and (vector? args)
               (vector? (first args)))
          (list :% (list :- "struct" sym) (list :- "{\n")
                (list \|
                      (map (fn [[type name]]
                             (list :- (str name ":" type ",\n")))
                           args))
                (list :- "}"))
          
          :else
          (list :% (list :- "struct" sym) (list :- "[")
                (list \| (apply list 'do vars))
                (list :- "\n}")))))

(defn rst-defimpl
  "emits a defclass template for python"
  {:added "4.0"}
  ([[_ sym inherit & body]]
   (let [{:keys [module] :as mopts}  (preprocess/macro-opts)
         body   (top/transform-defclass-inner body)
         name   (symbol (:id module) (name sym))
         supers (list 'quote (remove keyword? inherit))]
     `(:- :class (:% ~name ~supers) \:
          (\\
           \\ (\| (do ~@body))
           \\)))))

(def +features+
  (-> (grammar/build)
      (grammar/build:override
       {:var        {:symbol '#{var} :raw "let" }
        :range      {:raw ".."}})
      (grammar/build:extend
       {})
      (grammar/build:extend
       {:defstruct  {:op :defstruct :symbol '#{defstruct}
                     :type :def :section :code :emit :macro
                     :macro  #'rst-defstruct
                     :static/type :struct}})))

(def +sym-replace+
  {\- "_"
   \= "_eq"
   \< "_lt"
   \> "_gt"})

(def +template+
  (-> (emit/default-grammar)
      (h/merge-nested
       {:banned #{:set :map :regex}
        :highlight '#{return break}
        :default {:function  {:raw ""
                              :args {:sep ", "}}
                  :invoke    {:reversed true
                              :hint ":"
                              :space ""
                              :static "::"}}
        :data    {:vector    {:start "{" :end "}" :space ""}
                  :tuple     {:start "(" :end ")" :space ""}}
        :block   {:for       {:parameter {:sep ","}}}
        :function {:defn        {:raw "fn"
                                 :args  {:start "(" :end ") " :space ""}}
                   :function  {:raw ""
                               :args   {:start "|" :end "| -> " :space ""}
                               :body   {:start "" :end "" :append true}}}
        :define   {:defglobal {:raw "let"}
                   :def       {:raw "let"}
                   :declare   {:raw "let"}}})
      (assoc-in [:token :symbol :replace] +sym-replace+)))

(def +grammar+
  (grammar/grammar :rs
    (grammar/to-reserved +features+)
    +template+))

(def +meta+
  (book/book-meta
   {:module-current   (fn [])
    :module-import    (fn [name _ opts]  
                        (list :- "#include" name))
    :module-export    (fn [{:keys [as refer]} opts])}))

(def +book+
  (book/book {:lang :rust
              :meta +meta+
              :grammar +grammar+}))

(def +init+
  (script/install +book+))




(comment
  (def +template+
    (->> {:banned #{:keyword}
          :allow   {:assign  #{:symbol :quote}}
          :highlight '#{return break tup with await yield pass raise}
          :default {:comment   {:prefix "#"}
                    :common    {:statement ""}
                    :block     {:parameter {:start " " :end ""}
                                :body      {:start ":" :end "" :append true}}
                    :invoke    {:reversed true
                                :hint ":"}
                    :function  {:raw "lambda"
                                :args      {:start " " :end ":" :space ""}
                                :body      {:start "" :end "" :append true}}
                    :infix     {:if  {:check "and" :then "or"}}
                    :global    {:reference '(globals)}}
          :token   {:nil       {:as "None"}
                    :boolean   {:as #'python-token-boolean}
                    :string    {}
                    :symbol    {:global #'python-symbol-global
                                :namespace {:alias true :link false}}}
          :block    {:try      {:control {:catch  {:raw  "except"
                                                   :args {:start "" :end ":" :space ""}}}}
                     :branch   {:control {:elseif {:raw "elif"}}}}
          :data     {:map-entry {:key-fn data/default-map-key}
                     :set       {:start "{" :end "}" :space ""}
                     :vector    {:start "[" :end "]" :space ""}
                     :tuple     {:start "(" :end ")" :space ""}
                     :free      {:start ""  :end "" :space ""}}
          :function {:defn        {:raw "def"
                                   :args  {:start "(" :end "):" :space ""}}
                     :fn.inner    {:raw "def"
                                   :symbol {:layout :flat}
                                   :args   {:start "(" :end "):" :space ""}}}
          

          :define   {:defglobal  {:raw ""}
                     :def        {:raw ""}}}
         (h/merge-nested (emit/default-grammar)))))
  
