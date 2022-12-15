(ns fx.prettier-test
  (:use code.test)
  (:require [fx.prettier :refer :all]))

^{:refer fx.prettier/prettier:download :added "4.0"}
(fact "downloads prettier assets from unpkg")

^{:refer fx.prettier/prettier:create :added "4.0"}
(fact "creates a prettier instance")

^{:refer fx.prettier/get-prettier :added "4.0"}
(fact "gets the global prettier instance")

^{:refer fx.prettier/prettify :added "4.0"}
(fact "prettifies a js/jsx string"

  (prettify "const    a  \n =    1")
  => "const a = 1;\n")
