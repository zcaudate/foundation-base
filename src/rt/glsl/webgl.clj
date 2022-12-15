(ns rt.glsl.webgl
  (:require [std.lib :as h]
            [std.html :as html]
            [std.fs :as fs]))

(def +root+ "assets/rt.glsl")

(def +html-url+ "https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Constants")
(def +html-path+ (str +root+ "/webgl.html"))
(def +edn-path+  (str +root+ "/webgl.edn"))

(defn fetch-html
  "fetches the webgl spec"
  {:added "4.0"}
  []
  (h/sh "wget" +html-url+ "-O" "webgl.html"
        {:root (str "resources/" +root+)}))

(defn get-html
  "reads webgl spec or fetches it"
  {:added "4.0"}
  ([]
   (or (if-let [res (h/sys:resource +html-path+)]
         (slurp res))
       (do (fetch-html)
           (get-html)))))

(defn build-edn
  "builds the edn representation"
  {:added "4.0"}
  ([]
   (let [out (->> (html/select (html/parse (get-html))
                               "table.standard-table tbody tr td:first-child code")
                  (map html/text)
                  (map #(re-find #"^\w+" %))
                  (filter #(> (count %) 3))
                  (mapv symbol))]
     (spit (str "resources/" +edn-path+)
           out)
     out)))

(defn get-edn
  "gets the edn representation"
  {:added "4.0"}
  ([]
   (or (if-let [edn (h/sys:resource +edn-path+)]
         (read-string (slurp edn)))
       (build-edn))))

(defn clean
  "cleans all files"
  {:added "4.0"}
  ([]
   (fs/delete (fs/path "resources/" +html-path+))
   (fs/delete (fs/path "resources/" +edn-path+))))

(defn create
  "creates the edn from blank dir"
  {:added "4.0"}
  ([]
   (fs/create-directory "resources/assets/python.core")
   (spit (str "resources/" +html-path+)
         (fetch-html))
   (build-edn)))

(def +context-methods+
  '[activeTexture
    attachShader
    bindAttribLocation
    bindBuffer
    bindFramebuffer
    bindRenderbuffer
    bindTexture
    blendColor
    blendEquation
    blendEquationSeparate
    blendFunc
    blendFuncSeparate
    bufferData
    bufferSubData
    checkFramebufferStatus
    clear
    clearColor
    clearDepth
    clearStencil
    colorMask
    commit
    compileShader
    compressedTexImage2D
    compressedTexImage3D
    compressedTexSubImage2D
    copyTexImage2D
    copyTexSubImage2D
    createBuffer
    createFramebuffer
    createProgram
    createRenderbuffer
    createShader
    createTexture
    cullFace
    deleteBuffer
    deleteFramebuffer
    deleteProgram
    deleteRenderbuffer
    deleteShader
    deleteTexture
    depthFunc
    depthMask
    depthRange
    detachShader
    disable
    disableVertexAttribArray
    drawArrays
    drawElements
    enable
    enableVertexAttribArray
    finish
    flush
    framebufferRenderbuffer
    framebufferTexture2D
    frontFace
    generateMipmap
    getActiveAttrib
    getActiveUniform
    getAttachedShaders
    getAttribLocation
    getBufferParameter
    getContextAttributes
    getError
    getExtension
    getFramebufferAttachmentParameter
    getParameter
    getProgramInfoLog
    getProgramParameter
    getRenderbufferParameter
    getShaderInfoLog
    getShaderParameter
    getShaderPrecisionFormat
    getShaderSource
    getSupportedExtensions
    getTexParameter
    getUniform
    getUniformLocation
    getVertexAttrib
    getVertexAttribOffset
    hint
    isBuffer
    isContextLost
    isEnabled
    isFramebuffer
    isProgram
    isRenderbuffer
    isShader
    isTexture
    lineWidth
    linkProgram
    pixelStorei
    polygonOffset
    readPixels
    renderbufferStorage
    sampleCoverage
    scissor
    shaderSource
    stencilFunc
    stencilFuncSeparate
    stencilMask
    stencilMaskSeparate
    stencilOp
    stencilOpSeparate
    texImage2D
    texParameterf
    texParameteri
    texSubImage2D
    uniform1f
    uniform1fv
    uniform1i
    uniform1iv
    
    uniform2f
    uniform2fv
    uniform2i
    uniform2iv
    
    uniform3f
    uniform3fv
    uniform3i
    uniform3iv
    
    uniform4f
    uniform4fv
    uniform4i
    uniform4iv
    uniformMatrix2fv
    uniformMatrix3fv
    uniformMatrix4fv
    useProgram
    validateProgram
    vertexAttrib1f
    vertexAttrib2f
    vertexAttrib3f
    vertexAttrib4f
    vertexAttrib1fv
    vertexAttrib2fv
    vertexAttrib3fv
    vertexAttrib4fv
    vertexAttribPointer
    viewport])
