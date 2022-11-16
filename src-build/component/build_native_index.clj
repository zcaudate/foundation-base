(ns component.build-native-index
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]
            [std.make :as make :refer [def.make]]
            [pune.project-web :as project-web]
            [component.web-native-index :as web-native-index]))

;;
;; NGINX CONF
;;
  
(def.make COMPONENT-NATIVE
  {:tag      "native"
   :build    ".build/native"
   :github   {:repo   "zcaudate/component.native"
              :description "Native components"}
   :sections {:common [project-web/+expo-makefile+]
              :node   [{:type :gitignore,
                        :main '[dist
                                node_modules
                                yarn.lock
                                pnpm-lock.yaml
                                bundle.js]}
                       {:type :gitignore,
                        :main
                        ["node_modules/**/*"
                         ".expo/*"
                         "npm-debug.*"
                         "*.jks"
                         "*.p8"
                         "*.p12"
                         "*.key"
                         "*.mobileprovision"
                         "*.orig.*"
                         "web-build/"
                         ".DS_Store"
                         "yarn.lock"
                         "yarn-error.log"]}
                       
                       (project-web/expo-app-json "component-native",)
                       {:type :package.json,
                        :main
                        {"main" "node_modules/expo/AppEntry.js",
                         "name" "component-native",
                         "scripts"
                         {"start" "expo start",
                          "android" "expo start --android",
                          "ios" "expo start --ios",
                          "web" "expo start --web",
                          "eject" "expo eject"},
                         "private" true,
                         "dependencies"
                         {"react-native-svg" "12.3.0",
                          "javascript-time-ago" "2.3.11",
                          "react-native" "0.68.1",
                          "expo-media-library" "14.1.0",
                          "expo-web-browser" "10.2.1",
                          "uuid" "8.3.2",
                          "react-dom" "17.0.2",
                          "lightweight-charts" "3.8.0",
                          "dateformat" "^4",
                          "react-native-vector-icons" "9.1.0",
                          "react-native-web" "0.17.7",
                          "react-native-error-boundary" "1.1.10",
                          "base-64" "1.0.0",
                          "react-native-base64" "0.1.0",
                          "react" "17.0.2",
                          "react-native-get-random-values" "1.8.0",
                          "expo-random" "12.2.0"},
                         "devDependencies"
                         {"@babel/core" "7.9.0",
                          "@babel/preset-env" "7.13.15",
                          "@types/react" "16.9.35",
                          "@types/react-native" "0.63.2",
                          "expo" "45.0.3",
                          "expo-cli" "6.0.8",
                          "typescript" "4.3.5"},
                         "metro" {"watchFolders" ["assets"]}}}
                       {:type :resource,
                        :target "assets",
                        :main
                        [["core-assets/demo/adaptive-icon.png"
                          "adaptive-icon.png"]
                         ["core-assets/demo/favicon.png" "favicon.png"]
                         ["core-assets/demo/icon.png" "icon.png"]
                         ["core-assets/demo/snack-icon.png" "snack-icon.png"]
                         ["core-assets/demo/splash.png" "splash.png"]]}]}
   :default [{:type   :module.graph
              :lang   :js
              :target "src"
              :main   'component.web-native-index
              :emit   {:code   {:label true}}}]})

(comment
  (do (make/build-all COMPONENT-NATIVE)
      (make/gh:dwim-init COMPONENT-NATIVE))
  (make/gh:dwim-init COMPONENT-NATIVE)
  (def *res*
    (future (make/run-internal COMPONENT-NATIVE :build-web)))
  )
