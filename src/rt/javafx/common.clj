(ns rt.javafx.common
  (:require [fx.gui :as fx]
            [std.concurrent :as cc]
            [std.lib :as h]
            [std.html :as html]
            [std.json :as json]
            [std.string :as str]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.impl :as impl]
            [xt.lang.base-repl :as k]
            [rt.basic.impl.process-js :as process])
  (:import (java.util.concurrent CompletableFuture)
           (javafx.stage Stage)
           (javafx.scene.web WebEngine
                             WebView)
           (com.sun.webkit.dom JSObject)))

(defonce ^:dynamic *lu*
  (atom {:by-id       {}
         :by-instance {}}))

(def +path+ "assets/rt.javafx")

(def +url+ "https://cdn.jsdelivr.net/npm/")

(def +files+ [[nil
               "harness.js"        :harness]
              [nil
               "harness-new.js"    :harness.new]
              ["lodash@4.17.21/lodash.min.js"
               "lodash.min.js"     :lodash]
              ["react@17.0.2/umd/react.development.js"
               "react.js"          :react]
              ["react-dom@16.13.1/umd/react-dom.development.js"
               "react-dom.js"      :react-dom]
              ["react@17.0.2/umd/react.min.js"
               "react.min.js"      :react.min]
              ["react-dom@16.13.1/umd/react-dom.min.js"
               "react-dom.min.js"  :react-dom.min]
              [nil
               "uuid.js"           :uuid]
              [nil
               "valtio-core.js"    :valtio]])

(def +content+
  (html/html [:html
              [:head
               [:meta {:http-equiv "Content-Security-Policy"
                       :content (str/join ";"
                                          ["default-src 'unsafe-eval'"
                                           "connect-src * 'unsafe-eval'"])}]]
              [:body]]))

(def +bootstrap+
  (impl/emit-entry-deps
                    k/return-eval
                    {:lang :js
                     :layout :flat}))

(defn download-assets
  "downloads lodash, react and react dom"
  {:added "4.0"}
  ([]
   (h/on:all
    (map (fn [[path file]]
           (if path
             (h/sh "wget" (str +url+ path) "-O" file
                   {:async true
                    :root (str "resources/" +path+)})))
         +files+))))


(defn ^WebEngine get-engine
  "gets the webengine"
  {:added "4.0"}
  ([^WebView instance]
   (.getEngine instance)))

(defprotocol IExecute
  (instance-exec-raw [ctx script])
  (instance-exec [ctx script])
  (instance-load [ctx page]))

(extend-protocol IExecute
  WebEngine
  (instance-exec-raw [ctx script]
    (.executeScript ctx script))
  
  (instance-exec [ctx script]
    (fx/do-fx
     (try
       (str (.executeScript ctx script))
       (catch Throwable t))))

  (instance-load [ctx page]
    (fx/do-fx
     (.loadContent ctx page)))

  WebView
  (instance-exec-raw [ctx script] (instance-exec-raw (get-engine ctx) script))
  (instance-exec [ctx script] (instance-exec (get-engine ctx) script))
  (instance-load [ctx page]   (instance-load (get-engine ctx) page)))

(defn instance-init
  "initialises the webview"
  {:added "4.0"}
  ([instance preload]
   (instance-load instance +content+)
   (instance-exec instance +bootstrap+)
   (doseq [[_ file key] +files+]
     (if (preload key)
       (instance-exec instance
                      (slurp (h/sys:resource
                              (str "assets/rt.javafx/" file))))))))

(defn instance-list
  "lists all instances"
  {:added "4.0"}
  ([]
   (keys (:by-id  @*lu*))))

(defn instance-get
  "gets an instance by id"
  {:added "4.0"}
  ([id]
   (get-in @*lu* [:by-id id])))

(defn instance-display
  "displays the webview in javafx stage"
  {:added "4.0"}
  ([instance {:keys [id title callback minimal]
              :or {id (h/sid)}}]
   (or (let [rid (get-in @*lu* [:by-instance instance])]
         (or (instance-get rid)
             (instance-get id)))
       (fx/do-fx
        (let [stage (doto (fx/fx-prepare instance)
                      (.setTitle (or title id))
                      (.setWidth 300)
                      (.setHeight (if minimal 50 600))
                      (.setX 0)
                      (.setY 100)
                      (.show)
                      (.requestFocus)
                      (fx/add-close-hook
                       (fn [_]
                         (swap! *lu* (fn [m]
                                       (-> m
                                           (update :by-id dissoc id)
                                           (update :by-instance dissoc instance))))
                         (if (:stop callback) ((:stop callback))))))]
          (swap! *lu* (fn [m]
                        (-> m
                            (update :by-id assoc id {:stage stage
                                                     :instance instance})
                            (update :by-instance assoc instance id))))
          (if (:start callback) ((:start callback)))
          {:stage stage
           :instance instance})))))

(defn instance-start
  "starts the instance"
  {:added "4.0"}
  ([]
   (instance-start {}))
  ([{:keys [display id scripts callback preload] :as opts}]
   (let [instance (fx/do-fx (WebView.))
         _ (instance-init instance (set preload))
         _ (if (not (= display false))
             (instance-display instance opts))
         _ (doseq [url scripts]
             (instance-exec instance (slurp url)))]
     instance)))

(defn instance-stop
  "stops the instance"
  {:added "4.0"}
  ([instance]
   (instance-stop instance {}))
  ([instance callback]
   (let [id   (get-in @*lu* [:by-instance instance])
         {:keys [stage]} (instance-get id)]
     (when stage
       (fx/do-fx (.close ^Stage stage)
                 (swap! *lu* (fn [m]
                               (-> m
                                   (update :by-id dissoc id)
                                   (update :by-instance dissoc instance))))
                 (if (:stop callback) ((:stop callback))))))))


(comment

  (def -web- (instance-start {:display true}))
  
  (instance-exec -web- "'hello'")
  
  (instance-exec -web- "RETURN_EVAL('dhtd')")
  

  
  (instance-load -web-
                 "<button onclick='logPrint.invoke()'>Hello</button>")
  (fx/do-fx
   (let [window (instance-exec-raw -web- "window")
         _ (.setMember window "logPrint" logPrint)
         _ (.setMember window "createPromise" createPromise)]
     (str window)))

  @(fx/do-fx (instance-exec-raw -web- "p = createPromise.invoke(); p.obtrudeValue(3); p"))
  
  (h/on:complete)
  (defn createPromise
    []
    (h/incomplete))

  (.? (h/incomplete))
  (defn logPrint []
    (h/prn :Hello)))
  
  
(comment
  (fx/do-fx
   (doto (:stage (val (first (:by-id @*lu*))))
     (.setWidth 300)
     (.setHeight 600)
     (.setX 0)
     (.setY 100))))
