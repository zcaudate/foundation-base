(ns js.react-native.helper-browser
  (:require [std.lang :as  l]
            [std.lib :as h]))

(l/script :js
  {:require [[xt.lang.base-lib :as k]
             [xt.lang.event-route :as event-route]
             [js.react :as r]
             [js.react-native :as n]
             [js.react.ext-route :as ext-route]]
   :export [MODULE]})

(defn.js getHash
  []
  (when (and window
             window.location)
    (return window.location.hash)))

(defn.js getHashRoute
  "gets the browser hash route"
  {:added "4.0"}
  []
  (var hash (-/getHash))
  (return (:? hash
              (k/substring hash
                           2)
              "")))

(defn.js useHashRoute
  "listens to the browser hash route"
  {:added "4.0"}
  [route]
  (var [routeUrl setRouteUrl] (ext-route/useRouteUrl route))
  (r/watch [routeUrl]
    (when (and (k/not-nil? routeUrl)
               (n/isWeb))
      (:= window.location.hash
          (+ "/" routeUrl))))
  (r/init []
    (var listener (fn []
                    (var hash (-/getHash))
                    (when (and (k/not-nil? hash)
                               (not= hash
                                     (+ "#/" (event-route/get-url route))))
                      #_(k/LOG! #{hash})
                      (setRouteUrl (-/getHashRoute)))))
    (when (n/isWeb)
      (window.addEventListener "popstate" listener)
      (return (fn:> (window.removeEventListener
                     "popstate"
                     listener))))))

(def.js MODULE (!:module))
