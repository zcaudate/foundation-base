(ns std.lang.model.spec-c
  (:require [std.lang.base.emit :as emit]
            [std.lang.base.grammar :as grammar]
            [std.lang.base.util :as ut]
            [std.lang.base.book :as book]
            [std.lang.base.script :as script]
            [std.string :as str]
            [std.lib :as h]))

(defn tf-define
  "not sure if this is needed (due to defmacro) but may be good for source to source"
  {:added "4.0"}
  [[_ sym & more :as form]]
  (let [[args body] (if (= 1 (count more))
                      [nil (first more)]
                      more)]
    (if args
      (list :- "#define" (list :% sym (list 'quote
                                            (apply list args)))
            body)
      (list :- "#define" sym body))))

(def +features+
  (-> (grammar/build :exclude [:data-shortcuts
                               :control-try-catch
                               :class])
      (grammar/build:extend
       {:define  {:op :define  :symbol '#{define}  :macro #'tf-define
                  :type :def :emit :macro
                  :section :code :priority 1}})))

(def +template+
  (->> {:banned #{:set :map :regex}
        :highlight '#{return break}
        :default {:function  {:raw ""}}
        :data    {:vector    {:start "{" :end "}" :space ""}
                  :tuple     {:start "(" :end ")" :space ""}}
        :block  {:for       {:parameter {:sep ","}}}
        :define {:def       {:raw ""}}}
       (h/merge-nested (emit/default-grammar))))

(def +grammar+
  (grammar/grammar :c
    (grammar/to-reserved +features+)
    +template+))

(def +meta+
  (book/book-meta
   {:module-current   (fn [])
    :module-import    (fn [name _ opts]  
                        (h/$ (:- "#include" ~name)))
    :module-export    (fn [{:keys [as refer]} opts])}))

(def +book+
  (book/book {:lang :c
              :meta +meta+
              :grammar +grammar+}))

(def +init+
  (script/install +book+))
