(ns lua.nginx.redis-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :basic
   :config  {:program :resty}
   :require [[lua.nginx.redis :as rds]]})

(fact:global
 {:setup    [(l/rt:restart)
             (rt.redis/start-redis-array [17000])]
  :teardown [(rt.redis/stop-redis-array [17000])
             (l/rt:stop)]})

^{:refer lua.nginx.redis/new-connection :added "4.0"}
(fact "creates a new connetion"
  ^:hidden

  (!.lua
   (local conn (rds/new-connection {:port 17000}))
   (. conn (ping)))
  => "PONG")

^{:refer lua.nginx.redis/new-subscription :added "4.0"}
(fact "creates a new subscription"
  ^:hidden
  
  (!.lua
   (local conn  (rds/new-connection {:port 17000}))
   (local sonn  (rds/new-subscription {:port 17000}
                                      ["EVENTS"]))
   
   [(. conn (publish "EVENTS" "HELLO"))
    (rds/read-reply sonn)])
  => [1 ["message" "EVENTS" "HELLO"]])

^{:refer lua.nginx.redis/new-psubscription :added "4.0"}
(fact "creates a new patten subscription"
  ^:hidden
  
  (!.lua
   (local conn  (rds/new-connection {:port 17000}))
   (local sonn  (rds/new-psubscription {:port 17000}
                                       ["*"]))
   
   [(. conn (publish "EVENTS" "HELLO"))
    (rds/read-reply sonn)])
  => [2 ["pmessage" "*" "EVENTS" "HELLO"]])

^{:refer lua.nginx.redis/eval-script :added "4.0"}
(fact "evaluate script from sha/script"
  ^:hidden
  
  (!.lua
   (local conn  (rds/new-connection {:port 17000}))
   (rds/eval-script conn {:sha (@! (h/sha1 "return 1"))
                          :body "return 1"}))
  => 1)

^{:refer lua.nginx.redis/script-tmpl :added "4.0"}
(fact "creates the script function templates"
  ^:hidden

  (require 'kmi.queue.common)
  (rds/script-tmpl '[mq-group-exists
                     kmi.queue.common/mq-common-group-exists])
  => '(defn.lua mq-group-exists
        [conn key partition group]
        (local sha "aa001080fe7c4a62192c441b59753e523835af3a")
        (local body (fn []
                      (return
                       (xt.lang.base-lib/join
                        "\n"
                        ["local K_GROUP = '__group__'"
                         ""
                         "local function mq_path(key,partition,...)"
                         "  return table.concat({key,'_',partition,...},':')"
                         "end"
                         ""
                         "local function mq_common_group_exists(key,partition,group)"
                         "  local k_group = mq_path(key,partition,K_GROUP)"
                         "  return redis.call('HEXISTS',k_group,group) == 1"
                         "end"
                         ""
                         "return mq_common_group_exists(ARGV[1],ARGV[2],ARGV[3])"]))))
        (return (lua.nginx.redis/eval-script conn
                                             (tab :sha sha :body body)
                                             key
                                             partition
                                             group))))

(comment)
