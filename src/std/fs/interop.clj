(ns std.fs.interop
  (:require [clojure.java.io]
            [std.fs.path :as path])
  (:import (java.nio.file Path)))

(extend Path
  clojure.java.io/IOFactory
  (assoc clojure.java.io/default-streams-impl
         :make-input-stream  (fn [path opts] (path/input-stream path))
         :make-output-stream (fn [path opts] (path/output-stream path))))
