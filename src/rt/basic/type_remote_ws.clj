(ns rt.basic.type-remote-ws
  (:require [std.protocol.context :as protocol.context]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.runtime :as default]
            [std.lib :as h :refer [defimpl]]
            [std.json :as json]
            [std.concurrent :as cc]
            [std.string :as str]
            [net.http.websocket :as ws]))
