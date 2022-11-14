(ns std.dom.find
  (:require [std.dom.common :as base]))

(declare dom-find
         dom-find-all)

(defn dom-match?
  "tests dom to match on either function or value
   
   (dom-match? (dom/dom-compile [:mock/pane {:hello \"world\"}])
               :hello
               string?)
   => true"
  {:added "3.0"}
  ([dom key match] 
   (let [val (get (:props dom) key)]
    (cond (fn? match)
          (try
            (match val)
            (catch Throwable t false))
          
          :else
          (= match val)))))

(defn dom-find-props
  "find dom element within props
 
   (-> (dom/dom-compile [:mock/pane {:child [:mock/pane {:tag \"A\"}]}])
       :props
       (dom-find-props :tag \"A\")
       str read-string)
   => [:- :mock/pane {:tag \"A\"}]"
  {:added "3.0"}
  ([props key match] 
   (reduce (fn [_ [k v]]
            (if-let [res (cond (base/dom? v)
                               (dom-find v key match)
                               
                               (and (vector? v)
                                    (base/dom? (first v)))
                               (reduce (fn [_ sdom]
                                         (if-let [res (dom-find sdom key match)]
                                           (reduced res)))
                                       nil
                                       v))]
              (reduced res)))
          nil
          props)))

(defn dom-find
  "find dom element
 
   (-> (dom/dom-compile [:mock/pane {:children [[:mock/pane {:tag \"A\"}]
                                                [:mock/pane {:tag \"B\"}]]}])
       (dom-find :tag identity)
       str read-string)
   => [:- :mock/pane {:tag \"A\"}]"
  {:added "3.0"}
  ([dom key match] 
   (cond (dom-match? dom key match)
        dom
        
        (:shadow dom)
        (dom-find (:shadow dom) key match)

        :else
        (dom-find-props (:props dom) key match))))

(defn dom-find-all-props
  "finds all dom elements within props
 
   (-> (dom/dom-compile [:mock/pane {:children [[:mock/pane {:tag \"A\"}]
                                                [:mock/pane {:tag \"B\"}]]}])
       :props
       (dom-find-all-props :tag string? (atom []))
       str read-string)
   => [[:- :mock/pane {:tag \"A\"}] [:- :mock/pane {:tag \"B\"}]]"
  {:added "3.0"}
  ([props key match state] 
   (reduce (fn [_ [k v]]
            (cond (base/dom? v)
                  (dom-find-all v key match state)
                  
                  (and (vector? v)
                       (base/dom? (first v)))
                  (reduce (fn [_ sdom]
                            (dom-find-all sdom key match state))
                          nil
                          v)))
          nil
          props)))

(defn dom-find-all
  "finds all matching dom elements
 
   (-> (dom/dom-compile [:mock/pane {:children [[:mock/pane {:tag \"A\"}]
                                                [:mock/pane {:tag \"B\"}]]}])
       (dom-find-all :tag string?)
       str read-string)
   => [[:- :mock/pane {:tag \"A\"}] [:- :mock/pane {:tag \"B\"}]]"
  {:added "3.0"}
  ([dom key match]
   (dom-find-all dom key match (atom [])))
  ([dom key match state]
   (if (dom-match? dom key match)
     (swap! state conj dom))
   (if (:shadow dom)
     (dom-find-all (:shadow dom) key match state)
     (dom-find-all-props (:props dom) key match state))
   @state))
