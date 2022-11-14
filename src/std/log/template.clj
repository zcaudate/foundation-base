(ns std.log.template
  (:require [std.lib.mustache :as st]))

(defonce +templates+
  (atom {}))

(defn add-template
  "adds a template to the registry
 
   (add-template :error/test \"The error is {{error/value}}\")"
  {:added "3.0"}
  ([class template]
   (swap! +templates+ assoc class template)))

(defn remove-template
  "removes a template from the registry"
  {:added "3.0"}
  ([class]
   (swap! +templates+ dissoc class)))

(defn has-template?
  "checks if template is registered
 
   (has-template? :error/test)"
  {:added "3.0"}
  ([class]
   (boolean (get @+templates+ class))))

(defn list-templates
  "lists all registered templates"
  {:added "3.0"}
  ([]
   @+templates+))

(defn render-message
  "returns a message given a :log/class or :log/template
 
   (render-message {:log/class :error/test
                    :error/value \"HELLO\"})
   => \"The error is HELLO\""
  {:added "3.0"}
  ([{:log/keys [class template] :as item}]
   (let [template (or template (get @+templates+ class))]
     (if template
       (st/render template item)))))
