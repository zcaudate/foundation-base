(ns xt.db.gen-bind
  (:require [std.lang :as l]
            [std.lib :as h]
            [rt.postgres :as pg]))

(defn tmpl-route
  "creates a route template"
  {:added "4.0"}
  [[sym src tmeta]]
  (let [{:keys [root] :as tmeta} (merge tmeta
                                        (h/template-meta))
        url (str root "/" (name src))]
    (with-meta
      (list 'def.xt (with-meta sym {:api/type :route
                                    :api/url url})
            (assoc (pg/bind-function @(resolve src))
                   :url url))
      tmeta)))

(defn tmpl-view
  "creates a view template"
  {:added "4.0"}
  [[sym src tmeta]]
  (let [{:keys [view] :as tmeta} (merge tmeta
                                        (h/template-meta))]
    
    (with-meta
      (list 'def.xt (with-meta sym {:api/type :view})
            (pg/bind-view @(resolve src) view))
      tmeta)))

(defn route-list
  "lists all routes"
  {:added "4.0"}
  [& [ns]]
  (mapv l/sym-full
        (l/module-entries :xtalk
                          (or ns (h/ns-sym))
                          (fn [e] (= :route (:api/type e))))))

(defn view-list
  "lists all views"
  {:added "4.0"}
  [& [ns]]
  (mapv l/sym-full
        (l/module-entries :xtalk
                          (or ns (h/ns-sym))
                          (fn [e] (= :view (:api/type e))))))
