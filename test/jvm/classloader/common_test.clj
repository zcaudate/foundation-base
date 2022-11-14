(ns jvm.classloader.common-test
  (:use code.test)
  (:require [jvm.classloader.common :refer :all]))

^{:refer jvm.classloader.common/to-url :added "3.0"}
(fact "constructs a `java.net.URL` object from a string"

  (str (to-url "/dev/null"))
  => "file:/dev/null/")
