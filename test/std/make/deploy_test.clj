(ns std.make.deploy-test
  (:use code.test)
  (:require [std.make.deploy :refer :all]))

^{:refer std.make.deploy/make-deploy-get-keys :added "4.0"}
(fact "deploy get keys")

^{:refer std.make.deploy/make-deploy :added "4.0"}
(fact "make deploy")

^{:refer std.make.deploy/make-deploy-gh-init :added "4.0"}
(fact "make deploy init github")

^{:refer std.make.deploy/make-deploy-gh-push :added "4.0"}
(fact "make deploy push github")
