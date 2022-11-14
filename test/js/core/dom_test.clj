(ns js.core.dom-test
  (:use code.test)
  (:require [std.lang :as  l]
            [std.lib :as h]
            [std.html :as html]
            [std.string :as str]
            [fx.gui :as gui]))

(l/script- :js
  {:runtime :javafx
   :config  {:preload [:react.min :react-dom.min]
             :dev {:print false}}
   :require [[js.core.dom :as dom]
             [rt.javafx.harness :as harness]]})

(fact:global
 {:setup [(l/rt:restart)]
  :teardown [(l/rt:stop)]})

^{:refer js.core.dom/body :added "4.0"
  :setup [(harness/setupRoot)
          (Thread/sleep 100)]}
(fact "gets the document body"
  
  (str (dom/body))
  => #"<HTMLBodyElement>")

^{:refer js.core.dom/id :added "4.0"
  :setup [(harness/teardownRoot)]}
(fact "gets element given id"
  ^:hidden
  
  (dom/id "root")
  => nil

  (harness/setupRoot)
  (str (dom/id "root"))
  => #"<HTMLDivElement>")

^{:refer js.core.dom/text :added "4.0"
  :setup [(harness/teardownRoot)
          (harness/setupRoot)]}
(fact "gets the text of a given root"
  ^:hidden
  
  (str/trim (dom/text "root"))
  => "root")

^{:refer js.core.dom/click :added "4.0"}
(fact "clicks on an element")

^{:refer js.core.dom/sel :added "4.0"
  :setup [(harness/teardownRoot)
          (harness/setupRoot)]}
(fact "selects an element based on id"
  ^:hidden
  
  (!.js (. (dom/sel "#root")
           textContent))
  => "root\n")

^{:refer js.core.dom/q :added "4.0"
  :setup [(l/rt:restart)]}
(fact "gets elements given query"
  ^:hidden

  (str (dom/q "#root"))
  => #"<NodeList>"
  
  (harness/setupRoot)
  (html/tree
   (!.js
    (. (dom/body) outerHTML)))
  => vector?

  (str (dom/q "#root"))
  => #"<NodeList>")

^{:refer js.core.dom/create :added "4.0"}
(fact "creates an element from tree form"
  ^:hidden
  
  (str (dom/create [:div {:class "hello"}
                    [:div {:class "world"}
                     "abc"]]))
  => #"<HTMLDivElement>")

^{:refer js.core.dom/appendChild :added "4.0"
  :setup [(harness/setupRoot)]}
(fact "appends a child to the dom"
  ^:hidden
  
  (!.js
   (dom/appendChild (dom/id "root")
                    (dom/create [:div "hello there"])))

  (str (dom/id "root"))
  => #"<HTMLDivElement>")

^{:refer js.core.dom/removeChild :added "4.0"
  :setup [(harness/setupRoot)]}
(fact "removes a child from the dom"
  ^:hidden
  
  (!.js
   (dom/removeChild (dom/body)
                    (dom/id "root")))

  (dom/id "root")
  => nil)

^{:refer js.core.dom/remove :added "4.0"
  :setup [(harness/setupRoot)]}
(fact "removes node from the dom"
  ^:hidden
  
  (!.js (dom/remove (dom/id "root"))
        (dom/id "root"))
  => nil)

^{:refer js.core.dom/setAttribute :added "4.0"
  :setup [(harness/setupRoot)]}
(fact "sets attribute of an element"
  ^:hidden
  
  (!.js
   (dom/setAttribute (dom/id "root")
                     "class"
                     "hello")
   (dom/text "root"))
  => "root\n")

^{:refer js.core.dom/getAttribute :added "4.0"
  :setup [(harness/setupRoot)]}
(fact  "gets the attribute of an element"
  ^:hidden
  
  (!.js
   (dom/setAttribute (dom/id "root")
                     "class"
                     "hello")
   (dom/getAttribute (dom/id "root") "id"))
  => "root")

^{:refer js.core.dom/classList :added "4.0"}
(fact "returns the class list"  
  ^:hidden
  
  (str (!.js
       (dom/classList (dom/id "root"))))
  => "<DOMTokenList>\nhello")

^{:refer js.core.dom/className :added "4.0"}
(fact "returns the class name"
  ^:hidden
  
  (!.js
   (dom/className (dom/id "root")))
  => "hello")

^{:refer js.core.dom/outer :added "4.0"}
(fact "gets the outer html of an element")

^{:refer js.core.dom/inner :added "4.0"}
(fact "gets the inner httml of an element")
