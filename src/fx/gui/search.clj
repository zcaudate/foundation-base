(ns fx.gui.search
  (:import (javafx.scene Node)))

(defprotocol IParent
  (get-children [this])
  (get-parent [this]))

(extend-protocol IParent
  javafx.scene.Parent
  (get-children [this]
    (.getChildrenUnmodifiable this))
  (get-parent [this]
    (.getParent this))

  javafx.scene.Node
  (get-children [this]
    nil)
  (get-parent [this]
    (.getParent this)))

(defn find-child-by-id
  "find node given parent and id"
  {:added "3.0"}
  ([^Node node id]
   (if (= (.getId node) id)
     node
     (reduce
      (fn [_ node]
        (if-let [found (find-child-by-id node id)]
          (reduced found)))
      nil
      (get-children node)))))

(defn find-by-id
  "find nested node given root and id"
  {:added "3.0"}
  ([^Node node id]
   (find-by-id node id node))
  ([^Node node id skip]
   (if-let [parent (get-parent node)]
     (if-let [found (reduce
                     (fn [_ node]
                       (when-not (= node skip)
                         (if-let [found (find-child-by-id node id)]
                           (reduced found))))
                     nil
                     (get-children parent))]
       found
       (find-by-id parent id parent)))))
