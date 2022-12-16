(ns play.tui-005-draw.element-castle
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core :as j]
              [js.react :as r]]
   :export   [MODULE]})

(defn.js Castle
  ([]
   (return
    [:box {:left "center"
           :bg "black"
           :width 36
           :content
           (j/join 
            ["                |>>>"
             "                |"
             "            _  _|_  _"
             "           |;|_|;|_|;|"
             "           \\\\.    .  /"
             "            \\\\:  .  /"
             "             ||:   |"
             "             ||:.  |"
             "             ||:  .|"
             "             ||:   |       \\,/"
             "             ||: , |            /`\\"
             "             ||:   |"
             "             ||: . |"
             "            _||_   |"
             "    __ ----~    ~`---,              __"
             "--~'                  ~~----_____-~'"]
            "\n")}])))

(def.js MODULE (!:module))
