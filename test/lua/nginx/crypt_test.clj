(ns lua.nginx.crypt-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :postgres
  {:runtime :jdbc.client
   :config  {:rt/id :test.scratch
             :dbname "test-scratch"
             :temp :create}
   :require [[rt.postgres :as pg]]})

(l/script- :lua
  {:runtime :basic
   :config  {:program :resty}
   :require [[xt.lang.base-lib :as k :include [:json]]
             [lua.nginx :as n]
             [lua.nginx.crypt :as crypt]]})

(fact:global
 {:setup    [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer lua.nginx.crypt/hmac :added "4.0"}
(fact "same functionality as postgres crypt"
  ^:hidden
  
  (pg/crypt "hello" "$1$qI5PyQbL")
  => "$1$qI5PyQbL$CGhOca3eF1M4DEWbsndfv0"
  
  (crypt/crypt "hello" "$1$qI5PyQbL")
  => "$1$qI5PyQbL$CGhOca3eF1M4DEWbsndfv0")


^{:refer lua.nginx.crypt/gen-salt :added "4.0"}
(fact "generates salt compatible with pgcrypto libraries"
  ^:hidden
  
  (== (count (pg/gen-salt "md5"))
      (count (crypt/gen-salt "md5")))
  => true

  (== (count (pg/gen-salt "bf"))
      (count (crypt/gen-salt "bf")))
  => true

  ;;
  ;; PG COMPATIBLE
  ;;


  (pg/crypt "HELLO"
            (crypt/gen-salt "bf"))
  => string?

  (pg/crypt "HELLO"
            (crypt/gen-salt "md5"))
  => string?)
