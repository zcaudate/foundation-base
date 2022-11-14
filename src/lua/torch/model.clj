(ns lua.torch.model
  (:require [std.lib :as h]
            [std.lang :as l]))

(l/script :lua
  {:runtime :basic
   :require [[lua.torch :as tch]
             [lua.torch.nn :as nn]
             [lua.core :as u]
             [xt.lang.base-lib :as k]]
   :export [MODULE]})

(comment
  (l/rt:scaffold-imports :lua))

(defn.lua base-type
  "gets the base torch type of a given element"
  {:added "4.0"}
  [x]
  (var parent (u/getmetatable x))
  (cond (u/getmetatable parent)
        (return (-/base-type parent))

        :else
        (return (or (tch/typename parent)
                    (tch/typename x)))))

(defn.lua is-container?
  "checks that layer is a container"
  {:added "4.0"}
  [model]
  (var parent (u/getmetatable model))
  (cond (k/nil? parent)
        (return false)
        
        (== "nn.Container"
            (tch/typename parent))
        (return true)

        :else
        (return (-/is-container? parent))))

(defn.lua list-base-types
  "list types given base name"
  {:added "4.0"}
  [name]
  (return
   (-> nn
       (k/obj-keep
        (fn:> [x]
          (:? (== name (-/base-type x))
              true)))
       (k/obj-keys)
       (k/sort))))

(defn.lua list-criterion
  "lists all criterion types"
  {:added "4.0"}
  []
  (return
   (-/list-base-types "nn.Criterion")))

(defn.lua list-modules
  "lists all modules"
  {:added "4.0"}
  []
  (return
   (-/list-base-types "nn.Module")))

(defn.lua list-containers
  "lists all containers"
  {:added "4.0"}
  []
  (return
   (-> nn
       (k/obj-keepf
        -/is-container?
        k/T)
       (k/obj-keys)
       (k/sort))))

(defn.lua list-layers
  "lists all layres types"
  {:added "4.0"}
  []
  (return
   (-> nn
       (k/obj-keepf
        (fn:> [v]
          (and (not (-/is-container? v))
               (== "nn.Module" (-/base-type v))))
        k/T)
       (k/obj-keys)
       (k/sort))))



(defn.lua as-lua-data
  "converts tensor to lua table"
  {:added "4.0"}
  [x]
  (cond (== "userdata" (type x))
        (return (tch/totable x))

        (== "table" (type x))
        (return (k/obj-map x -/as-lua-data))
        
        :else (return x)))

(defn.lua as-torch-data
  "converts lua table to tensor"
  {:added "4.0"}
  [x]
  (cond (== "table" (type x))
        (return (tch/Tensor x))

        (== "userdata" (type x))
        (return x)
        
        :else (return (tch/Tensor [x]))))

(defn.lua get-info
  "gets info for a layer"
  {:added "4.0"}
  [model]
  (when model
    (var out (-> (k/obj-pick model ["bias"
                                    "weight"])
                 (k/obj-map -/as-lua-data)))
    (k/set-key out "type" (tch/typename model))
    (return out)))

(defn.lua is-container
  "checks that model is a container"
  {:added "4.0"}
  [model]
  (return
   (== "nn.Container"
       (tch/typename
        (u/getmetatable
         model)))))

(defn.lua set-model-data
  "sets the network for a nn model"
  {:added "4.0"}
  [model data strict]
  (when (and strict
             (not= (tch/typename model) (. data ["type"])))
    (k/err (k/cat "Expected: " (tch/typename model)
                  ". Received: " (. data ["type"]))))
  (cond (. data children)
        (k/for:array [[idx sdata] (. data children)]
          (-/set-model-data (. model (get idx))
                            sdata
                            strict))
        
        :else
        (do (when (. data weight)
              (:= (. model weight)
                  (tch/Tensor (. data weight))))
            (when (. data bias)
              (:= (. model bias)
                  (tch/Tensor (. data bias))))))
  (return model))

(defn.lua get-model-data
  "gets the weights and biases for the model"
  {:added "4.0"}
  [model]
  (var children-fn
       (fn [model]
         (do (var size (k/len model))
             (var out [])
             (k/for:index [idx [1 size]]
               (var layer (. model (get idx)))
               (var info  (or (-/get-model-data layer)
                              {}))
               (x:arr-push out info))
             (return out))))
  (cond (-/is-container model)
        (return {:type (tch/typename model)
                 :children (children-fn model)})
        
        :else
        (return (-/get-info model))))

;;
;; prediction 
;;

(defn.lua predict-single
  "uses the neural-net to predict"
  {:added "4.0"}
  [model input as-raw]
  (:= input  (-/as-torch-data input))
  (var predicted (. model (forward input)))
  (var out {:input  input
            :predicted predicted})
  (cond as-raw
        (return out)

        :else
        (return (-/as-lua-data out))))

(defn.lua run-single
  "runs a single prediction"
  {:added "4.0"}
  [model sample criterion as-raw]
  (var result (-/predict-single model (. sample input) true))
  (var #{predicted} result)
  (var actual (-/as-torch-data (. sample output)))
  (var loss (. criterion (forward predicted actual)))
  (var out (k/obj-assign {:actual actual
                          :loss loss}
                         result))
  (cond as-raw
        (return out)

        :else
        (return (-/as-lua-data out))))


;;
;; training
;;

(defn.lua train-single
  "performs a run and adjusts weights accordingly"
  {:added "4.0"}
  [model sample criterion learning-rate as-raw]
  (var result (-/run-single model sample criterion true))
  (var #{input
         actual
         predicted} result)
  (var grad (. criterion (backward predicted actual)))
  (. model (zeroGradParameters))
  (. model (backward input grad))
  (. model (updateParameters learning-rate))
  (var out (k/obj-assign {:grad grad} result))
  (cond as-raw
        (return out)

        :else
        (return (-/as-lua-data out))))


(def.lua MODULE (!:module))
