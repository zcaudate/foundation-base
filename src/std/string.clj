(ns std.string
  (:require [std.string.coerce]
            [std.string.common]
            [std.string.case]
            [std.string.path]
            [std.string.prose]
            [std.string.wrap :as wrap]
            [std.lib.foundation :as h])
  (:refer-clojure :exclude [reverse replace]))

(h/intern-all std.string.common
              std.string.case
              std.string.path
              std.string.coerce
              std.string.prose)

(h/intern-in wrap/wrap)

(defn |
  "shortcut for join lines
 
   (| \"abc\" \"def\")
   => \"abc\\ndef\""
  {:added "3.0"}
  [& args]
  (join "\n" args))

(defn lines
  "transforms string to seperated newlines
 
   (lines \"abc\\ndef\")
   => '(std.string/| \"abc\" \"def\")"
  {:added "3.0"}
  [s]
  (cons `| (split-lines s)))
