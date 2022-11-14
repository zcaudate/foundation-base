(ns js.react.ext-route-test
  (:use code.test)
  (:require [js.react.ext-route :refer :all]))

^{:refer js.react.ext-route/makeRoute :added "4.0"}
(fact "makes a react compatible route")

^{:refer js.react.ext-route/listenRouteTree :added "4.0"}
(fact "listens for changes on the route tree")

^{:refer js.react.ext-route/listenRouteUrl :added "4.0"}
(fact "listens for all changes on the route url")

^{:refer js.react.ext-route/useRouteUrl :added "4.0"}
(fact "getter and setter for route url")

^{:refer js.react.ext-route/listenRouteSegment :added "4.0"}
(fact "listens for changes on a route segment")

^{:refer js.react.ext-route/useRouteSegment :added "4.0"}
(fact "getter and setter for route segment")

^{:refer js.react.ext-route/listenRouteParam :added "4.0"}
(fact "listens for changes on a route param")

^{:refer js.react.ext-route/useRouteParam :added "4.0"}
(fact "getter and setter for route param")

^{:refer js.react.ext-route/useRouteParamFlag :added "4.0"}
(fact "binary flag for route param")
