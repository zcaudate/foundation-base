(ns lua.nginx.mail
  (:require [std.lang :as l]
            [std.lib :as h])
  (:refer-clojure :exclude [send]))

(l/script :lua
  {:bundle  {:default [["resty.mail" :as ngxmail]]}
   :import  [["resty.mail" :as ngxmail]]
   :export  [MODULE]})

(h/template-entries [l/tmpl-macro {:base "mail"
                                   :inst "mailer"
                                   :tag "lua"}]
  [[send   [m]]])

(def.lua MODULE (!:module))
