(ns js.cell.playground
  (:require [std.lib :as h]
            [std.lang :as l]
            [std.fs :as fs]
            [std.html :as html]
            [std.string :as str]
            [js.core :as j]
            [js.cell :as cl]
            [js.cell.base-internal]))

(defonce ^:dynamic *current* (atom nil))

(def +index+
  (html/html [:html
              [:head
               [:meta {:http-equiv "Content-Security-Policy"
                       :content (str/join ";"
                                          ["default-src * 'unsafe-eval'"
                                           "connect-src * 'unsafe-eval'"
                                           "script-src  'self' 'unsafe-eval' 'unsafe-inline"
                                           "worker-src  data: 'self' 'unsafe-eval' 'unsafe-inline"])}]]]))

(defn start-playground
  "starts the playground"
  {:added "4.0"}
  []
  (or @*current*
      (reset! *current*
              (let [root (str (fs/create-tmpdir))
                    _    (spit (str root "/index.html") +index+)
                    port (h/port:check-available 0)
                    process (h/sh "http-server" "-p" (str port) {:root root
                                                                 :wait false})
                    _ (h/wait-for-port "localhost" port)]
                {:root root
                 :port port
                 :process process}))))

(defn stop-playground
  "stops the playground"
  {:added "4.0"}
  []
  (when @*current*
    (h/swap-return! *current*
      (fn [{:keys [process] :as m}]
        (h/sh-close process)
        [m nil]))))

(defn play-file
  "gets the file path in playground"
  {:added "4.0"}
  [& paths]
  (str/join "/" (cons (:root (start-playground)) paths)))

(defn play-url
  "gets the playground url"
  {:added "4.0"}
  [& [url]]
  (str "http://127.0.0.1:" (:port (start-playground)) "/" url))

(defn play-script
  "gets the script"
  {:added "4.0"}
  [forms & [as-script layout]]
  (cond as-script
        (l/emit-script (cons 'do forms) {:lang :js
                                         :layout (or layout :flat)})
        
        :else
        (let [script (l/emit-script (cons 'do forms) {:lang :js
                                                      :layout (or layout :flat)})
              sha (h/sha1 script)
              {:keys [root]} (start-playground)
              filename (str root "/" sha ".js")
              _ (when (not (fs/exists? filename))
                  (spit filename script))]
          (str sha ".js"))))

(defn play-worker
  "constructs the play worker"
  {:added "4.0"}
  [& [as-script]]
  (play-script '[(js.cell.base-fn/routes-init {})
                 (js.cell.base-internal/worker-init self)
                 (js.cell.base-internal/worker-init-post self {:done true})]
               as-script))

(defn play-files
  "copies files to the playground"
  {:added "4.0"}
  [files]
  (let [{:keys [root]} (start-playground)]
    (doall (for [[dst src] files]
             (let [out (str root "/" dst)]
               (fs/copy src out {:options [:replace-existing]})
               [out src])))))
