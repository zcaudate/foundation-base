(ns xt.runtime.type-hashmap-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [xt.lang.base-notify :as notify]))

(l/script- :js
  {:runtime :basic
   :require [[xt.runtime.type-hashmap :as hashmap]
             [xt.runtime.type-hashmap-node :as node]
             [xt.lang.base-lib :as k]
             [xt.lang.base-iter :as it]
             [xt.lang.base-repl :as repl]]})

(l/script- :lua
  {:runtime :basic
   :require [[xt.runtime.type-hashmap :as hashmap]
             [xt.runtime.type-hashmap-node :as node]
             [xt.lang.base-lib :as k]
             [xt.lang.base-iter :as it]
             [xt.lang.base-repl :as repl]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer xt.runtime.type-hashmap/hashmap-create :added "4.0"}
(fact "creates a new hashmap"
  ^:hidden
  (!.js
   (hashmap/hashmap-create {} 0))
  => {"::" "hashmap", "_root" {}, "_size" 0}

  (!.lua
   (hashmap/hashmap-create {} 0))
  => {"::" "hashmap", "_root" {}, "_size" 0})

^{:refer xt.runtime.type-hashmap/hashmap-empty :added "4.0"}
(fact "creates an empty hashmap"
  ^:hidden

  (!.js
    (hashmap/hashmap-empty))
  => {"::" "hashmap", "_size" 0, "_root" {"::" "hashmap.node", "entries" []}}

  (!.lua
   (hashmap/hashmap-empty))
  => {"::" "hashmap", "_size" 0, "_root" {"::" "hashmap.node", "entries" {}}})

^{:refer xt.runtime.type-hashmap/hashmap-find :added "4.0"}
(fact "finds a value by key"
  ^:hidden

  (!.js
    (var hm  (-> (hashmap/hashmap-empty)
                 (hashmap/hashmap-assoc "A" 1)))
    (hashmap/hashmap-find hm "A"))
  => 1
  
  (!.lua
    (var hm  (-> (hashmap/hashmap-empty)
                 (hashmap/hashmap-assoc "A" 1)))
    (hashmap/hashmap-find hm "A"))
  => 1)

^{:refer xt.runtime.type-hashmap/hashmap-assoc :added "4.0"}
(fact "associates a key-value pair in the hashmap"
  ^:hidden

  (!.js
    (var hm  (-> (hashmap/hashmap-empty)
                 (hashmap/hashmap-assoc "A" 1)
                 (hashmap/hashmap-assoc "B" 2)
                 (hashmap/hashmap-assoc "C" 3)
                 (hashmap/hashmap-assoc "D" 4)))
    hm)
  => {"::" "hashmap",
      "_size" 0,
      "_root" {"::" "hashmap.node",
               "entries" [nil nil nil nil nil ["B" 2] nil nil nil nil
                          nil nil ["A" 1] nil nil nil nil nil ["C" 3] ["D" 4]]}}

  (!.lua
    (var hm  (-> (hashmap/hashmap-empty)
                 (hashmap/hashmap-assoc "A" 1)
                 (hashmap/hashmap-assoc "B" 2)
                 (hashmap/hashmap-assoc "C" 3)
                 (hashmap/hashmap-assoc "D" 4)))
    hm))

^{:refer xt.runtime.type-hashmap/hashmap-dissoc :added "4.0"}
(fact "dissociates a key-value pair from the hashmap"
  ^:hidden

  (!.js
    (-> (hashmap/hashmap-empty)
        (hashmap/hashmap-assoc "A" 1)
        (hashmap/hashmap-assoc "B" 2)
        (hashmap/hashmap-assoc "C" 3)
        (hashmap/hashmap-assoc "D" 4)
        (hashmap/hashmap-dissoc "C")
        (hashmap/hashmap-dissoc "D")))
  => {"::" "hashmap", "_size" -2,
      "_root" {"::" "hashmap.node", "entries" [nil nil nil nil nil ["B" 2] nil nil nil nil
                                               nil nil ["A" 1] nil nil nil nil nil nil nil]}})

^{:refer xt.runtime.type-hashmap/hashmap-size :added "4.0"}
(comment "returns the size of the hashmap"
  ^:hidden
  
  (!.js
    (var hm (-> (hashmap/hashmap-empty)
                (hashmap/hashmap-assoc "key1" "val1")
                (hashmap/hashmap-assoc "key2" "val2")))
    (hashmap/hashmap-size hm))
  => 2

  (!.lua
    (var hm (-> (hashmap/hashmap-empty)
                (hashmap/hashmap-assoc "key1" "val1")
                (hashmap/hashmap-assoc "key2" "val2")))
    (hashmap/hashmap-size hm))
  => 2)

^{:refer xt.runtime.type-hashmap/hashmap-to-array :added "4.0"}
(fact "converts hashmap to an array of key-value pairs"
  ^:hidden

  (!.js
    (var hm (-> (hashmap/hashmap-empty)
                (hashmap/hashmap-assoc "key1" "val1")
                (hashmap/hashmap-assoc "key2" "val2")))
    (hashmap/hashmap-to-array hm)))
