(ns std.lib.enum-test
  (:use code.test)
  (:require [std.lib.enum :refer :all])
  (:import java.lang.annotation.ElementType))

^{:refer std.lib.enum/enum? :added "3.0"}
(fact "check to see if class is an enum type"

  (enum? java.lang.annotation.ElementType) => true

  (enum? String) => false)

^{:refer std.lib.enum/enum-values :added "3.0"}
(fact "returns all values of an enum type"

  (->> (enum-values ElementType)
       (map str))
  => (contains ["TYPE" "FIELD" "METHOD" "PARAMETER" "CONSTRUCTOR"]
               :in-any-order :gaps-ok))

^{:refer std.lib.enum/create-enum :added "3.0"}
(fact "creates an enum value from a string"

  (create-enum "TYPE" ElementType)
  => ElementType/TYPE)

^{:refer std.lib.enum/enum-map :added "3.0"}
(fact "cached map of enum values"

  (enum-map ElementType) ^:hidden
  => (satisfies [:annotation-type
                 :constructor
                 :field
                 :local-variable
                 :method
                 :module
                 :package
                 :parameter
                 :type
                 :type-parameter
                 :type-use]
                (comp vec sort keys)))

^{:refer std.lib.enum/enum-map-form :added "3.0"}
(fact "creates the form for the enum"

  (enum-map-form ElementType) ^:hidden
  => '{:package java.lang.annotation.ElementType/PACKAGE,
       :type-use java.lang.annotation.ElementType/TYPE_USE,
       :method java.lang.annotation.ElementType/METHOD,
       :field java.lang.annotation.ElementType/FIELD,
       :type java.lang.annotation.ElementType/TYPE,
       :module java.lang.annotation.ElementType/MODULE,
       :type-parameter java.lang.annotation.ElementType/TYPE_PARAMETER,
       :constructor java.lang.annotation.ElementType/CONSTRUCTOR,
       :local-variable java.lang.annotation.ElementType/LOCAL_VARIABLE,
       :annotation-type java.lang.annotation.ElementType/ANNOTATION_TYPE,
       :parameter java.lang.annotation.ElementType/PARAMETER})

^{:refer std.lib.enum/enum-map> :added "3.0"}
(fact "a macro for getting elements of the enum"

  (enum-map> ElementType) ^:hidden
  => {:package ElementType/PACKAGE,
      :type-use ElementType/TYPE_USE,
      :method ElementType/METHOD,
      :field ElementType/FIELD,
      :type ElementType/TYPE,
      :module ElementType/MODULE,
      :type-parameter ElementType/TYPE_PARAMETER,
      :constructor ElementType/CONSTRUCTOR,
      :local-variable ElementType/LOCAL_VARIABLE,
      :annotation-type ElementType/ANNOTATION_TYPE,
      :parameter ElementType/PARAMETER})

^{:refer std.lib.enum/to-enum :added "3.0"}
(fact "gets an enum value given a symbol"

  (to-enum "TYPE" ElementType)
  => ElementType/TYPE

  (to-enum :field ElementType)
  => ElementType/FIELD)

(comment
  (./import))
