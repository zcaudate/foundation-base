(ns std.lang.model.spec-xtalk
  (:require [std.lang.base.emit :as emit]
            [std.lang.base.grammer :as grammer]
            [std.lang.base.util :as ut]
            [std.lang.base.book :as book]
            [std.lang.base.script :as script]
            [std.string :as str]
            [std.lib :as h]))

(def +features+
  (-> (grammer/build-min [:top-declare
                          :coroutine
                          :macro
                          :macro-arrow
                          :macro-let])))

(def +grammer+
  (grammer/grammer :xt
    (grammer/to-reserved +features+)
    (emit/default-grammer
     {:banned #{:keyword}
      :allow   {:assign  #{:symbol :vector :set}}})))

(def +meta+ (book/book-meta {}))

(def +book+
  (book/book {:lang :xtalk
              :meta +meta+
              :grammer +grammer+}))

(def +init+
  (script/install +book+))
