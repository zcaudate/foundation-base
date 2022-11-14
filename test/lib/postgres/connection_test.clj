(ns lib.postgres.connection-test
  (:use code.test)
  (:require [lib.postgres.connection :as conn]
            [lib.jdbc :as jdbc]))

^{:refer lib.postgres.connection/conn-create :added "4.0"}
(fact "creates a pooled connection"
  ^:hidden
  
  (def +pool+ (conn/conn-create {:dbname "test"}))

  +pool+
  => com.impossibl.postgres.jdbc.PGPooledConnection

  (conn/conn-close +pool+))

^{:refer lib.postgres.connection/conn-close :added "4.0"}
(fact "closes a connection")

^{:refer lib.postgres.connection/conn-execute :added "4.0"
  :setup [(def +pool+ (conn/conn-create {:dbname "test"}))]
  :teardown [(conn/conn-close +pool+)]}
(fact "executes a command"
  ^:hidden
  
  (conn/conn-execute +pool+
                     "select 1;"
                     jdbc/fetch)
  => [{:?column? 1}])

^{:refer lib.postgres.connection/notify-listener :added "4.0"}
(fact "creates a notification listener"
  ^:hidden
  
  (conn/notify-listener {})
  => com.impossibl.postgres.api.jdbc.PGNotificationListener)

^{:refer lib.postgres.connection/notify-create :added "4.0"
  :setup [(def +pair+ [])
          (def +p+ (promise))
          (def +pool+ (conn/conn-create {:dbname "test"}))]
  :teardown [(conn/conn-close +pool+)
             (conn/conn-close (first +pair+))]}
(fact "creates a notify channel"
  ^:hidden
  
  (def +pair+ (conn/notify-create {:dbname "test"}
                                  {:channel "test_channel"
                                   :on-notify (fn [id ch payload]
                                                (deliver +p+ [id ch payload]))}))

  (do (conn/conn-execute +pool+ "NOTIFY test_channel;")
      (deref +p+ 1000 :timed-out)) => vector?)

(comment
  (./import))
