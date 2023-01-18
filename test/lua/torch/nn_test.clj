(ns lua.torch.nn-test
  (:use code.test)
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script- :lua
  {:runtime :basic
   :config {:program :torch}
   :require  [[xt.lang.base-lib :as k]
              [xt.lang.base-iter :as iter]
              [xt.lang.base-runtime :as rt :with [defvar.lua]]
              [lua.torch :as tch]
              [lua.torch.nn :as nn]
              [lua.torch.model :as model]
              [lua.torch.optim :as optim]
              [lua.core :as u]]
   :export [MODULE]})

(fact:global
 {:setup [(l/rt:restart)
          (h/suppress
           (l/with:print
               (l/rt:scaffold-imports :lua)))]
  :teardown [(l/rt:stop)]})

^{:refer lua.torch.nn/LinearUnit :adopt true :added "4.0"}
(fact "creates a Linear Layer"
  ^:hidden
  
  (defvar.lua ^{:static/no-lint true}
    LinearUnitNet
    []
    (return (nn/Linear 1 1)))
  
  (defvar.lua ^{:static/no-lint true}
    LinearUnitCriterion
    []
    (return (nn/MSECriterion)))  
  
  ;;
  ;; Testing Weights
  ;;
  (!.lua
   (== (k/first
        (tch/totable
         (. (-/LinearUnitNet)
            (forward (tch/Tensor [1])))))
       
       (+  (k/first (tch/totable (. (-/LinearUnitNet)
                                    bias)))
           (k/first (k/first (tch/totable (. (-/LinearUnitNet)
                                             weight)))))))
  => true
  
  (!.lua
   (== (k/first
        (tch/totable
         (. (-/LinearUnitNet)
            (forward (tch/Tensor [2])))))
       
       (+  (k/first (tch/totable (. (-/LinearUnitNet)
                                    bias)))
           (* 2 (k/first (k/first (tch/totable (. (-/LinearUnitNet)
                                                  weight))))))))
  => true

  ;;
  ;; Testing Training
  ;;
  ;; Will see weight -> 1 and bias -> 0
  ;;
  (!.lua
   (k/for:index [i [1 1000]]
     (var N (k/random))
     (var input  (tch/Tensor [N]))
     (var actual (tch/Tensor [N]))
     (var predicted (. (-/LinearUnitNet)
                       (forward input)))
     (var loss (. (-/LinearUnitCriterion)
                  (forward predicted actual)))
     (. (-/LinearUnitNet)
        (zeroGradParameters))
     (. (-/LinearUnitNet)
        (backward
         input
         (. (-/LinearUnitCriterion)
            (backward predicted actual))))
     (. (-/LinearUnitNet)
        (updateParameters 0.1)))
   (model/get-info (-/LinearUnitNet)))
  => map?)

^{:refer lua.torch.nn/Tanh :adopt true :added "4.0"
  :setup [(def +input+
            [-4 -3 -2 -1 0 1 2 3 4])
          (def +output+
            [-0.99932929973907
             -0.99505475368673
             -0.96402758007582
             -0.76159415595576
             0
             0.76159415595576
             0.96402758007582
             0.99505475368673
             0.99932929973907])]}
(fact "Looks at Transforms"
  ^:hidden
  
  (!.lua
   (tch/totable
    (. (nn/Tanh)
       (forward (tch/Tensor (@! +input+))))))
  => +output+
  
  (!.lua
   (tch/totable
    (tch/tanh (tch/Tensor (@! +input+)))))
  => +output+)

^{:refer lua.torch.nn/Add :adopt true :added "4.0"
  :setup [(def +input+
            [1 0 -1])]}
(fact "Looks at Transforms"
  ^:hidden
  
  (!.lua
   (var layer (nn/Add 3))
   (k/set-key layer "bias" (tch/Tensor [5 5 5]))
   (tch/totable
    (. layer
       (forward (tch/Tensor (@! +input+))))))
  => [6 5 4]

  (!.lua
   (var layer (nn/CAdd 3))
   (k/set-key layer "bias" (tch/Tensor [5 5 5]))
   (tch/totable
    (. layer
       (forward (tch/Tensor (@! +input+))))))
  => [6 5 4]

  (!.lua
   (var layer (nn/CAddTable 3))
   (tch/totable
    (. layer
       (forward [(tch/Tensor (@! +input+))
                 (tch/Tensor (@! +input+))]))))
  => [2 0 -2])

^{:refer lua.torch.nn/Xor :adopt true :added "4.0"}
(fact "creates an XOR neural net"
  ^:hidden
  
  (defvar.lua ^{:static/no-lint true}
    XorNet
    []
    (var network (nn/Sequential))
    (var inputs 2)
    (var outputs 1)
    (var hidden 2)
    (. network (add (nn/Linear inputs hidden)))
    (. network (add (nn/Tanh)))
    (. network (add (nn/Linear hidden outputs)))
    (return network))
  
  (defvar.lua ^{:static/no-lint true}
    XorCriterion
    []
    (return (nn/MSECriterion)))
  
  (defn.lua make-sample
    []
    (var input  [(k/sign (- (k/random)
                            0.5))
                 (k/sign (- (k/random)
                            0.5))])
    (var output (:? (< 0 (* (k/first input)
                            (k/second input)))
                    [-1]
                    [1]))
    (return {:input  input
             :output output}))
  
  (make-sample)
  => #{{"output" [-1], "input" [1 1]}
       {"output" [-1], "input" [-1 -1]}
       {"output" [1], "input" [-1 1]}
       {"output" [1], "input" [1 -1]}})
