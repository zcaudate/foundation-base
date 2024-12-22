(ns lib.redis.event-test
  (:use code.test)
  (:require [lib.redis.event :refer :all]
            [lib.redis :as r]
            [lib.redis.bench :as bench]
            [net.resp.pool :as pool]
            [net.resp.connection :as conn]
            [net.resp.wire :as wire]
            [std.concurrent :as cc]
            [std.lib :as h]))

(defn blank
  ([client]
   (doto client (pool/pool:request-single ["FLUSHDB"]))))

(fact:global
 {:setup    [(bench/start-redis-array [17001])]
  :teardown [(bench/stop-redis-array [17001])]
  :component
  {|client|   {:create   (r/client-create {:port 17001})
               :setup    (comp blank r/client:start)
               :teardown r/client:stop}}})


^{:refer lib.redis.event/action:add :added "3.0"}
(fact "adds an action from registry")

^{:refer lib.redis.event/action:remove :added "3.0"}
(fact "removes an action from registry")

^{:refer lib.redis.event/action:list :added "3.0"}
(fact "lists action types")

^{:refer lib.redis.event/action:get :added "3.0"}
(fact "gets action type")

^{:refer lib.redis.event/listener-string :added "3.0"}
(fact "string description of a listener")

^{:refer lib.redis.event/listener? :added "3.0"}
(fact "checks that object is a listener")

^{:refer lib.redis.event/listener-loop :added "3.0"}
(fact "creates a listener loop")

^{:refer lib.redis.event/listener:create :added "3.0"
  :use [|client|]}
(fact "creates a listener"
  ^:hidden

  (def -p- (promise))
  
  (listener:create (assoc |client| :format :edn)
                   :subscribe
                   :test
                   ["foo" "bar"]
                   (fn [_ _ _ msg]
                     (deliver -p- msg)))
  
  (pool/pool:request-single |client| ["PUBLISH" "foo" (str {:a 1 :b 2})])
  => number?

  (deref -p- 100 :failed)
  => {:a 1 :b 2})

^{:refer lib.redis.event/listener:teardown :added "3.0"
  :use [|client|]}
(fact "tears down the listener"
  ^:hidden
  
  (def -p- (promise))
  (def -l- (-> (listener:create (assoc |client| :format :edn)
                                :subscribe
                                :test
                                ["foo" "bar"]
                                (fn [_ _ _ msg]
                                  (deliver -p- msg)))
               
               (listener:teardown)))
  (Thread/sleep 100)
  (pool/pool:request-single |client| ["PUBLISH" "foo" (str {:a 1 :b 2})])
  => 0
  
  (deref -p- 100 :failed)
  => :failed)

^{:refer lib.redis.event/listener:add :added "3.0"
  :use [|client|]}
(fact "adds a listener to the redis client"
  ^:hidden

  (def -p- (promise))
      (listener:add :subscribe |client| :test ["foo" "bar"]
                    (fn [_ _ _ msg]
                      (deliver -p- msg)))

      (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
      => 1

      (deref -p- 100 :failed)
      => "HELLO")

^{:refer lib.redis.event/listener:remove :added "3.0"
  :use [|client|]}
(fact "removes a listener from the client"
  ^:hidden

  (def -p- (promise))
      (listener:add :subscribe |client| :test ["foo" "bar"]
                    (fn [_ _ _ msg]
                      (deliver -p- msg)))
      (listener:remove :subscribe |client| :test)

      (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
      => 0

      (deref -p- 100 :failed)
      => :failed)

^{:refer lib.redis.event/listener:all :added "3.0"
  :use [|client|]}
(fact "lists all listeners"
  ^:hidden

  (-> (doto |client|
        (subscribe  :key  ["foo"] prn)
        (psubscribe :key "*" prn))
      (listener:all))
      => (contains [listener?
                    listener?]))

^{:refer lib.redis.event/listener:count :added "3.0"
  :use [|client|]}
(fact "counts all listeners"
  ^:hidden

  (-> (doto |client|
        (subscribe  :key  ["foo"] prn)
        (psubscribe :key "*" prn))
      (listener:count))
      => {:subscribe 1 :psubscribe 1})

^{:refer lib.redis.event/listener:list :added "3.0"
  :use [|client|]}
(fact "lists all listeners"

  (-> (doto |client|
        (subscribe  :foo  ["foo"] prn)
        (psubscribe :bar "*" prn))
      (listener:list))
  => {:subscribe [:foo] :psubscribe [:bar]})

^{:refer lib.redis.event/listener:get :added "3.0"
  :use [|client|]}
(fact "gets a client listener"

  (-> (doto |client|
        (subscribe  :foo  ["foo"] prn))
      (listener:get :subscribe :foo))
  => listener?)

^{:refer lib.redis.event/subscribe:wrap :added "3.0"}
(fact "wrapper for the subscribe delivery function"
  ^:hidden

  ((subscribe:wrap (fn [_ _ _ msg]
                     msg)
                   {:format :edn})
   [(.getBytes "message")
    (.getBytes "channel")
    (.getBytes (str {:a 1 :b 2}))])
      => {:a 1, :b 2})

^{:refer lib.redis.event/subscribe :added "3.0"
  :use [|client|]}
(fact "subscribes to a channel on the cache"
  ^:hidden

  (def -p- (promise))
      (subscribe |client| :test ["foo" "bar"]
                 (fn [_ _ _ msg]
                   (deliver -p- msg)))

      (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
      => 1

      (deref -p- 100 :failed)
      => "HELLO")

^{:refer lib.redis.event/unsubscribe :added "3.0"
  :use [|client|]}
(fact "unsubscribes from a channel"
  ^:hidden

  (def -p- (promise))
      (subscribe |client| :test ["foo" "bar"]
                 (fn [_ _ _ msg]
                   (deliver -p- msg)))
      (unsubscribe |client| :test)

      (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
      => 0

      (deref -p- 100 :failed)
      => :failed)

^{:refer lib.redis.event/psubscribe:wrap :added "3.0"}
(fact "wrapper for the psubscribe delivery function"
  ^:hidden

  ((psubscribe:wrap (fn [_ _ _ msg]
                      msg)
                    {:format :edn})
   [(.getBytes "pmessage")
    (.getBytes "pattern")
    (.getBytes "channel")
    (.getBytes (str {:a 1 :b 2}))])
      => {:a 1, :b 2})

^{:refer lib.redis.event/psubscribe :added "3.0"
  :use [|client|]}
(fact "subscribes to a pattern on the cache"
  ^:hidden

  (def -p- (promise))
  (psubscribe |client| :test ["*"]
              (fn [_ _ _ msg]
                (deliver -p- msg)))
  
  (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
  => 1
  
  (deref -p- 100 :failed)
  => "HELLO")

^{:refer lib.redis.event/punsubscribe :added "3.0"
  :use [|client|]}
(fact "unsubscribes from the pattern"
  ^:hidden

  (def -p- (promise))
  (doto |client|
    (psubscribe :test ["*"]
                (fn [_ _ _ msg]
                  (deliver -p- msg)))
    (punsubscribe :test))
  
  (pool/pool:request-single |client| ["PUBLISH" "foo" "HELLO"])
  => number?
  
  (deref -p- 100 :failed))

^{:refer lib.redis.event/events-string :added "3.0"}
(fact "creates a string from a set of enums"
  ^:hidden

  (events-string #{:string :hash :generic})
  => "h$g")

^{:refer lib.redis.event/events-parse :added "3.0"}
(fact "creates a set of enums from a string"
  ^:hidden

  (events-parse "h$g")
  => #{:hash :string :generic})

^{:refer lib.redis.event/config:get :added "3.0"
  :use [|client|]}
(fact "gets the config for notifications"
  ^:hidden

  (-> (doto |client|
        (config:set #{:string :generic}))
      (config:get))
  => #{:string :generic}

  (-> (doto |client|
        (config:add #{:list :hash}))
      (config:get))
  => #{:string :generic :list :hash}
  
  
  (-> (doto |client|
        (config:remove #{:list :hash}))
      (config:get))
  => #{:string :generic})

^{:refer lib.redis.event/config:set :added "3.0"
  :use [|client|]}
(fact "sets the config for notifications")

^{:refer lib.redis.event/config:add :added "3.0"
  :use [|client|]}
(fact "adds config notifications")

^{:refer lib.redis.event/config:remove :added "3.0"
  :use [|client|]}
(fact "removes config notifications")

^{:refer lib.redis.event/notify:args :added "3.0"}
(fact "produces the notify args"
  ^:hidden

  (notify:args "test" ["input"])
      => ["__keyspace@*:test:input"])

^{:refer lib.redis.event/notify:wrap :added "3.0"
  :use [|client|]}
(fact "wrapper for the notify delivery function"
  ^:hidden

  ((notify:wrap (fn [_ key] key) {:namespace "test"})
   [(.getBytes "pmessage")
    (.getBytes "__keyspace@*:test:*")
    (.getBytes "__keyspace@*:test:set:abc")])
  => "set:abc")

^{:refer lib.redis.event/notify :added "3.0"
  :use [|client|]}
(fact "notifications for a given client"
  ^:hidden

  (def -p- (promise))
  (doto (assoc |client| :namespace "test")
    (notify :test "*"
            (fn [_ key]
              (deliver -p- key))))
  
  (String. ^bytes (pool/pool:request-single |client| ["SET" "test:A" "ABC"]))
  => "OK"
  
  (list-notify |client|)
  => (contains-in {:test {:pattern "*" :handler fn?}})
  
  (has-notify |client| :test)
  => true
  
  (deref -p- 100 :failed)
  => "A")

(comment
  (def |client|
    
    (r/client:start
     (r/client-create {:port 17001})))
  
  (pool/pool:request-single |client| ["PING"]))


^{:refer lib.redis.event/unnotify :added "3.0"
  :use [|client|]}
(fact "removes notifications for a given client"
  ^:hidden

  (def -p- (promise))
  (doto (assoc |client| :namespace "test")
    (notify :test "*"
            (fn [_ key] (deliver -p- key)))
    (unnotify :test))

  (list-notify |client|)
  => (just {})

  (has-notify |client| :test)
  => false

  (String. ^bytes (pool/pool:request-single |client| ["SET" "test:B" "ABC"]))
  => "OK"

  (deref -p- 100 :failed)
  => (any "B"
          :failed))

^{:refer lib.redis.event/has-notify :added "3.0"}
(fact "checks that a given notify listener is installed")

^{:refer lib.redis.event/list-notify :added "3.0"}
(fact "lists all notify listeners for a client")

^{:refer lib.redis.event/start:events-redis :added "3.0"
  :use [|client|]}
(fact "creates action for `:events`" ^:hidden

  (-> (start:events-redis |client| #{:generic :string :hash :list})
      (config:get)
      (events-string))
      => string?)

^{:refer lib.redis.event/start:notify-redis :added "3.0"
  :use [|client|]}
(fact "creates action for `:notify`" ^:hidden

  (def -p- (promise))

      (-> (assoc |client| :format :json)
          (start:events-redis #{:generic :string})
          (start:notify-redis {:test {:pattern "*"
                                      :handler (fn [_ key]
                                                 (deliver -p- key))}}))

      (pool/pool:request-single |client| ["SET" "a" 1])

      (listener:list |client|)
      => {:notify [:test]}

      (deref -p- 500 :failed)
      => "a")

^{:refer lib.redis.event/stop:notify-redis :added "3.0"}
(fact "stop action for `:notify` field")

(comment
  (./import)
  (pool/pool:request-single |client| ["PUBLISH" "bar" (.getBytes "HELLO")])
  (h/tracked :redis :stop))
