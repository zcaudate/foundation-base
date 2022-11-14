(ns std.make.makefile
  (:require [std.string :as str]
            [std.lib :as h]))

(def ^:dynamic *indent* 0)

(defn- block?
  ([v]
   (or (map? v)
       (and (vector? v)
            (vector? (first v))))))

(defn- nested-block?
  ([v]
   (and (vector? v)
        (block? (last v)))))

(defn emit-headers
  "emits makefile headers
 
   (emit-headers {:CC :gcc})
   => \"CC = gcc\""
  {:added "4.0"}
  ([m]
   (->> (map (fn [[k v]]
               (h/strn k " = " (str/write-line v)))
             m)
        (str/join "\n"))))

(defn emit-target
  "emits all makefile targets"
  {:added "4.0"}
  ([[tag & more]]
   (let [[deps commands] (if (map? (first more))
                           [(:- (first more)) (rest more)]
                           [nil more])]
     (str (h/strn tag ":")
          (if deps (str " " (str/write-line deps))) "\n\t"
          (str/join "\n\t" (map str/write-line commands))))))

(defn write
  "link to `std.lang.compile/compile-ext-fn`"
  {:added "4.0"}
  ([v]
   (let [[headers & targets] (if (map? (first v))
                               v
                               (cons nil v))]
     (str (when headers
            (str (emit-headers headers) "\n\n"))
          (->> (map emit-target targets)
               (str/join "\n\n"))))))

(comment

  (h/pl (write
       [{:CC     :gcc
         :CFLAGS "-I."
         :DEPS   ["hellomake.h"]
         :OBJ    ["hellomake.h"]}
        [:.PHONY {:- "clean"}]
        [:clean
         '[rm -f "$(ODIR)/*.o" *- core "$(INCDIR)/*~"]]])))
