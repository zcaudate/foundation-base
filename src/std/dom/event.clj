(ns std.dom.event
  (:require [std.dom.common :as base]
            [std.dom.local :as local]
            [std.dom.react :as react]
            [std.dom.type :as type]))

(defn event-params
  "converts input into event params"
  {:added "3.0"}
  ([params] 
   (let [m   (cond (keyword? params)
                  {:id params}
                  
                  (map? params) params
                  
                  :else
                  (throw (ex-info "Not supported" {:input params})))]
    m)))

(defn event-handler
  "finds the relevant handler by ancestry
 
   (def -parent- (doto (dom/dom-create :mock/pane)
                   (dom/dom-attach :parent-handler)))
   
   (def -child-  (doto (dom/dom-create :mock/pane)
                   (mut/mutable:set :parent -parent-)))
   
   (event-handler -child-)
   => :parent-handler"
  {:added "3.0"}
  ([dom] 
   (or (:handler dom)
      (if-let [parent (:parent dom)]
        (event-handler parent))
      base/*dom-handler*)))

(defn handle-local
  "handles a local dom event
 
   (handle-local (-> (dom/dom-create :mock/pane-local)
                     (dom/dom-render)
                     :shadow)
                 {:id :local/test})
   ;;[:+ :mock/pane-local]
   => dom/dom?"
  {:added "3.0"}
  ([dom {:keys [id] :as m}] 
   (let [parent  (local/local-parent dom)
        handler (or (:local/handler (:cache parent))
                    base/*dom-handler*)]
    (handler dom m))))

(defn handle-event
  "handles an event given all necessary inputs"
  {:added "3.0"}
  ([dom params item listener data] 
   (let [{:keys [id] :as m}  (assoc (merge params data) :dom dom :item item :listener listener)]
    (cond (= id :dom/set)
          (react/dom-set-state m)

          (-> (namespace id) (= "local"))
          (handle-local dom m)
          
          :else
          (let [handler (event-handler dom)]
            (handler dom m))))))
