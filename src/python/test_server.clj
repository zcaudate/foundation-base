(ns python.test-server
  (:require [std.lib :as h]
            [std.lang :as l]))

(l/script :python
  {:runtime :basic
   :require [[python.remote-port-server :as ss]]})



(comment
  (!.py
   (+ 1 2 3))
  (h/pp {:a 1})
  ^*(!.py
     (ss/start-async 12677))
  
  (./create-tests))
