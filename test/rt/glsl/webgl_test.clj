(ns rt.glsl.webgl-test
  (:use code.test)
  (:require [rt.glsl.webgl :refer :all]))

^{:refer rt.glsl.webgl/fetch-html :added "4.0"}
(fact "fetches the webgl spec")

^{:refer rt.glsl.webgl/get-html :added "4.0"}
(fact "reads webgl spec or fetches it")

^{:refer rt.glsl.webgl/build-edn :added "4.0"}
(fact "builds the edn representation")

^{:refer rt.glsl.webgl/get-edn :added "4.0"}
(fact "gets the edn representation")

^{:refer rt.glsl.webgl/clean :added "4.0"}
(fact "cleans all files")

^{:refer rt.glsl.webgl/create :added "4.0"}
(fact "creates the edn from blank dir")
