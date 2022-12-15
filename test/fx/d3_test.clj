(ns fx.d3-test
  (:use code.test)
  (:require [fx.d3 :refer :all]
            [std.lib :as h]))

^{:refer fx.d3/d3:download :added "4.0"}
(fact "downloads the d3 script")

^{:refer fx.d3/d3:create :added "4.0"}
(fact "creates a d3 webview instance")

^{:refer fx.d3/get-d3 :added "4.0"}
(fact "gets or creates a d3 webview instance")

(comment
  
  (!.js
   (const y (. (d3.scaleLinear)
               (domain [0 100])
               (range [0 640]))))
  
  (defrun.js __setup__
   (const y (d3/scaleLinear
             {:domain [0 100]
              :range [0 640]})))
  
  
  
  (get-d3)
  
  (d3:download)
  (h/res:stop :hara/fx.d3)
  (def -d3- (get-d3))
  
  (h/pl +init-d3+)
  (h/req -d3- {:op :exec :body (lang/ptr:str __setup__)})
  
  (h/req -d3- {:op :exec :body  (!.js (y 50))})
  
  )
