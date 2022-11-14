(ns rt.jocl.runtime-test
  (:use code.test)
  (:require [rt.jocl.runtime :refer :all]
            [rt.jocl.exec :as exec]
            [std.lang :as  l]
            [std.lib :as h]))

(l/script- :c)

(define.c MUL= [c a b i] (:= (. c [i]) (* (. a [i]) (. b [i]))))

(define.c G0  (get-global-id 0))

(defn.c ^{:- [:__kernel :void]
          :rt/kernel {:worksize '(fn [{:keys [a]}] [(count a)])}}
  sample
  ([:__global :const :float :* a
    :__global :const :float :* b
    :__global :float :* c]
   (var :int i := -/G0)
   (-/MUL= c a b i)))

(defonce +exec+
  (-> (exec/exec {:source sample
                  :worksize (fn [{:keys [a]}]
                              [(count a)])})
      (h/start)))

(fact:global
 {:component
  {|rt| {:create (jocl:create {})
         :setup  h/start
         :teardown h/stop}}})

^{:refer rt.jocl.runtime/kernel? :added "3.0"}
(fact "check that a code entry "

  (kernel? @sample)
  => true)

^{:refer rt.jocl.runtime/init-exec-jocl :added "3.0"
  :use [|rt|]}
(fact "initialises the exec in the runtime"
  ^:hidden
  
  (init-exec-jocl |rt|
                  (h/pointer {:context :lang/c 
                              :lang :c
                              :module 'rt.jocl.runtime-test
                              :section :code
                              :id 'sample})
                  @sample)
  => exec/exec?
  
  (-> ((get @(:state |rt|) `sample)
       (float-array (range 10))
       (float-array (range 10))
       (float-array 10))
      seq)
  =>  [0.0 1.0 4.0 9.0 16.0 25.0 36.0 49.0 64.0 81.0])

^{:refer rt.jocl.runtime/init-ptr-jocl :added "3.0"}
(fact "initialises the pointer")

^{:refer rt.jocl.runtime/invoke-ptr-jocl :added "3.0"}
(fact "invokes a jocl ptr (cached kernel)")

^{:refer rt.jocl.runtime/stop-jocl :added "3.0"}
(fact "stops the runtime")

^{:refer rt.jocl.runtime/jocl:create :added "3.0"}
(fact "creates a new runtime")

^{:refer rt.jocl.runtime/jocl :added "3.0"}
(fact "create and starts the runtime")
