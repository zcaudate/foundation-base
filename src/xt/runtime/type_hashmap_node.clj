
(ns xt.runtime.type-hashmap-node
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export [MODULE]})

;; Basic Definitions
(def.xt HASHMAP_BITS 5)
(def.xt HASHMAP_MASK (- (k/pow 2 -/HASHMAP_BITS) 1))

;; Masks a hash value to fit within a node
(defmacro.xt impl-mask
  "Masks a hash value to the appropriate range"
  {:added "4.0"}
  [hash shift]
  (list 'x:bit-and (list 'x:bit-rshift hash shift) `-/HASHMAP_MASK))

(defn.xt impl-assoc-entry
  "associates a key-value pair in an array of key-value pairs"
  {:added "4.0"}
  [entry key val]
  (var updated false)
  (var result
       (k/arr-map
        entry
        (fn [pair]
          (if (== (k/first pair) key)
            (do (:= updated true)
                (return [key val]))
            (return pair)))))
  (if updated
    (return result)
    (do (x:arr-push result [key val])
        (return result))))

(defn.xt impl-dissoc-entry
  "dissociates a key from an array of key-value pairs"
  {:added "4.0"}
  [entry key]
  (return (k/arr-filter entry (fn:> [pair] (not= (k/first pair) key)))))

;; Node Operations
(defn.xt node-create
  "creates a new hashmap node"
  {:added "4.0"}
  [edit-id entries]
  (var node {"::" "hashmap.node"
             "entries" entries})
  (when (k/not-nil? edit-id)
    (k/set-key node "edit_id" edit-id))
  (return node))

(defn.xt node-clone
  "clones a hashmap node"
  {:added "4.0"}
  [node]
  (var #{edit-id entries} node)
  (return (-/node-create edit-id (k/arr-clone entries))))

(defn.xt ensure-editable
  "ensures a node is editable"
  {:added "4.0"}
  [node]
  (var #{edit-id} node)
  (when (k/nil? edit-id)
    (k/err "Node is not editable")))

(defn.xt ensure-persistent
  "ensures a node is persistent"
  {:added "4.0"}
  [node]
  (var #{edit-id entries} node)
  (cond (k/nil? edit-id)
        (return node)

        :else
        (return (-/node-create nil (k/arr-clone entries)))))

;; Lookup
(defn.xt node-find-entry
  "finds an entry in the node"
  {:added "4.0"}
  [node key hash shift]
  (var #{entries} node)
  (var idx (-/impl-mask hash shift))  ;; Calculate the index in the entries array
  (var entry (k/get-idx entries (x:offset idx)))  ;; Retrieve the entry at the index

  (cond (k/nil? entry)  ;; If no entry exists at the index, return nil
        (return nil)

        (and (k/is-object? entry) (k/get-key entry "::"))  ;; If the entry is another hashmap node
        (return (-/node-find-entry entry key hash (+ shift -/HASHMAP_BITS)))
        
        (== (k/first entry) key)
        (return (k/second entry))))

;; Insertion
(defn.xt node-insert
  "inserts a key-value pair into the node"
  {:added "4.0"}
  [node key val hash shift editable]
  (when editable
    (-/ensure-editable node))

  (var #{edit-id entries} node)
  (var idx   (k/x:offset (-/impl-mask hash shift)))
  (var entry (k/get-idx entries idx))

  (cond
    (k/nil? entry)
    (do (var new-entry [key val])
        (k/set-idx entries idx new-entry)
        (return node))
    
    (k/is-array? entry)
    (do (k/set-idx entries idx (-/impl-assoc-entry entry key val))
        (return node))
    
    (and (k/is-object? entry)
         (k/get-key entry "::"))
    (do (var new-child (-/node-insert entry key val hash (+ shift -/HASHMAP_BITS) editable))
        (k/set-idx entries idx new-child)
        (return node))

    :else
    (do (var child (-/node-create edit-id []))
        (k/set-idx entries idx (-/node-insert child key val hash (+ shift -/HASHMAP_BITS) editable))
        (return node))))

;; Deletion
(defn.xt node-delete
  "deletes a key-value pair from the node"
  {:added "4.0"}
  [node key hash shift editable]
  (when editable
    (-/ensure-editable node))

  (var #{edit-id entries} node)
  (var idx  (k/x:offset (-/impl-mask hash shift)))
  (var entry (k/get-idx entries idx))

  (cond
    (k/nil? entry) (return node)

    (and (k/is-object? entry)
         (k/get-key entry "::"))
    (do (var new-child (-/node-delete entry key hash (+ shift -/HASHMAP_BITS) editable))
        (if (k/is-empty? new-child)
          (do (k/set-idx entries idx nil)
              (return node))
          (do (k/set-idx entries idx new-child)
              (return node))))
    
    (k/is-array? entry)
    (do (k/set-idx entries idx nil)
        (return node))
    
    :else
    (return node)))

;; Constants
(def.xt EMPTY_HASHMAP_NODE
  (-/node-create nil []))

;; Module Export
(def.xt MODULE (!:module))

