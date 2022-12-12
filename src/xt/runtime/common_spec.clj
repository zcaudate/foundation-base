(ns xt.runtime.common-spec
  (:require [std.lib :as h]
            [std.lang :as l]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export  [MODULE]})

(def +metatypes+
  {:runtime    {:var {:trigger {}
                      :watch   {}}}
   :iterator   {}
   :promise    {}
   :stream     {}
   :code       {:symbol  {}
                :keyword {}
                :syntax  {}}
   :collection {:list    {}
                :vector  {}
                :hashmap {}
                :hashset {}}
   :executor   {}})
