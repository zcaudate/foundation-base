(ns rt.basic.type-container-test
  (:use code.test)
  (:require [rt.basic.type-container :refer :all]
            [std.lib :as h]
            [std.lang :as l]))

^{:refer rt.basic.type-container/start-container-process :added "4.0"}
(fact "starts the container"
  ^:hidden
  
  (h/suppress
   (start-container-process :python
                            {:group "test"
                             :image "python"
                             :runtime :basic
                             :exec ["python" "-c"]
                             :bootstrap (fn [port opts]
                                          "1+1")}
                            0
                            {:lang :python
                             :tag :basic
                             :module (h/ns-sym)}))
  => (any nil? map?))

^{:refer rt.basic.type-container/start-container :added "4.0"}
(fact "starts a container"
  ^:hidden
  
  (h/suppress
   (start-container :python
                    {:image "python"}
                    0
                    {:lang :python
                     :runtime :basic
                     :tag :basic
                     :module (h/ns-sym)}))
  => (any nil? map?))

^{:refer rt.basic.type-container/stop-container :added "4.0"}
(fact "stops a container")
