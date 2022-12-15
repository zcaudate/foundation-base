(ns fx.gui.data.return
  (:require [fx.gui.data.state :as common]
            [std.fs :as fs]
            [std.lib :as h]))

(def ^:dynamic *namespaces*
  {:image  'fx.gui.data.common
   :html   'fx.gui.data.common})

(def ^:dynamic *formats*
  {:svg   :string/html
   :html  :string/html
   :png   :bytes/image
   :jpeg  :bytes/image
   :gif   :bytes/image})

(defn- process-convert
  ([output format]
   (let [convert (keyword (namespace format))
         tag  (keyword (name format))]
     [tag output])))

(defn process-output
  "processes output bytes from process"
  {:added "3.0"}
  ([^bytes output {:keys [type title save size] :as settings}]
   (if (and save output)
     (fs/write-all-bytes (fs/path save) output))
   (if (and title type)
     (if-let [format (get *formats* type)]
       (let [[tag output] (process-convert output format)
             _ (h/require (get *namespaces* tag))]
         ((common/output-fn tag) output title size)))
     output)))

