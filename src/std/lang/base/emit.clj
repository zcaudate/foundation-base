(ns std.lang.base.emit
  (:require [std.string :as str]
            [std.lib :as h]
            [std.lang.base.emit-common :as common]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.emit-block :as block]
            [std.lang.base.emit-top-level :as top]
            [std.lang.base.emit-preprocess :as preprocess]
            [std.lang.base.emit-fn :as fn]
            [std.lang.base.grammer :as grammer]
            [std.lang.base.util :as ut]))

(defn default-grammer
  "returns the default grammer
 
   (emit/default-grammer)
   => map?"
  {:added "4.0"}
  [& [m]]
  (h/merge-nested helper/+default+ m))

(def +option-keys+
  [:lang
   :entry
   :module
   :book
   :snapshot
   :layout
   :emit])

(defn emit-main-loop
  "creates the raw emit
 
   (emit/emit-main-loop '(not (+ 1 2 3))
                       +grammer+
                       {})
   => \"!((+ 1 2 3))\""
  {:added "4.0"}
  ([form grammer mopts]
   (common/emit-common-loop form
                            grammer
                            mopts
                            top/+emit-lookup+
                            top/emit-form)))

(defn emit-main
  "creates the raw emit with loop
 
   (emit/emit-main '(not (+ 1 2 3))
                   +grammer+
                   {})
   => \"!(1 + 2 + 3)\""
  {:added "4.0"}
  ([form grammer mopts]
   (binding [common/*emit-fn* emit-main-loop]
     (emit-main-loop form grammer mopts))))

(defn emit
  "emits form to output string"
  {:added "4.0"}
  ([form grammer namespace mopts]
   (let [mopts (select-keys mopts +option-keys+)]
     (binding [*ns* (or (if namespace
                          (the-ns namespace))
                        *ns*)]
       (cond (:emit grammer)
             ((:emit grammer) form mopts)
             
             :else
             (emit-main form grammer mopts))))))

(defmacro with:emit
  "binds the top-level emit function to common/*emit-fn*
 
   (emit/with:emit
    (common/*emit-fn* '(not (+ 1 2 3))
                      +grammer+
                      {}))
   => \"!(1 + 2 + 3)\""
  {:added "4.0"}
  [& body]
  `(binding [common/*emit-fn* emit-main-loop]
     ~@body))

;;
;;
;;
;;


(def +test-grammer+
  (delay (grammer/grammer :test
           (grammer/to-reserved (grammer/build))
           helper/+default+)))

(defn prep-options
  "prepares the options for processing"
  {:added "4.0"}
  [meta]
  (let [{:keys [lang grammer book namespace snapshot]
         step :-} meta
        step (or step
                 (if (or (and lang snapshot)
                         book)
                   :staging
                   :input))
        namespace (or namespace
                      (h/ns-sym))
        book     (or (if (symbol? book)
                       @(resolve book)
                       book)
                     (get-in snapshot [lang :book]))
        grammer  (or (if (symbol? grammer)
                       @(resolve grammer)
                       grammer)
                     (if book (:grammer book))
                     @+test-grammer+)
        mopts (select-keys meta +option-keys+)]
    [step grammer book namespace mopts]))

(def +steps+
  [[:raw]
   [:input]
   [:staging]])

(defn prep-form
  "prepares the form"
  {:added "4.0"}
  [step form grammer book mopts]
  (case step
    :raw     [form]
    :input   [(preprocess/to-input form)]
    :staging (preprocess/to-staging (preprocess/to-input form)
                                    grammer
                                    (:modules book)
                                    mopts)))
