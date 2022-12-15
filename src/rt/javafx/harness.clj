(ns rt.javafx.harness
  (:require [std.lang :as l]
            [std.lib :as h :refer [defimpl]]
            [std.json :as json]))

(l/script :js
  {:require [[js.core.dom :as dom]
             [xt.lang.base-lib :as k]
             [js.react :as r :include [:dom]]]})

(defn.js TestComponent
  "creates a test component"
  {:added "4.0"}
  ([props]
   (let [[val setVal] (r/local (k/random))]
     (return
      [:div [:h1 (+ "HELLO " val)]
       [:button {:onClick (fn []
                            (setVal (k/random)))}
        "CHANGE"]]))))

(defn.js hasRoot
  "checks that there is a root div"
  {:added "4.0"}
  []
  (return (not (not (dom/id "root")))))

(defn.js teardownRoot
  "removes the root div"
  {:added "4.0"}
  []
  (let [root (dom/id "root")
        _     (if root (dom/remove root))]))

(defn.js setupRoot
  "creates the root div"
  {:added "4.0"}
  []
  (if (not (-/hasRoot))
    (->> (dom/create [:div {:id "root"} "root"])
         (dom/appendChild (dom/body))))
  (return (dom/id "root")))

(defn.js resetRoot
  "does teardown and setup"
  {:added "4.0"}
  []
  (-/teardownRoot)
  (-/setupRoot))

(defn.js Harness
  "creates the root harness"
  {:added "4.0"}
  ([props]
   (r/init []
     (:= (.  window ["attached"]) true))
   (return [:% r/Fragment {}
            props.children])))

(defn.js attachRoot
  "attaches the root div"
  {:added "4.0"}
  [App]
  (if (not (-/hasRoot))
    (-/setupRoot))
  (r/renderDom [:% App]
               (dom/id "root")))

(defn.js attachTestComponent
  "attaches the test component"
  {:added "4.0"}
  ([]
   (-/attachRoot -/TestComponent)))

;;
;;
;;

(comment

  (setupRoot)
  (hasRoot)
  )
