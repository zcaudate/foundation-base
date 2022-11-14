(ns std.lib.network-test
  (:use code.test)
  (:require [std.lib.network :refer :all]
            [std.lib.foundation :as h]))

^{:refer std.lib.network/local-host :added "3.0"}
(fact "returns the current host"

  (local-host)
  ;; #object[java.net.Inet4Address 0x4523dee "chapterhouse.base.local/127.0.0.1"]
  => java.net.Inet4Address)

^{:refer std.lib.network/local-ip :added "3.0"}
(fact "returns the current ip"

  (local-ip)
  ;; "127.0.0.1"
  => string?)

^{:refer std.lib.network/local-hostname :added "3.0"}
(fact "returns the current host name"

  (local-hostname)
  ;; "chapterhouse.base.local"
  => string?)

^{:refer std.lib.network/local-shortname :added "3.0"}
(fact "returns the current host short name"

  (local-shortname)
  ;; "chapterhouse"
  => string?)

^{:refer std.lib.network/socket :added "3.0"}
(fact "creates a new socket"

  (h/suppress
   (with-open [s ^java.net.Socket (socket 51311)] s)))

^{:refer std.lib.network/socket:port :added "3.0"}
(fact "gets the remote socket port")

^{:refer std.lib.network/socket:local-port :added "3.0"}
(fact "gets the local socket port")

^{:refer std.lib.network/socket:address :added "3.0"}
(fact "gets the remote socket address")

^{:refer std.lib.network/socket:local-address :added "3.0"}
(fact "getst the local socket address")

^{:refer std.lib.network/port:check-available :added "4.0"}
(fact "check that port is available"

  (port:check-available 51311)
  => boolean?)

^{:refer std.lib.network/port:get-available :added "4.0"}
(fact "get first available port from a range"

  (port:get-available [51311 51312 51313])
  => number?)

^{:refer std.lib.network/wait-for-port :added "3.0"}
(fact "waits for a port to be ready"

  (wait-for-port "localhost" 51311 {:timeout 1000}))
