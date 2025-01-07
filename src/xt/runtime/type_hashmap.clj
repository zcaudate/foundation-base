(ns xt.runtime.type-hashmap
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]
             [xt.runtime.interface-spec :as spec]
             [xt.runtime.interface-collection :as interface-collection]
             [xt.runtime.type-hashmap-node :as node]
             [xt.runtime.common-hash :as hash]
             [xt.lang.base-iter :as iter]]
   :export [MODULE]})

;; HashMap Implementation

(defn.xt hashmap-create
  "Creates a new hashmap"
  {:added "4.0"}
  [root size]
  (var hashmap {"::" "hashmap"
                 :_root root
                 :_size size})
  (return hashmap))

(defn.xt hashmap-empty
  "Creates an empty hashmap"
  {:added "4.0"}
  []
  (return (-/hashmap-create node/EMPTY_HASHMAP_NODE 0)))

(defn.xt hashmap-find
  "Finds a value by key"
  {:added "4.0"}
  [hashmap key]
  (var #{_root} hashmap)
  (var hash (hash/hash-native key))
  (return (node/node-find-entry _root key hash 0)))

(defn.xt hashmap-assoc
  "Associates a key-value pair in the hashmap"
  {:added "4.0"}
  [hashmap key val]
  (var #{_root _size} hashmap)
  (var hash (hash/hash-native key))
  (var new-root (node/node-insert _root key val hash 0 false))
  (var new-size (:? (k/not-nil? (node/node-find-entry _root key hash 0))
                    _size
                    (+ _size 1)))
  (return (-/hashmap-create new-root new-size)))

(defn.xt hashmap-dissoc
  "Dissociates a key-value pair from the hashmap"
  {:added "4.0"}
  [hashmap key]
  (var #{_root _size} hashmap)
  (var hash (hash/hash-native key))
  (when (k/nil? (node/node-find-entry _root key hash 0))
    (return hashmap))
  (var new-root (node/node-delete _root key hash 0 false))
  (return (-/hashmap-create new-root (- _size 1))))

(defn.xt hashmap-size
  "Returns the size of the hashmap"
  {:added "4.0"}
  [hashmap]
  (var #{_size} hashmap)
  (return _size))

(defgen.xt hashmap-to-iter
  "Converts hashmap to iterator"
  {:added "4.0"}
  [hashmap]
  (var #{_root} hashmap)
  (iter/for:iter [[key val] _root]
    (yield [key val])))

(defn.xt hashmap-to-array
  "Converts hashmap to an array of key-value pairs"
  {:added "4.0"}
  [hashmap]
  (var #{_root} hashmap)
  (var out [])
  (iter/for:iter [[key val] _root]
    (x:arr-push out [key val]))
  (return out))

;; Interface Specification

(def.xt HASHMAP_SPEC
  [[spec/IColl   {:to-iter  -/hashmap-to-iter
                  :to-array -/hashmap-to-array}]
   [spec/IEmpty  {:empty -/hashmap-empty}]
   [spec/ISize   {:size -/hashmap-size}]
   [spec/IFind   {:find -/hashmap-find}]
   [spec/IAssoc  {:assoc -/hashmap-assoc}]
   [spec/IDissoc {:dissoc -/hashmap-dissoc}]])

(def.xt HASHMAP_PROTOTYPE
  (-> -/HASHMAP_SPEC
      (k/proto-spec)
      (k/proto-create)))

(defn.xt hashmap-new
  "Creates a new hashmap with the given key-value pairs"
  {:added "4.0"}
  [pairs]
  (var hashmap (-/hashmap-empty))
  (k/for:array [[key val] pairs]
    (:= hashmap (-/hashmap-assoc hashmap  key val)))
  (return hashmap))

;; Constants
(def.xt EMPTY_HASHMAP
  (-/hashmap-empty))

;; Module Export
(def.xt MODULE (!:module))



(comment
 ^{:no-test true}
 (ns xt.runtime.type-hashmap
   (:require [std.lang :as l]
             [std.lib :as h]))

 (l/script :xtalk
           {:require [[xt.lang.base-lib :as k]
                      [xt.lang.base-iter :as it]
                      [xt.runtime.interface-common :as data-common]]
            :export [MODULE]})

 (def.xt BITS 5)
  (def.xt WIDTH (k/pow 2 -/BITS))
  (def.xt MASK (- -/WIDTH 1))

  (defn.xt impl-mask
    [hash shift]
    (return (k/bit-and (k/bit-rshift hash shift)
                       -/MASK)))

  (defn.xt impl-bitpos
    [hash shift]
    (return (k/bit-lshift 1 (-/impl-mask hash shift))))

  (defn.xt impl-edit-allowed
    [edit-0 edit-1]
    (return (and (k/not-nil? edit-0)
                 (== edit-0 edit-1))))

  (defn.xt impl-copy-without
    [])

  ;;
  ;;
  ;;

  (comment

    (defn.xt data-node-create
      [edit-id shift leaf]
      (return
       {"::" "hashmap.node"
        :bitmap (-/impl-mask (k/get-key leaf "hash") shift)
        :nodemap (-/impl-mask (k/get-key leaf "hash") shift)
        :nodes  [leaf]
        :shift shift}))

    (defn.xt data-node-create
      [edit-id shift leaf]
      (return
       {"::" "hashmap.node"
        :bitmap (-/impl-mask (k/get-key leaf "hash") shift)
        :nodes  [leaf]
        :shift shift}))

    (defn.xt node-create
      "creates a new node"
      {:added "4.0"}
      [edit-id children]
      (var out {"::" "vector.node"
                :children children})
      (when (k/not-nil? edit-id)
        (k/set-key out "edit_id" edit-id))
      (return out))

    (defn.xt node-clone
      "clones the node"
      {:added "4.0"}
      [node]
      (var #{edit-id children} node)
      (return (-/node-create edit-id
                             (k/arr-clone children))))

    (defn.xt node-editable-root
      "creates an editable root"
      {:added "4.0"}
      [node]
      (var #{children} node)
      (return (-/node-create (k/random) (k/arr-clone children))))

    (defn.xt node-editable
      "creates an editable node"
      {:added "4.0"}
      [node edit-id]
      (return (:? (== edit-id (k/get-key node "edit_id"))
                  node
                  (-/node-clone node)))))

  (def.xt MODULE (!:module))
  )
