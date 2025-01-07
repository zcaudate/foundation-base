(ns xt.runtime.type-hashmap-node-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [xt.lang.base-notify :as notify]))

(l/script- :js
  {:runtime :basic
   :require [[xt.runtime.type-hashmap-node :as node]
             [xt.runtime.common-hash :as hash]
             [xt.lang.base-lib :as k]
             [xt.lang.base-iter :as it]
             [xt.lang.base-repl :as repl]]})

(l/script- :lua
  {:runtime :basic
   :require [[xt.runtime.type-hashmap-node :as node]
             [xt.runtime.common-hash :as hash]
             [xt.lang.base-lib :as k]
             [xt.lang.base-iter :as it]
             [xt.lang.base-repl :as repl]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})


^{:refer xt.runtime.type-hashmap-node/impl-mask :added "4.0"}
(fact "Masks a hash value to the appropriate range"
  ^:hidden

  (!.js
    [(node/impl-mask 123456789 5)
     (node/impl-mask 0xFFFFFFFF 10)])
  => [8 31]

  (!.lua
    [(node/impl-mask 123456789 5)
     (node/impl-mask 0xFFFFFFFF 10)])
  => [8 31])

^{:refer xt.runtime.type-hashmap-node/impl-assoc-entry :added "4.0"}
(fact "associates a key-value pair in an array of key-value pairs"
  ^:hidden

  (!.js
    [(node/impl-assoc-entry [["k1" "v1"] ["k2" "v2"]] "k1" "NEW")
     (node/impl-assoc-entry [["k1" "v1"]] "k2" "v2")])
  => [[["k1" "NEW"] ["k2" "v2"]]
      [["k1" "v1"] ["k2" "v2"]]]
  
  (!.lua
    [(node/impl-assoc-entry [["k1" "v1"] ["k2" "v2"]] "k1" "NEW")
     (node/impl-assoc-entry [["k1" "v1"]] "k2" "v2")])
  => [[["k1" "NEW"] ["k2" "v2"]]
      [["k1" "v1"] ["k2" "v2"]]])

^{:refer xt.runtime.type-hashmap-node/impl-dissoc-entry :added "4.0"}
(fact "dissociates a key from an array of key-value pairs"
  ^:hidden

  (!.js
    [(node/impl-dissoc-entry [["k1" "v1"] ["k2" "v2"]] "k1")
     (node/impl-dissoc-entry [["k1" "v1"] ["k2" "v2"]] "k3")])
  => [[["k2" "v2"]]
      [["k1" "v1"] ["k2" "v2"]]]
  
  (!.lua
    [(node/impl-dissoc-entry [["k1" "v1"] ["k2" "v2"]] "k1")
     (node/impl-dissoc-entry [["k1" "v1"] ["k2" "v2"]] "k3")])
  => [[["k2" "v2"]]
      [["k1" "v1"] ["k2" "v2"]]])

^{:refer xt.runtime.type-hashmap-node/node-create :added "4.0"}
(fact "creates a new hashmap node"
  ^:hidden

  (!.js
    (node/node-create nil []))
  => {"::" "hashmap.node", "entries" []}
  
  (!.lua
   (node/node-create nil []))
  => {"::" "hashmap.node", "entries" {}})

^{:refer xt.runtime.type-hashmap-node/node-clone :added "4.0"}
(fact "clones a hashmap node"
  ^:hidden
  (!.js
   (node/node-clone
    (node/node-create nil [["k1" "v1"]])))
  => {"::" "hashmap.node", "entries" [["k1" "v1"]]}

  (!.lua
   (node/node-clone
    (node/node-create nil [["k1" "v1"]])))
  => {"::" "hashmap.node", "entries" [["k1" "v1"]]})

^{:refer xt.runtime.type-hashmap-node/ensure-editable :added "4.0"}
(fact "ensures a node is editable"
  ^:hidden

  (!.js
    (node/ensure-editable
     (node/node-create nil [])))
  => (throws)

  (!.lua
   (node/ensure-editable
    (node/node-create nil [])))
  => (throws))

^{:refer xt.runtime.type-hashmap-node/ensure-persistent :added "4.0"}
(fact "ensures a node is persistent"
  ^:hidden
  
  (!.js
    (node/ensure-persistent (node/node-create nil [])))
  => {"::" "hashmap.node", "entries" []}

  (!.lua
    (node/ensure-persistent (node/node-create nil [])))
  => {"::" "hashmap.node", "entries" {}})

^{:refer xt.runtime.type-hashmap-node/node-find-entry :added "4.0"}
(fact "finds an entry in the node"
  ^:hidden

  (!.js
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)
        (node/node-insert  "key2" "val2" (hash/hash-native "key2") 0 false)
        (node/node-find-entry "key1" (hash/hash-native "key1") 0)))
  => "val1"

  (!.lua
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)
        (node/node-insert  "key2" "val2" (hash/hash-native "key2") 0 false)
        (node/node-find-entry "key1" (hash/hash-native "key1") 0)))
  => "val1")





{"::" "hashmap.node", "entries" [nil nil nil nil nil nil nil ["key1" "val1"]]}
{"::" "hashmap.node", "entries" [nil nil nil nil nil nil nil ["key1" "val1"]]}

(!.lua
  (var n (node/node-create nil []))
  (node/node-insert n "key1" "val1" (hash/hash-native "key1") 0 false
                    )
  n)



(node/impl-mask (hash/hash-native "key1") 0)

^{:refer xt.runtime.type-hashmap-node/node-insert :added "4.0"}
(fact "inserts a key-value pair into the node"
  ^:hidden
  
  (!.lua
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)))
  => {"::" "hashmap.node", "entries" [nil nil nil nil nil nil nil ["key1" "val1"]]}
  
  (!.js
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)
        (node/node-insert  "key2" "val2" (hash/hash-native "key2") 0 false)))
  => {"::" "hashmap.node",
      "entries" [nil nil nil nil nil nil nil ["key1" "val1"] nil nil nil nil nil nil
                 nil nil nil nil nil nil nil nil nil nil nil nil ["key2" "val2"]]})

^{:refer xt.runtime.type-hashmap-node/node-delete :added "4.0"}
(fact "deletes a key-value pair from the node"
  ^:hidden
  
  (!.js
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)
        (node/node-insert  "key2" "val2" (hash/hash-native "key2") 0 false)
        (node/node-insert  "key3" "val3" (hash/hash-native "key3") 0 false)
        (node/node-delete "key1" (hash/hash-native "key1") 0 false)))
  => {"::" "hashmap.node", "entries" [nil nil nil nil nil nil nil nil nil nil nil nil nil ["key3" "val3"]
                                      nil nil nil nil nil nil nil nil nil nil nil nil ["key2" "val2"]]}

  (!.lua
    (-> (node/node-create nil [])
        (node/node-insert  "key1" "val1" (hash/hash-native "key1") 0 false)
        (node/node-insert  "key2" "val2" (hash/hash-native "key2") 0 false)
        (node/node-delete "key2" (hash/hash-native "key2") 0 false)))
  => {"::" "hashmap.node", "entries" [nil nil nil nil nil nil nil ["key1" "val1"]]})
