(ns lua.torch.model-test
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
          (!.lua
           (:= (!:G torch) (require "torch"))
           (:= (!:G nn) (require "nn")))]
  :teardown [(l/rt:stop)]})

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

^{:refer lua.torch.model/base-type :added "4.0"}
(fact "gets the base torch type of a given element"
  ^:hidden
  
  (model/base-type nn/Abs)
  => "nn.Module")

^{:refer lua.torch.model/is-container? :added "4.0"}
(fact "checks that layer is a container"
  ^:hidden
  
  (!.lua
   (model/is-container? (nn/Sequential)))
  => true

  (!.lua
   (model/is-container? (nn/Linear 1 1)))
  => false)

^{:refer lua.torch.model/list-base-types :added "4.0"}
(fact "list types given base name"
  ^:hidden
  
  (model/list-base-types "nn.Module")
  => vector?

  (model/list-base-types "nn.Criterion")
  => vector?)

^{:refer lua.torch.model/list-criterion :added "4.0"}
(fact "lists all criterion types"
  ^:hidden
  
  (model/list-criterion)
  => ["AbsCriterion"
      "BCECriterion"
      "ClassNLLCriterion"
      "ClassSimplexCriterion"
      "CosineEmbeddingCriterion"
      "CrossEntropyCriterion"
      "DistKLDivCriterion"
      "DistanceRatioCriterion"
      "HingeEmbeddingCriterion"
      "L1Cost"
      "L1HingeEmbeddingCriterion"
      "MSECriterion"
      "MarginCriterion"
      "MarginRankingCriterion"
      "ModuleCriterion"
      "MultiCriterion"
      "MultiLabelMarginCriterion"
      "MultiLabelSoftMarginCriterion"
      "MultiMarginCriterion"
      "ParallelCriterion"
      "SmoothL1Criterion"
      "SoftMarginCriterion"
      "SpatialAutoCropMSECriterion"
      "SpatialClassNLLCriterion"
      "WeightedMSECriterion"])

^{:refer lua.torch.model/list-modules :added "4.0"}
(fact "lists all modules")

^{:refer lua.torch.model/list-containers :added "4.0"}
(fact "lists all containers"
  ^:hidden
  
  (model/list-containers)
  => ["Bottle"
      "CReLU"
      "Concat"
      "ConcatTable"
      "Convert"
      "Decorator"
      "DepthConcat"
      "DontCast"
      "GPU"
      "LayerNormalization"
      "MapTable"
      "Maxout"
      "NaN"
      "Parallel"
      "ParallelTable"
      "Profile"
      "Sequential"
      "SpatialLPPooling"
      "WeightNorm"])

^{:refer lua.torch.model/list-layers :added "4.0"}
(fact "lists all layres types"
  ^:hidden
  
  (model/list-layers)
  => ["Abs"
      "Add"
      "AddConstant"
      "BatchNormalization"
      "Bilinear"
      "CAdd"
      "CAddTable"
      "CAddTensorTable"
      "CDivTable"
      "CMaxTable"
      "CMinTable"
      "CMul"
      "CMulTable"
      "CSubTable"
      "Clamp"
      "Collapse"
      "Constant"
      "Container"
      "Contiguous"
      "Copy"
      "Cosine"
      "CosineDistance"
      "CriterionTable"
      "DotProduct"
      "Dropout"
      "ELU"
      "Euclidean"
      "Exp"
      "FlattenTable"
      "GatedLinearUnit"
      "GradientReversal"
      "HardShrink"
      "HardTanh"
      "Identity"
      "Index"
      "IndexLinear"
      "JoinTable"
      "Kmeans"
      "L1Penalty"
      "LeakyReLU"
      "Linear"
      "LinearWeightNorm"
      "Log"
      "LogSigmoid"
      "LogSoftMax"
      "LookupTable"
      "MM"
      "MV"
      "MaskedSelect"
      "Max"
      "Mean"
      "Min"
      "MixtureTable"
      "Mul"
      "MulConstant"
      "Narrow"
      "NarrowTable"
      "Normalize"
      "OneHot"
      "PReLU"
      "Padding"
      "PairwiseDistance"
      "PartialLinear"
      "PixelShuffle"
      "Power"
      "PrintSize"
      "RReLU"
      "ReLU"
      "ReLU6"
      "Replicate"
      "Reshape"
      "Select"
      "SelectTable"
      "Sigmoid"
      "SoftMax"
      "SoftMin"
      "SoftPlus"
      "SoftShrink"
      "SoftSign"
      "SparseLinear"
      "SpatialAdaptiveAveragePooling"
      "SpatialAdaptiveMaxPooling"
      "SpatialAveragePooling"
      "SpatialBatchNormalization"
      "SpatialContrastiveNormalization"
      "SpatialConvolution"
      "SpatialConvolutionLocal"
      "SpatialConvolutionMM"
      "SpatialConvolutionMap"
      "SpatialCrossMapLRN"
      "SpatialDepthWiseConvolution"
      "SpatialDilatedConvolution"
      "SpatialDilatedMaxPooling"
      "SpatialDivisiveNormalization"
      "SpatialDropout"
      "SpatialFractionalMaxPooling"
      "SpatialFullConvolution"
      "SpatialFullConvolutionMap"
      "SpatialLogSoftMax"
      "SpatialMaxPooling"
      "SpatialMaxUnpooling"
      "SpatialReflectionPadding"
      "SpatialReplicationPadding"
      "SpatialSoftMax"
      "SpatialSubSampling"
      "SpatialSubtractiveNormalization"
      "SpatialUpSamplingBilinear"
      "SpatialUpSamplingNearest"
      "SpatialZeroPadding"
      "SplitTable"
      "Sqrt"
      "Square"
      "Squeeze"
      "Sum"
      "Tanh"
      "TanhShrink"
      "TemporalConvolution"
      "TemporalDynamicKMaxPooling"
      "TemporalMaxPooling"
      "TemporalRowConvolution"
      "TemporalSubSampling"
      "Threshold"
      "Transpose"
      "Unsqueeze"
      "View"
      "VolumetricAveragePooling"
      "VolumetricBatchNormalization"
      "VolumetricConvolution"
      "VolumetricDilatedConvolution"
      "VolumetricDilatedMaxPooling"
      "VolumetricDropout"
      "VolumetricFractionalMaxPooling"
      "VolumetricFullConvolution"
      "VolumetricMaxPooling"
      "VolumetricMaxUnpooling"
      "VolumetricReplicationPadding"
      "WeightedEuclidean"
      "WhiteNoise"
      "ZeroGrad"
      "ZipTable"
      "ZipTableOneToMany"])

^{:refer lua.torch.model/as-lua-data :added "4.0"}
(fact "converts tensor to lua table"
  ^:hidden
  
  (!.lua
   (model/as-lua-data (tch/Tensor [1 2 3])))
  => [1 2 3])

^{:refer lua.torch.model/as-torch-data :added "4.0"}
(fact "converts lua table to tensor"
  ^:hidden
  
  (!.lua
   (model/as-lua-data
    (model/as-torch-data [1 2 3])))
  => [1 2 3])

^{:refer lua.torch.model/get-info :added "4.0"}
(fact "gets info for a layer"
  ^:hidden
  
  (!.lua
   (model/get-info (nn/Linear 1 1)))
  => (contains-in
      {"type" "nn.Linear"
       "bias" [number?]
       "weight" [[number?]]})

  (!.lua
   (model/get-info (nn/Linear 2 2)))
  => (contains-in
      {"type" "nn.Linear"
       "bias" [number? number?],
       "weight" [[number? number?]
                 [number? number?]]}))

^{:refer lua.torch.model/is-container :added "4.0"}
(fact "checks that model is a container")

^{:refer lua.torch.model/set-model-data :added "4.0"
  :setup [(def +network+
            {"children"
             [{"bias" [0.81203520313831 -1.4014264126895],
               "weight"
               [[1.0408440310131 1.0610565403961]
                [1.3885321475216 1.4410916029971]]}
              {}
              {"bias" [-1.170133613625],
               "weight" [[1.410949201755 -1.3828521072806]]}]})]}
(fact "sets the network for a nn model"
  ^:hidden
  
  (!.lua
   (model/get-model-data
    (model/set-model-data
     (nn/Linear 2 2)
     {"bias" [1 2],
      "weight"
      [[3 4]
       [5 6]]})))
  => {"type" "nn.Linear", "bias" [1 2], "weight" [[3 4] [5 6]]}

  (!.lua
   (-/XorNet-reset)
   (model/as-lua-data
    (model/run-single
     (model/set-model-data
      (-/XorNet)
      (@! +network+))
     {:input [1 1]
      :output -1}
     (nn/MSECriterion))))
  => {"predicted" [-0.99999999999992],
      "input" [1 1],
      "actual" [-1],
      "loss" 6.3189730660466E-27})

^{:refer lua.torch.model/get-model-data :added "4.0"}
(fact "gets the weights and biases for the model")

^{:refer lua.torch.model/predict-single :added "4.0"}
(fact "uses the neural-net to predict"
  ^:hidden
  
  (!.lua
   (model/predict-single
    (-/XorNet)
    [1 1]))
  => (contains
      {"predicted" vector?,
       "input" [1 1]}))

^{:refer lua.torch.model/run-single :added "4.0"}
(fact "runs a single prediction"
  ^:hidden
  
  (!.lua
   (model/run-single
    (-/XorNet)
    {:input [1 1]
     :output -1}
    (nn/MSECriterion)))
  => {"predicted" [-0.99999999999992], "input" [1 1],
      "actual" [-1], "loss" 6.3189730660466E-27})

^{:refer lua.torch.model/train-single :added "4.0"
  :setup    [(!.lua
              (model/set-model-data
               (-/XorNet)
               (@! +network+)))]
  :teardown [(!.lua
              (model/set-model-data
               (-/XorNet)
               (@! +network+)))]}
(fact "performs a run and adjusts weights accordingly"
  ^:hidden
  
  (!.lua
   (model/train-single
    (-/XorNet)
    {:input [1 1]
     :output -1}
    (nn/MSECriterion)
    0.1))
  => {"grad" [1.5898393712632E-13],
      "predicted" [-0.99999999999992],
      "input" [1 1],
      "actual" [-1], "loss" 6.3189730660466E-27})
