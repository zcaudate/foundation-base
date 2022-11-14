(ns std.log.common-test
  (:use code.test)
  (:require [std.log :as log]))

^{:refer std.log.common/set-static! :added "3.0"}
(fact "sets the global static variable")

^{:refer std.log.common/set-level! :added "3.0"}
(fact "sets the global level variable")

^{:refer std.log.common/set-context! :added "3.0"}
(fact "sets the global context")

^{:refer std.log.common/set-logger! :added "3.0"}
(fact "sets the global logger")

^{:refer std.log.common/put-logger! :added "3.0"}
(fact "updates the global logger")

^{:refer std.log.common/default-logger :added "3.0"}
(fact "returns the default logger")

^{:refer std.log.common/basic-logger :added "3.0"}
(fact "returns the basic logger")

^{:refer std.log.common/verbose-logger :added "3.0"}
(fact "returns the verbose logger")
