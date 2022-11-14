(ns std.lang.base.impl-lifecycle-test
  (:use code.test)
  (:require [std.lang.base.impl-lifecycle :refer :all]
            [rt.postgres.script.scratch]
            [lua.nginx]
            [lua.nginx.task]
            [xt.lang.base-lib :as k]
            [xt.lang]
            [std.lang :as l]
            [std.lib :as h]))

^{:refer std.lang.base.impl-lifecycle/emit-module-prep :added "4.0"}
(fact "prepares the module for emit"
  ^:hidden
  
  (-> (emit-module-prep 'xt.lang
                        {:lang :lua})
      second
      keys
      sort)
  => '(:code :export :link :native :setup :teardown)

  (:link (second (emit-module-prep 'xt.lang.base-lib
                                   {:lang :lua
                                    :emit {:compile {:root-ns 'lua}}})))
  => ()
  
  (:link (second (emit-module-prep 'lua.nginx
                                   {:lang :lua
                                    :emit {:compile {:root-ns 'lua}}})))
  => ()
  
  (:link (second (emit-module-prep 'lua.nginx.task
                                   {:lang :lua
                                    :emit {:compile {:root-ns 'lua}}})))
  => '(["./xt/lang/base-lib" {:as k, :ns xt.lang.base-lib}]
       ["./xt/sys/cache-common" {:as cache, :ns xt.sys.cache-common}])
  

  (:link (second (emit-module-prep 'lua.nginx.task
                                   {:lang :lua
                                    :emit {:compile {:root-ns 'xt.lang}}})))
  => '(["./lang/base-lib" {:as k, :ns xt.lang.base-lib}]
       ["./sys/cache-common" {:as cache, :ns xt.sys.cache-common}]))

^{:refer std.lang.base.impl-lifecycle/emit-module-setup-concat :added "4.0"}
(fact "joins setup raw into individual blocks")

^{:refer std.lang.base.impl-lifecycle/emit-module-setup-join :added "4.0"}
(fact "joins setup raw into the setup code"
  ^:hidden
  
  (-> (emit-module-setup-raw 'rt.postgres.script.scratch
                             {:lang :postgres
                              :emit {:export {:suppress true}
                                     :code   {:label true}}})
      (emit-module-setup-join))
  => string?)

^{:refer std.lang.base.impl-lifecycle/emit-module-setup-raw :added "4.0"}
(fact "creates module setup map of array strings"
  ^:hidden
  
  (emit-module-setup-raw 'lua.nginx
                         {:lang :lua})
  => map?
  
  (emit-module-setup-raw 'rt.postgres.script.scratch
                         {:lang :postgres
                          :emit {:export {:suppress true}
                                 :code   {:label true}}})
  => map?)

^{:refer std.lang.base.impl-lifecycle/emit-module-setup :added "4.0"}
(fact "emits the entire module as string"
  ^:hidden
  
  (:link (second (emit-module-prep 'xt.lang
                                   {:lang :lua
                                    :graph {:root-ns 'lua}})))
  => '{}
  
  (emit-module-setup 'xt.lang.base-lib
                     {:lang :lua})
  => string?

  (def -out-
    (emit-module-setup
     'rt.postgres.script.scratch
     {:lang :postgres
      :emit {:export {:suppress true}
             :native {:suppress true}
             :link   {:suppress true}
             :code   {:label true}}}))
  -out-
  => string?)

^{:refer std.lang.base.impl-lifecycle/emit-module-teardown-concat :added "4.0"}
(fact "joins teardown raw into individual blocks"

  (-> (emit-module-teardown-raw 'xt.lang
                                {:lang :lua})
      (emit-module-teardown-concat))
  => coll?)

^{:refer std.lang.base.impl-lifecycle/emit-module-teardown-join :added "4.0"}
(fact "joins teardown raw into code"

  (-> (emit-module-teardown-raw 'xt.lang
                                {:lang :lua})
      (emit-module-teardown-join))
  => string?)

^{:refer std.lang.base.impl-lifecycle/emit-module-teardown-raw :added "4.0"}
(fact "creates module teardown map of array strings")

^{:refer std.lang.base.impl-lifecycle/emit-module-teardown :added "4.0"}
(fact "creates the teardown script"
  ^:hidden
  
  (def -out-
    (emit-module-teardown
     'rt.postgres.script.scratch
     {:lang :postgres
      :emit {:code   {:suppress true}}}))
  
  -out-
  => string?

  (emit-module-teardown 'xt.lang
                        {:lang :lua
                         :layout :full})
  => string?
  
  (emit-module-teardown 'xt.lang
                        {:lang :lua
                         :layout :full
                         :emit {:code {:suppress true}}})
  => "")

(comment
  (./import))
