(ns rt.glsl
  (:require [std.lib :as h]
            [std.lang :as l]
            [rt.glsl.pipeline :as pipeline]
            [rt.glsl.webgl :as webgl])
  (:refer-clojure :exclude [flush]))

;;
;; shader constants
;;

(l/script :glsl)

(def$.gl origin-upper-left origin_upper_left)

(def$.gl pixel-center-integer pixel_center_integer)

(def$.gl layout layout)
  
(defn glsl-tmpl
  "creates entries for `def.gl`
 
   (glsl-tmpl '[[:int] gl_MaxTransformFeedbackInterleavedComponents 64])
   => '(def$.gl gl_MaxTransformFeedbackInterleavedComponents
         gl_MaxTransformFeedbackInterleavedComponents)"
  {:added "4.0"}
  ([entry]
   (let [[type sym] entry
         [nsym glsym] (if (vector? sym) sym [sym sym])]
     (h/$ (def$.gl ~nsym ~glsym)))))

(h/template-entries [glsl-tmpl] pipeline/+all+)

;;
;; js constants
;;

(l/script :js)

(defn webgl-constant-tmpl
  "creates entries for javascript"
  {:added "4.0"}
  ([const]
   (h/$ (def$.js ~const ~(symbol (str "gl." const))))))

(def +constants+
  (partition-all 200 (filter symbol? (webgl/get-edn))))

(h/template-entries [webgl-constant-tmpl]
  (nth +constants+ 0))

(h/template-entries [webgl-constant-tmpl]
  (nth +constants+ 1))

(h/template-entries [webgl-constant-tmpl]
  (nth +constants+ 2))

(h/template-entries [webgl-constant-tmpl]
  [canvas drawingBufferWidth drawingBufferHeight])

(defn webgl-method-tmpl-fn
  "function for the template
 
   (webgl-method-tmpl-fn 'gl 'hello '[a b c])
   => '(. gl (hello a b c))"
  {:added "4.0"}
  [gl method args]
  (list '. gl (apply list (symbol method) args)))

(defn webgl-method-tmpl
  "creates the webgl template
   
   (webgl-method-tmpl 'Hello)
   => '(defmacro.js Hello [& args]
         (webgl-method-tmpl-fn (quote gl) \"Hello\" args))"
  {:added "4.0"}
  ([method]
   (h/$ (defmacro.js ~method
          [& args]
          (webgl-method-tmpl-fn 'gl ~(str method) args)))))

(h/template-entries [webgl-method-tmpl]
  webgl/+context-methods+)
