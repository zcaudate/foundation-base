(ns std.lib.template
  (:require [std.lib.collection :as c]
            [std.lib.foundation :as h]
            [std.lib.walk :as walk]))

(def ^:dynamic *locals*)

(defn replace-template
  "replace arguments with values
 
   (replace-template '(+ $0 $1 $2) '[x y z])
   => '(+ x y z)"
  {:added "3.0"}
  ([form args]
   (let [m (zipmap
            (map (fn [i] (symbol (str "$" i)))  (range))
            args)]
     (walk/postwalk-replace m form))))

(defmacro template:locals
  "gets local variable symbol names and values
 
   (let [x 1 y 2]
     (template:locals))
   => '{x 1, y 2}"
  {:added "3.0"}
  ([]
   (into {} (for [k (keys &env)]
              [(list 'symbol (name k)) k]))))

(defn local-eval
  "evals local variables
 
   (let [x 1]
     (binding [*locals* (template:locals)]
       (local-eval '(+ x x))))
   => 2"
  {:added "3.0"}
  ([form]
   (local-eval form *locals*))
  ([form locals]
   (let [top-level (list 'clojure.core/let
                         (vec (apply concat
                                     (map (fn [k]
                                            [k `(get *locals* (symbol ~(name k)))])
                                          (keys locals))))
                         form)]
     (try (eval top-level)
          (catch Throwable t
            (throw (ex-info "Form not valid" {:form top-level}
                            t)))))))

(defn replace-unquotes
  "replace unquote and unquote splicing
 
   (let [x 1]
     (binding [*locals* (template:locals)]
       (replace-unquotes '(+ ~x ~@(list x 1 2 3)))))
   => '(+ 1 1 1 2 3)"
  {:added "3.0"}
  ([form]
   (let [inputs (volatile! [])
         push-fn  (fn [entry]
                    (let [i (count @inputs)]
                      (vswap! inputs conj entry)
                      (symbol (str "$" i))))
         form   (walk/postwalk (fn [form]
                                 (cond (not (list? form))
                                       form

                                       (= (first form) 'clojure.core/unquote)
                                       (push-fn {:tag :unquote :form (second form)})

                                       (= (first form) 'clojure.core/unquote-splicing)
                                       (push-fn {:tag :unquote-splicing :forms (second form)})

                                       :else
                                       form))
                               form)
         processed (zipmap (map (fn [i] (symbol (str "$" i)))
                                (range))
                           (mapv local-eval @inputs))
         unquote  (->> (c/filter-vals #(-> % :tag (= :unquote)) processed)
                       (c/map-vals :form))
         form     (walk/postwalk-replace unquote form)
         splicing (->> (c/filter-vals #(-> % :tag (= :unquote-splicing)) processed)
                       (c/map-vals :forms))
         spl-fn   (fn [form]
                    (reduce (fn [form e]
                              (concat form (if (contains? splicing e)
                                             (get splicing e)
                                             [e])))
                            ()
                            form))
         form     (walk/postwalk (fn [form]
                                   (cond (c/form? form)
                                         (spl-fn form)

                                         (vector? form)
                                         (vec (spl-fn form))

                                         :else
                                         form))
                                 form)]
     form)))

(defn template-fn
  "replace body with template
 
   (let [-x- 'a]
     (binding [*locals* (template:locals)]
       (template-fn '(+ ~-x- 2 nil))))
   => '(+ a 2 nil)"
  {:added "3.0"}
  ([form]
   (-> form
       (replace-unquotes))))

(defmacro $
  "template macro
 
   (def -x- 'a)
 
   ($ (+ ~-x- 2 3))
   => '(+ a 2 3)
 
 
   (let [a []]
     ($ (~@a)))
   => ()
 
   (let [a nil]
     ($ (~@a)))
   => ()"
  {:added "3.0"}
  ([form]
   `(binding [*locals* (template:locals)]
      (-> (quote ~form)
          (replace-unquotes)
          (vary-meta merge (select-keys (h/template-meta)
                                        [:line :column]))))))

(defmacro deftemplate
  "marker for a template form"
  {:added "3.0"}
  ([& body]
   `(defn ~@body)))

(comment

  
  )
