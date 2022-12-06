^{:no-test true}
(ns component.task-native-index
  (:require
   [std.lang :as l]
   [std.lib :as h]
   [std.string :as str]
   [std.make :as make]
   [std.lib.link :as link]
   [component.build-native-index :as build-native-index]))

(defn task-gh-push
  [& [message]]
  (make/gh:dwim-push build-native-index/COMPONENT-NATIVE message))

(defn task-gh-init
  [& [message]]
  (make/with-verbose false
    (make/gh:dwim-init build-native-index/COMPONENT-NATIVE message)))

(defn -main
  []
  (try
    (h/p "")
    (h/p "********************************************************************")
    (h/p "********************************************************************")
    (h/p "")
    (h/p "GITHUB PUSH")
    (h/p "")
    (h/p "********************************************************************")
    (h/p "********************************************************************")
    (task-gh-init)
    (catch Throwable t
      (prn t)
      (System/exit 1)))
  
  (System/exit 0))

(comment)
