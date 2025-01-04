(ns meta.gen-modules)

(comment
  (->> (std.string/split-lines (slurp "modules.txt"))
       (filter (fn [s]
                 (or (re-find #"^jdk" s)
                     (re-find #"^java" s))))
       (map (fn [s]
              (format "\"--add-opens\" \"java.base/%s=ALL-UNNAMED\""
                      s)))
       (std.string/join "\n")
       (spit "modules-add.txt"))

  (->> (std.string/split-lines (slurp "modules.txt"))
       (filter (fn [s]
                 (or (re-find #"^jdk" s)
                     (re-find #"^java" s))))
       (map (fn [s]
              (format "\"--add-opens\" \"java.desktop/%s=ALL-UNNAMED\""
                      s)))
       (std.string/join "\n")
       (spit "modules-add.txt"))
  
  (reflect/query-class )
  (std.object.query/all-class-members URLClassLoader))
