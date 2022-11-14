(ns std.dom.invoke-test
  (:use code.test)
  (:require [std.dom.invoke :refer :all]
            [std.dom.type :as type]
            [std.lib :refer [definvoke]]))

^{:refer std.dom.invoke/invoke-intern-dom :added "3.0"}
(fact "constructor for dom"

  (invoke-intern-dom nil 'hello {:class :react
                                 :tag :test/hello-dom} [['dom '_] 1])
  
  (invoke-intern-dom nil 'hello {:class :value
                                 :tag :test/hello-dom} [['_] 1]))

(comment
  (./import)
  (type/metaprops :houma/fruit-chart)
  (definvoke k2-element
    [:dom {:tag   :test/element
           :metaclass :dom/element
           :class :element}])
  
  (k2-element {})
  
  (definvoke k2-data
    [:dom {:tag   :k2/data
           :class :value}]
    ([{:keys [children]}]
     (vec children)))
  
  (definvoke fruit-chart
    [:dom {:tag   :houma/fruit-chart1
           :class :react}]
    ([dom _]
     (base/dom-compile [:fx/label "hello"])))
  
  (base/dom-item (impl/dom-render (fruit-chart {})))

  (base/dom-item (impl/dom-render (k2-data {} [1 2 3 4 5])))
  
  (type/metaprops-remove :houma/fruit-chart1)
  (resolve 'na)
  
  
  (comment
  (fx/display [:houma/fruit-chart {:state (atom {})
                                   :cursor [:data]}]
              dom/dom-state-handler))
  (definvoke )

  
  (let [[doc attr [class params] & body]
        (fn/create-args (apply vector doc? attr? bindings body))]
    `(let [~'template (fn ~@body)]
       (component-install ~tag ~class ~'template ~params)))

  )
