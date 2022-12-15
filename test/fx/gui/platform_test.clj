(ns fx.gui.platform-test
  (:use code.test)
  (:require [fx.gui.platform :refer :all]))

^{:refer fx.gui.platform/init! :added "3.0" :unit #{:gui}}
(fact "initialises the fx platform"

  (init!))

^{:refer fx.gui.platform/callback :added "3.0" :unit #{:gui}}
(fact "constructs a callback"

  (callback (fn [cb] cb))
  => javafx.util.Callback)

^{:refer fx.gui.platform/run-fx :added "3.0" :unit #{:gui}}
(fact "constructs a fx call to run later"

  (run-fx (fn [] (+ 1 2 3)))
  => nil)

^{:refer fx.gui.platform/call-fx :added "3.0" :unit #{:gui}}
(fact "constructs a fx call"

  (call-fx (fn [] (+ 1 2 3)))
  => nil)

^{:refer fx.gui.platform/return-fx :added "3.0" :unit #{:gui}}
(fact "constructs a fx call, returning a result"

  (return-fx (fn [] (+ 1 2 3)))
  => 6)

^{:refer fx.gui.platform/wrap-fx :added "3.0" :unit #{:gui}}
(fact "wraps a function to construct an fx call"

  ((wrap-fx (fn [a b] (+ a b))) 5 6)
  => 11)

^{:refer fx.gui.platform/do-fx :added "3.0" :unit #{:gui}}
(fact "provides a convenience macro for ensuring fx-thread is called"

  (do-fx (+ 1 2 3)) => 6)

^{:refer fx.gui.platform/event-handler :added "3.0" :unit #{:gui}}
(fact "constructs an event handler"

  (event-handler (fn [_]))
  => javafx.event.EventHandler)

^{:refer fx.gui.platform/event-handler-object :added "3.0" :unit #{:gui}}
(fact "constructs an event handler"

  (event-handler-object nil (fn [this _]))
  => javafx.event.EventHandler)

^{:refer fx.gui.platform/change-listener :added "3.0" :unit #{:gui}}
(fact "constructs a change listener"

  (change-listener (fn [prop old new]))
  => javafx.beans.value.ChangeListener)

^{:refer fx.gui.platform/task :added "3.0" :unit #{:gui}}
(fact "constructs a task"

  (task (fn []))
  => javafx.concurrent.Task)

(comment
  (popup {} [(button {} ["A"])])

  (display [:vbox {:min-width 200}
            [:button "A"]
            [:button "B"]
            [:choice-box {:id "form/race"} "Roman" "Greek" "Frank"]
            [:label {:bind {:text ["form/race" :value]}}
             "hello"]])

  (def s (interop/compile [:stage {:title "hello world"} [:scene]]))
  (.setScene s (scene))
  (.setMinHeight s 100)
  (->> (.? s Void/TYPE :method :public :name)
       (map case/spear-case))
  (doto (fx.gui.control/choice-box {})
    (-> (.getItems)
        (.addAll (into-array String ["A" "B" "C"]))))

  (call-fx (fn [] (.show s)))
  (call-fx (fn [] (.requestFocus s))))

(comment
  (popup {:x 1})

  (object/from-data {} Scene)
  (interop/compile [:button "hello"])
  (def s (interop/compile [:stage [:scene [:vbox {:min-width 100}
                                           [:button "hello"]
                                           [:button "world"]]]]))

  (platform/call-fx (fn [] (.show s)))
  (platform/call-fx (fn [] (.requestFocus s)))

  (interop/compile [:scene])
  (file-chooser {:title "hello there"})

  (def stg (stage {:title "hello there"}))
  (platform/call-fx (fn [] (.show stg)))
  (platform/call-fx (fn [] (.requestFocus stg)))
  (stage {})
  (popup {:x 10 :y 20} [(javafx.scene.layout.VBox.)])
  (scene)

  ((meta #'directory-chooser))
  (meta #'popup)

  (interop/get-properties (popup {:x 100 :y 200}))
  (popup)
  ((:y (interop/all-properties-cached-fn (popup {:x 100 :y 200})))
   (doto (popup {:x 100 :y 200})
     (.setY 200))))
