(ns std.contract.type-test
  (:use code.test)
  (:require [std.contract.type :refer :all]))

^{:refer std.contract.type/check :added "3.0"}
(fact "checks that data fits the spec")

^{:refer std.contract.type/common-spec-invoke :added "3.0"}
(fact "invokes the common spec")

^{:refer std.contract.type/common-spec-string :added "3.0"}
(fact "displays the common spec")

^{:refer std.contract.type/combine :added "3.0"}
(fact "combines spec schemas (usually maps)")

^{:refer std.contract.type/common-spec :added "3.0"}
(fact "creates a common spec")

^{:refer std.contract.type/defspec :added "3.0"}
(fact "macro for defining a spec")

^{:refer std.contract.type/multi-spec-invoke :added "3.0"}
(fact "invokes the multi spec")

^{:refer std.contract.type/multi-spec-string :added "3.0"}
(fact "displays the multi spec")

^{:refer std.contract.type/multi-gen-final :added "3.0"}
(fact "generates the final schema for a multispec")

^{:refer std.contract.type/multi-spec-add :added "3.0"}
(fact "adds additional types to the multi spec")

^{:refer std.contract.type/multi-spec-remove :added "3.0"}
(fact "removes additional types from the multi spec")

^{:refer std.contract.type/multi-spec :added "3.0"}
(fact "creates a multi spec")

^{:refer std.contract.type/defmultispec :added "3.0"}
(fact "macro for defining a multispec")

^{:refer std.contract.type/defcase :added "3.0"}
(fact "adds an additional case to the multispec")

^{:refer std.contract.type/spec? :added "3.0"}
(fact "checks that object is of type spec")

^{:refer std.contract.type/valid? :added "3.0"}
(fact "checks that data is valid")

(comment

  (defmultispec <hello> :type)

  (defcase <hello> :human {:type :human
                           :id string?})

  (<hello> {:type :human :id "oeuoeu"})

  (multi-spec :type
              {:human {:type :human
                       :data {:name string?
                              :age integer?}}})

  (-> (mc/schema [:map [:id string?]])
      (mu/closed-schema)
      (mc/explain {:idc 1})
      (me/with-spell-checking)
      (me/humanize)))
