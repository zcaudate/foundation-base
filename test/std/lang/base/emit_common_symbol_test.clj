(ns std.lang.base.emit-common-symbol-test
  (:use code.test)
  (:require [std.lang.base.emit-common :as common :refer :all]
            [std.lang.base.emit-helper :as helper]
            [std.lang.base.grammer :as grammer]
            [std.lib :as h]))

(def +reserved+
  (-> (grammer/build)
      (grammer/to-reserved)))

(def +grammer+
  (grammer/grammer :test +reserved+ helper/+default+))

^{:refer std.lang.base.emit-common/emit-symbol :adopt true :added "4.0"}
(fact "emit symbol"

  (emit-symbol 'hello/hello +grammer+ {:layout :full
                                       :module {:link '{hello hello}}})
  => "hello____hello"

  (emit-symbol 'hello/hello +grammer+ {:layout :module
                                       :module {:link '{hello hello}}})
  => "hello.hello"

  (emit-symbol 'hello/hello +grammer+ {:layout :host
                                       :module {:link '{hello hello}}})
  => "hello____hello"

  (emit-symbol 'hello/hello +grammer+ {:layout :flat
                                       :module {:link '{hello hello}}})
  => "hello"

  (emit-symbol 'hello/hello +grammer+ {:layout :full
                                       :module {:link '{hello hello.world.again}}})
  => "hello_world_again____hello")
