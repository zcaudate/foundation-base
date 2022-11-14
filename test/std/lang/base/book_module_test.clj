(ns std.lang.base.book-module-test
  (:use code.test)
  (:require [std.lang.base.book-module :refer :all]))

^{:refer std.lang.base.book-module/book-module? :added "4.0"}
(fact "checks of object is a book module")

^{:refer std.lang.base.book-module/book-module :added "4.0"}
(fact "creates a book module"
  ^:hidden
  
  (book-module {:id 'L.core
                :lang :lua
                :link '{- L.core}
                :declare {}
                :alias '{cr coroutine
                         t  table}
                :native   {}
                :code   '{identity {}}})
  => book-module?)

^{:refer std.lang.base.book-module/module-deps :added "4.0"}
(fact "gets dependencies for a given module"
  ^:hidden
  
  (-> (book-module {:id 'L.nginx
                    :lang :lua
                    :link '{-  L.nginx
                            u  L.core}
                    :declare {}
                    :alias '{cr coroutine
                             t  table}
                    :native   {}
                    :code   '{identity {}}})
      (module-deps 'L.nginx))
  => '#{L.core})
