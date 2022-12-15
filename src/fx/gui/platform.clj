(ns fx.gui.platform
  (:require [std.lib :as h])
  (:import (javafx.application Platform
                               Preloader)
           (javafx.beans.value ChangeListener)
           (javafx.event EventHandler)
           (javafx.util Callback)
           (javafx.concurrent Task)))

(defn init!
  "initialises the fx platform
 
   (init!)"
  {:added "3.0"}
  ([]
   (do (try (Platform/startup #())
            (catch IllegalStateException t))
       (Platform/setImplicitExit false)
       true)))

(defonce +init+ (if (h/dev?) (init!)))

(defn callback
  "constructs a callback
 
   (callback (fn [cb] cb))
   => javafx.util.Callback"
  {:added "3.0"}
  ([f]
   (reify Callback
     (call [this e] (f e)))))

(defn run-fx
  "constructs a fx call to run later
 
   (run-fx (fn [] (+ 1 2 3)))
   => nil"
  {:added "3.0"}
  ([f]
   (Platform/runLater f)))

(defn call-fx
  "constructs a fx call
 
   (call-fx (fn [] (+ 1 2 3)))
   => nil"
  {:added "3.0"}
  ([f]
   (if (Platform/isFxApplicationThread)
     (f)
     (Platform/runLater f))))

(defn return-fx
  "constructs a fx call, returning a result
 
   (return-fx (fn [] (+ 1 2 3)))
   => 6"
  {:added "3.0"}
  ([f]
   (if (Platform/isFxApplicationThread)
     (f)
     (let [p (promise)]
       (Platform/runLater (fn [] (deliver p
                                          (try (f)
                                               (catch Throwable t t)))))
       @p))))

(defn wrap-fx
  "wraps a function to construct an fx call
 
   ((wrap-fx (fn [a b] (+ a b))) 5 6)
   => 11"
  {:added "3.0"}
  ([f]
   (fn [& args]
     (return-fx (fn [] (apply f args))))))

(defmacro do-fx
  "provides a convenience macro for ensuring fx-thread is called
 
   (do-fx (+ 1 2 3)) => 6"
  {:added "3.0"}
  ([& body]
   `(return-fx (fn [] ~@body))))

(defn event-handler
  "constructs an event handler
 
   (event-handler (fn [_]))
   => javafx.event.EventHandler"
  {:added "3.0"}
  ([f]
   (reify EventHandler
     (handle [_ event] (f event)))))

(defn event-handler-object
  "constructs an event handler
 
   (event-handler-object nil (fn [this _]))
   => javafx.event.EventHandler"
  {:added "3.0"}
  ([this f]
   (reify EventHandler
     (handle [_ event] (f this event)))))

(defn change-listener
  "constructs a change listener
 
   (change-listener (fn [prop old new]))
   => javafx.beans.value.ChangeListener"
  {:added "3.0"}
  ([f]
   (reify ChangeListener
     (changed [_ prop old new] (f prop old new)))))

(defn task
  "constructs a task
 
   (task (fn []))
   => javafx.concurrent.Task"
  {:added "3.0"}
  ([f]
   (proxy [Task] []
     (call [] (f)))))
