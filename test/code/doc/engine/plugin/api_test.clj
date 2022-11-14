(ns code.doc.engine.plugin.api-test
  (:use code.test)
  (:require [code.doc.engine.plugin.api :refer :all]))

^{:refer code.doc.engine.plugin.api/entry-tag :added "3.0"}
(fact "helper for formating vars"

  (entry-tag 'code.doc 'output-path)
  = "entry__code.doc__output_path")

^{:refer code.doc.engine.plugin.api/lower-first :added "3.0"}
(fact "converts the first letter to lowercase"

  (lower-first "Hello")
  => "hello")

^{:refer code.doc.engine.plugin.api/api-entry-example :added "3.0"}
(fact "helper function to convert a test entry into a html tree form")

^{:refer code.doc.engine.plugin.api/api-entry-source :added "3.0"}
(fact "helper function to convert a source entry into a html tree form")

^{:refer code.doc.engine.plugin.api/api-entry :added "3.0"}
(fact "formats a `ns/var` pair tag into an html element")

^{:refer code.doc.engine.plugin.api/select-entries :added "3.0"}
(fact "selects api entries based on filters")

^{:refer code.doc.engine.plugin.api/api-element :added "3.0"}
(fact "displays the entire `:api` namespace")
