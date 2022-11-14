(ns net.resp.pool-test
  (:use code.test)
  (:require [net.resp.pool :refer :all]
            [net.resp.node :as node]
            [net.resp.connection :as conn]
            [net.resp.wire :as wire]
            [std.concurrent :as cc]
            [std.lib :as h]))

(fact:global
 {:component
  {|node|    {:create   nil
              :setup    (fn [_]
                          (node/start-node nil 4456))
              :teardown node/stop-node}
   |pool|    {:create   (pool {:tag "test.pool"
                               :path [:test :pool]
                               :port 4456})
              :setup    h/start
              :teardown h/stop}}})

^{:refer net.resp.pool/pool :added "3.0"}
(fact "creates the connection pool" ^:hidden

  (pool {:tag "test.pool"
         :path [:test :pool]
         :port 4455})
  => cc/pool?)

^{:refer net.resp.pool/pool:apply :added "3.0"
  :use [|node| |pool|]}
(fact "applys a function to connection arguments" ^:hidden

  (pool:apply |pool| cc/req-fn [["PING"]])
  => "PONG")

^{:refer net.resp.pool/wrap-pool :added "3.0"
  :use [|node| |pool|]}
(fact "wraps a function taking pool" ^:hidden

  ((wrap-pool (fn [pool]
                (h/started? pool)))
   {:pool |pool|})
  => true)

^{:refer net.resp.pool/wrap-connection :added "3.0"
  :use [|node| |pool|]}
(fact "wraps a function taking pool resource"

  ((wrap-connection
    (fn [connection]
      (h/string (conn/connection:request-single connection ["PING"]))))
   {:pool |pool|}) ^:hidden
  => "PONG")
