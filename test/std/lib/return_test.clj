(ns std.lib.return-test
  (:use code.test)
  (:require [std.lib.return :refer :all]
            [std.lib.future :as f]))

^{:refer std.lib.return/return-resolve :added "3.0"}
(fact "resolves encased futures"

  (return-resolve (f/future (f/future 1)))
  => 1)

^{:refer std.lib.return/return-chain :added "3.0"}
(fact "chains a function if a future or resolves if not"

  (return-chain 1 inc)
  => 2

  @(return-chain (f/future 1) inc)
  => 2)
