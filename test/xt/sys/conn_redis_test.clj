(ns xt.sys.conn-redis-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [lib.redis.bench :as bench]
            [xt.lang.base-notify :as notify]))

(l/script- :lua
  {:runtime :basic
   :config  {:program :resty}
   :require [[xt.sys.conn-redis :as redis]
             [xt.lang.base-lib :as k]
             [lua.nginx.driver-redis :as lua-driver]]})

(l/script- :js
  {:runtime :basic
   :require [[xt.sys.conn-redis :as redis]
             [xt.lang.base-lib :as k]
             [xt.lang.base-repl :as repl]
             [js.lib.driver-redis :as js-driver]]})

(l/script- :xtalk
  {:require [[xt.sys.conn-redis :as redis]
             [xt.lang.base-lib :as k]]})

(fact:global
 {:setup    [(bench/start-redis-array [17000])
             (l/rt:restart)]
  :teardown [(l/rt:stop)
             (bench/stop-redis-array [17000])]})

^{:refer xt.sys.conn-redis/script-tmpl :added "4.0"}
(fact "creates a script template"
  ^:hidden
  
  (require 'kmi.queue.common)
  (redis/script-tmpl '[mq-group-exists
                       kmi.queue.common/mq-common-group-exists])
  => h/form?
  
  (eval (redis/script-tmpl '[mq-group-not-exists
                             kmi.queue.common/mq-common-group-not-exists]))
  => #'xt.sys.conn-redis-test/mq-group-not-exists)

^{:refer xt.sys.conn-redis/connect :added "4.0"}
(fact "connects to a datasource"
  ^:hidden
  
  ;;
  ;; LUA
  ;;
  
  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   (redis/exec conn "ping" [] {}))
  => "PONG"

  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   (redis/exec conn "echo" ["hello"] {}))
  => "hello"

  ;;
  ;; JS
  ;;
  
  (notify/wait-on :js
    (redis/connect {:constructor js-driver/connect-constructor
                    :port 17000}
                   {:success (fn [conn]
                               (redis/exec conn "ping" []
                                           (repl/<!)))}))
  => "PONG"
  
  (notify/wait-on :js
    (redis/connect {:constructor js-driver/connect-constructor
                    :port 17000}
                   {:success (fn [conn]
                               (redis/exec conn "echo" ["hello"]
                                           (repl/<!)))}))
  => "hello")

^{:refer xt.sys.conn-redis/disconnect :added "4.0"}
(fact "disconnect redis")

^{:refer xt.sys.conn-redis/exec :added "4.0"}
(fact "executes a redis command"
  ^:hidden
  
  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   [(redis/exec conn "ping" [])
    (redis/exec conn "echo" ["hello"])])
  => ["PONG" "hello"]  

  (do
    (notify/wait-on :js
      (:= (!:G conn) (redis/connect {:constructor js-driver/connect-constructor
                                     :port 17000}
                                    (repl/<!))))
    
    [(notify/wait-on :js
       (redis/exec conn "ping" [] (repl/<!)))
     (notify/wait-on :js
       (. (redis/exec conn "echo" ["hello"])
          (then (repl/>notify))))])
  => ["PONG" "hello"])

^{:refer xt.sys.conn-redis/create-subscription :added "4.0"}
(fact "creates a subscription given channel")

^{:refer xt.sys.conn-redis/create-psubscription :added "4.0"}
(fact "creates a pattern subscription given channel")

^{:refer xt.sys.conn-redis/eval-body :added "4.0"}
(fact "evaluates a the body"
  ^:hidden
  
  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   [(redis/eval-body conn {:body "return 1" }
                     []
                     {})
    (redis/eval-body conn {:body "return 0" }
                     []
                     {})
    (redis/eval-body conn {:body "return nil" }
                     []
                     {})
    (redis/eval-body conn {:body "return false" }
                     []
                     {})
    (redis/eval-body conn {:body "return true" }
                     []
                     {})
    (redis/eval-body conn {:body "return 'hello'"}
                     []
                     {})])
  => [1 0 nil nil 1 "hello"]

  
  (do
    (notify/wait-on :js
      (:= (!:G  conn) (redis/connect {:constructor js-driver/connect-constructor
                                      :port 17000}
                                       (repl/<!))))
    [(notify/wait-on :js
       (redis/eval-body conn {:body "return 1"}
                        []
                        (repl/<!)))
     (notify/wait-on :js
       (redis/eval-body conn {:body "return 0"}
                        []
                        (repl/<!)))
     (notify/wait-on :js
       (redis/eval-body conn {:body "return nil"}
                        []
                        (repl/<!)))
     (notify/wait-on :js
       (redis/eval-body conn {:body "return false"}
                        []
                        (repl/<!)))
     (notify/wait-on :js
       (redis/eval-body conn {:body "return true"}
                        []
                        (repl/<!)))
     (notify/wait-on :js
       (redis/eval-body conn {:body "return 'hello'"}
                        []
                        (repl/<!)))])
  => [1 0 nil nil 1 "hello"])

^{:refer xt.sys.conn-redis/eval-script :added "4.0"}
(fact "evaluates sha, then body if errored"
  ^:hidden
  
  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   [(redis/exec conn "flushdb" [])
    (redis/eval-script conn {:sha (@! (h/sha1 "return 1"))
                             :body "return 1"}
                       [])])
  => ["OK" 1]

  (do
    (notify/wait-on :js
      (:= (!:G  conn) (redis/connect {:constructor js-driver/connect-constructor
                                      :port 17000}
                                     (repl/<!))))
    [(notify/wait-on :js
       (redis/exec conn "flushdb" [] (repl/<!)))
     (notify/wait-on :js
       (redis/eval-script conn {:sha (@! (h/sha1 "return 2"))
                                :body "return 2"}
                          []
                          (repl/<!)))])
  => ["OK" 2]

  (!.lua
   (var conn (redis/connect {:constructor lua-driver/connect-constructor
                             :port 17000}
                            {}))
   [(redis/exec conn "flushdb" [])
    (-/mq-group-not-exists conn "a" "b" "c")])
  => ["OK" 1]  

  (do
    (notify/wait-on :js
      (:= (!:G  conn) (redis/connect {:constructor js-driver/connect-constructor
                                      :port 17000}
                                     (repl/<!))))
    [(notify/wait-on :js
       (redis/exec conn "flushdb" [] (repl/<!)))
     (notify/wait-on :js
       (. (-/mq-group-not-exists conn "a" "b" "c")
          (then (repl/>notify))))])
  => ["OK" 1])
