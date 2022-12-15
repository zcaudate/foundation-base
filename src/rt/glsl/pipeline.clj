(ns rt.glsl.pipeline
  (:require [std.lib :as h]
            [std.lang :as l]))

(def +vertex-shader+
  '{:in   [[[:int] gl_VertexID]
           [[:int] gl_InstanceID]
           [[:int] gl_DrawID] 
           [[:int] gl_BaseVertex] 
           [[:int] gl_BaseInstance]]
    :out  [[[:vec4] gl_Position]
           [[:float] gl_PointSize]
           [[:float []] gl_ClipDistance]]})

(def +tesselation-control+
  '{:in   [[[:struct] gl_PerVertex]
           [[:int] gl_PatchVerticesIn]
           [[:int] gl_PrimitiveID]
           [[:int] gl_InvocationID]
           [[:int] gl_MaxPatchVertices]]
    :out [[[:float [4]] gl_TessLevelOuter]
          [[:float [2]] gl_TessLevelInner]]})

(def +tesselation-evaluation+
  '{:in   [[[:vec3] gl_TessCoord]
           [[:int] gl_PatchVerticesIn]
           [[:int] gl_PrimitiveID]]})

(def +geometry-shader+
  '{:in   [[[:int] gl_PrimitiveIDIn]
           [[:int] gl_InvocationID]]})

(def +fragment-shader+
  '{:in   [[[:int] gl_FragCoord]
           [[:vec4] gl_FragColor]
           [[:bool] gl_FrontFacing]
           [[:vec2] gl_PointCoord]
           [[:int] gl_SampleID]
           [[:vec2] gl_SamplePositions]
           [[:int []] gl_SampleMaskIn]
           [[:int] gl_Layer]
           [[:int] gl_ViewportIndex]]
    :out  [[[:float]  gl_FragDepth]
           [[:int []] gl_SampleMask]]})

(def +compute-shader+
  '{:in [[[:uvec3] gl_NumWorkGroups]
         [[:uvec3] gl_WorkGroupID]
         [[:uvec3] gl_LocalInvocationID]
         [[:uvec3] gl_GlobalInvocationID]
         [[:int] gl_LocalInvocationIndex]
         [[:uvec3] gl_WorkGroupSize]]})

(def +uniforms+
  '{:in [[[:float] gl_DepthRange]
         [[:float] [gl_DepthRangeNear gl_DepthRange.near]]
         [[:float] [gl_DepthRangeFar gl_DepthRange.far]]
         [[:float] [gl_DepthRangeDiff gl_DepthRange.diff]]
         [[:int] gl_NumSamples]]})

(def +constants+
  '[[[:int] gl_MaxVertexAttribs 16]
    [[:int] gl_MaxVertexOutputComponents 64]
    [[:int] gl_MaxVertexUniformComponents 1024]
    [[:int] gl_MaxVertexTextureImageUnits 16]
    [[:int] gl_MaxGeometryInputComponents 64]
    [[:int] gl_MaxGeometryOutputComponents 128]
    [[:int] gl_MaxGeometryUniformComponents 1024]
    [[:int] gl_MaxGeometryTextureImageUnits 16]
    [[:int] gl_MaxGeometryOutputVertices 256]
    [[:int] gl_MaxGeometryTotalOutputComponents 1024]
    [[:int] gl_MaxGeometryVaryingComponents 64]
    [[:int] gl_MaxFragmentInputComponents 128]
    [[:int] gl_MaxDrawBuffers 8]
    [[:int] gl_MaxFragmentUniformComponents 1024]
    [[:int] gl_MaxTextureImageUnits1 16]
    [[:int] gl_MaxClipDistances 8]
    [[:int] gl_MaxCombinedTextureImageUnits 48	96]

    [[:int] gl_MaxTessControlInputComponents 128]
    [[:int] gl_MaxTessControlOutputComponents 128]
    [[:int] gl_MaxTessControlUniformComponents 1024]
    [[:int] gl_MaxTessControlTextureImageUnits 16]
    [[:int] gl_MaxTessControlTotalOutputComponents 4096]
    [[:int] gl_MaxTessEvaluationInputComponents 128]
    [[:int] gl_MaxTessEvaluationOutputComponents 128]
    [[:int] gl_MaxTessEvaluationUniformComponents 1024]
    [[:int] gl_MaxTessEvaluationTextureImageUnits 16]
    [[:int] gl_MaxTessPatchComponents 120]
    [[:int] gl_MaxPatchVertices 32]
    [[:int] gl_MaxTessGenLevel 64]

    [[:int] gl_MaxViewports 16]
    [[:int] gl_MaxVertexUniformVectors 256]
    [[:int] gl_MaxFragmentUniformVectors 256]
    [[:int] gl_MaxVaryingVectors 15]

    [[:int] gl_MaxVertexImageUniforms 0]
    [[:int] gl_MaxVertexAtomicCounters 0]
    [[:int] gl_MaxVertexAtomicCounterBuffers 0]
    [[:int] gl_MaxTessControlImageUniforms 0]
    [[:int] gl_MaxTessControlAtomicCounters 0]
    [[:int] gl_MaxTessControlAtomicCounterBuffers 0]
    [[:int] gl_MaxTessEvaluationImageUniforms 0]
    [[:int] gl_MaxTessEvaluationAtomicCounters 0]
    [[:int] gl_MaxTessEvaluationAtomicCounterBuffers 0]
    [[:int] gl_MaxGeometryImageUniforms 0]
    [[:int] gl_MaxGeometryAtomicCounters 0]
    [[:int] gl_MaxGeometryAtomicCounterBuffers 0]
    [[:int] gl_MaxFragmentImageUniforms 8]
    [[:int] gl_MaxFragmentAtomicCounters 8]
    [[:int] gl_MaxFragmentAtomicCounterBuffers 1]
    [[:int] gl_MaxCombinedImageUniforms 8]
    [[:int] gl_MaxCombinedAtomicCounters 8]
    [[:int] gl_MaxCombinedAtomicCounterBuffers 1]
    [[:int] gl_MaxImageUnits 8]
    [[:int] gl_MaxCombinedImageUnitsAndFragmentOutputs 8]
    [[:int] gl_MaxImageSamples 0]
    [[:int] gl_MaxAtomicCounterBindings 1]
    [[:int] gl_MaxAtomicCounterBufferSize 32]
    [[:int] gl_MinProgramTexelOffset -8]
    [[:int] gl_MaxProgramTexelOffset 7]

    [[:ivec3] gl_MaxComputeWorkGroupCount [65535, 65535, 65535] ]
    [[:ivec3] gl_MaxComputeWorkGroupSize  [1024 1024 64] ]
    [[:int] gl_MaxComputeUniformComponents 512]
    [[:int] gl_MaxComputeTextureImageUnits 16]
    [[:int] gl_MaxComputeImageUniforms 8]
    [[:int] gl_MaxComputeAtomicCounters 8]
    [[:int] gl_MaxComputeAtomicCounterBuffers 1]

    [[:int] gl_MaxTransformFeedbackBuffers 4]
    [[:int] gl_MaxTransformFeedbackInterleavedComponents 64]])

(def +all+
  (concat (:in  +vertex-shader+)
          (:out +vertex-shader+)
          (:in  +tesselation-control+)
          (:in  +tesselation-evaluation+)
          (:in  +geometry-shader+)
          (:in  +fragment-shader+)
          (:out +fragment-shader+)
          (:in  +compute-shader+)
          (:in  +uniforms+)
          +constants+))
