(ns rt.glsl-test
  (:use code.test)
  (:require [rt.glsl :refer :all :exclude [flush]]))

^{:refer rt.glsl/glsl-tmpl :added "4.0"}
(fact "creates entries for `def.gl`"

  (glsl-tmpl '[[:int] gl_MaxTransformFeedbackInterleavedComponents 64])
  => '(def$.gl gl_MaxTransformFeedbackInterleavedComponents
        gl_MaxTransformFeedbackInterleavedComponents))

^{:refer rt.glsl/webgl-constant-tmpl :added "4.0"}
(fact "creates entries for javascript")

^{:refer rt.glsl/webgl-method-tmpl-fn :added "4.0"}
(fact "function for the template"

  (webgl-method-tmpl-fn 'gl 'hello '[a b c])
  => '(. gl (hello a b c)))

^{:refer rt.glsl/webgl-method-tmpl :added "4.0"}
(fact "creates the webgl template"
  
  (webgl-method-tmpl 'Hello)
  => '(defmacro.js Hello [& args]
        (webgl-method-tmpl-fn (quote gl) "Hello" args)))
