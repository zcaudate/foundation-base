(ns std.protocol.block)

(definterface IBlock
  (^clojure.lang.Keyword _type   [])        ;; returns one of *type*
  (^clojure.lang.Keyword _tag    [])        ;; returns semantic representation
  (^String _string [])        ;; convert node to string form
  (^Number _length [])        ;; length of the node
  (^Number _width  [])        ;; rows taken up by node
  (^Number _height [])        ;; cols, or length of last row
  (^Number _prefixed [])        ;; length of prefix
  (^Number _suffixed [])
  (^Boolean _verify [])        ;; check to see if the node contains valid data 
  )

(definterface IBlockModifier
  (_modify [accumulator input]))

(definterface IBlockExpression
  (_value  [])         ;; returns the value
  (^String _value_string  [])  ;; returns the string to be read in order to give a value 
  )

(definterface IBlockContainer
  (_children  [])           ;; returns one of *type*
  (_replace_children  [children])   ;; returns one of *type*
  ;(-linebreaks  [_])         ;; returns number of linebreaks in the container
  ;(-last-row-length  [_])    ;; returns the last row length
  )

(comment
  (require 'jvm.namespace)
  (jvm.namespace/reset 'std.block.type))
