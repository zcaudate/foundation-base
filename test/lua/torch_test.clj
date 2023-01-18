(ns lua.torch-test
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
              [lua.core :as u]]
   :export [MODULE]})

(fact:global
 {:setup [(l/rt:restart)
          (h/suppress
           (l/rt:scaffold-imports :lua))]
  :teardown [(l/rt:stop)]})

(comment
  (defvar.lua X0
    []
    (var A (tch/rand 5 5))
    (return (. (* A (. A (t)))
               (add 0.001 (tch/eye 5)))))

  (defvar.lua X1
    []
    (return (tch/rand 5)))

  (defn.lua J
    [x]
    (return (- (* 0.5 (. x (dot (* (-/X0) x))))
               (. (-/X1) (dot x)))))

  (defn.lua dJ
    [x]
    (return (- (* (-/X0) x)
               (-/X1))))


  (!.lua
   (:= lr 0.01)
   (:= x (tch/rand 5))
   (iter/arr<
    (iter/take 20000
               (iter/repeatedly
                (fn []
                  (:= x (- x (* (-/dJ x) lr)))
                  (return (-/J x)))))))
  (comment
    (h/suppress (l/rt:scaffold-imports :lua))
    (l/rt:restart))

  (!.lua
   (k/obj-keys optim))

  (def.lua
    TABLE
    {"__mul" (fn [a b]
               (return {:val (* (. a val)
                                (. b val))}))})

  (!.lua
   (tch/sigmoid (tch/Tensor [0])))

  (!.lua
   (var b {:val 8})
   (u/setmetatable b -/TABLE)
   (* b b))

  (!.lua
   (var t (tch/Tensor [[1 2 3]
                       [4 5 6]
                       [7 8 9]]))
   (-> t
       (tch/cat t)
       (tch/cat t)))

  (!.lua
   (var layer (nn/Linear 2 1))
   (var pred (. layer
                (forward (tch/Tensor [0 0]))))
   (. layer (backward (tch/rand 2))))

  (def.lua MODULE (!:module))




  (comment
    
    (!.lua
     (var s (tch/LongStorage 6))
     (k/set-idx s 1 4)
     (k/set-idx s 2 5)
     (k/set-idx s 3 6)
     (k/set-idx s 4 2)
     (k/set-idx s 5 7)
     (k/set-idx s 6 6)
     (. (tch/Tensor s)
        (size))
     #_[#_(. (tch/Tensor s)
             (nDimension))
        ])

    (!.lua
     (var x (tch/rand 2 4))
     (k/obj-keys (u/getmetatable x))))




  (comment
    (u/setmetatable)
    (!.lua
     (var linear (nn/Linear 3 1))
     (k/get-spec (k/obj-map linear k/identity)))
    {"_type" "string",
     "output" "userdata",
     "gradWeight" "userdata",
     "gradInput" "userdata",
     "bias" "userdata",
     "gradBias" "userdata",
     "weight" "userdata"}

    (!.lua
     (var linear (nn/Linear 2 1))
     (k/obj-keys (u/getmetatable (. linear bias))))
    
    (!.lua
     (var linear (nn/Linear 2 1))
     (. linear gradWieght))
    
    (!.lua
     (var linear (nn/Linear 1 3))
     (k/obj-map linear
                tch/totable))
    
    (nn/Sequential)
    (tch/Tensor 5 6)

    (tch/rand 5 3)

    (tch/random 1 3)

    (!.lua
     (tch/mm (tch/eye 5)
             (tch/rand 5)
             ))

    (!.lua
     (tch/dot 
      (tch/eye 5)
      (tch/eye 5)))

    (!.lua
     (tch/dot 
      (tch/rand 5)
      (tch/rand 5)
      ))

    (!.lua
     (tch/mul
      (tch/rand 5 2)
      5
      ))
    (!.lua
     (tch/cat (tch/rand 2 2)
              (tch/rand 2 2)))
    (!.lua
     (k/obj-keys torch)
     )

    (defglobal.lua
      A
      (tch/rand 5 5))





    (!.lua
     (-/J (tch/rand 5)
          (tch/rand 5 5)
          (tch/rand 5)))

    (!.lua
     (:= A (tch/rand 5 5))
     true)

    (!.lua
     (var A (tch/rand 5 5))
     (. (* A A)
        (t)
        (add 0.001 (tch/eye 5))))

    

    (comment
      (l/with:print
          (l/rt:scaffold-imports :lua))
      (l/with:print
          (!.lua
           (var a 1)
           {:a 1}))
      (h/suppress )
      )
    )

  )
