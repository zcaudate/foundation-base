(ns std.image.base-test
  (:use code.test)
  (:require [std.image.base :refer :all]
            [std.image.base.model :as model]))

^{:refer std.image.base/image :added "3.0"}
(fact "creates a new image record for holding data"

  (image {:size [100 100]
          :model :ushort-555-rgb
          :data nil})
  ;; #img[100 100]{:format :ushort-555-rgb}
  => std.image.base.Image)