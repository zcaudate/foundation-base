(ns std.dispatch.core-test
  (:use code.test)
  (:require [std.dispatch.core :refer :all]
            [std.dispatch.common :as common]
            [std.concurrent :as cc]
            [std.lib.component :as component]))

(defonce ^:dynamic *output*  (atom []))

(defonce ^:dynamic *dispatch* (atom nil))

(def +test-config+
  {:type :core
   :handler (fn [loop entries])
   :hooks   {:on-startup (fn [_]
                           (reset! *output* []))
             :on-process (fn [_ entry]
                           (swap! *output* conj entry))}
   :options {:pool  {:size 2}}})

(defn test-scaffold [config times sleep]
  (component/with [main-fn (create-dispatch config)]
                  (reset! *dispatch* main-fn)
                  (do (dotimes [i times]
                        (main-fn i))

                      (Thread/sleep sleep)
                      @*output*)))

^{:refer std.dispatch.core/submit-dispatch :added "3.0"}
(fact "submits to the core dispatch")

^{:refer std.dispatch.core/create-dispatch :added "3.0"}
(fact "creates a core dispatch"

  ;; POOL SIZE 2, NO SLEEP
  (test-scaffold +test-config+ 100 10)
  => #(-> % count (= 100))

  ;; POOL SIZE 2, WITH SLEEP
  (test-scaffold (assoc +test-config+
                        :handler (fn [_ _] (Thread/sleep 200)))
                 5
                 100)
  => #(-> % count (= 2))

  ;; POOL SIZE 50, WITH SLEEP
  (test-scaffold (-> +test-config+
                     (assoc :handler (fn [_ _] (Thread/sleep 200)))
                     (assoc-in [:options :pool :size] 50))
                 80
                 30)
  => #(-> % count (>= 50)))

(comment
  (code.manage/import)

  (cc/shutdown @(:raw (:runtime -exe-)))

  (def -exe- (create-dispatch {:options {:pool {:size 10
                                                :max 10
                                                :keep-alive 100}
                                         :queue {:size 4}}
                               :handler (fn [loop event]
                                          (Thread/sleep 1000))}))

  (submit-dispatch -exe- 1)

  (def +hooks+ {:on-startup (fn [_] (prn :ON-STARTUP))
                :on-shutdown (fn [_] (prn :ON-SHUTDOWN))
                :on-submit (fn [_ _] (prn :ON-SUBMIT))
                :on-queued   (fn [_ e] (prn :ON-QUEUED e))
                :on-process  (fn [_ e] (prn :ON-PROCESS e))
                :on-error    (fn [_ _ t] (prn :ON-ERROR t))
                :on-complete (fn [_ _ t] (prn :ON-COMPLETE t))})
  (./import)
  (submit-dispatch -exe- "HELLO")

  (def -exe- (->
              (common/start-dispatch))))
