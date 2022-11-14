(ns js.core.fetch-test
  (:use code.test)
  (:require [std.lang :as  l]
            [std.json :as json]
            [std.html :as html]
            [std.string :as str]
            [rt.javafx.test :as test]
            [xt.lang.base-repl :as repl]
            [xt.lang.base-notify :as notify]))

(l/script- :js
  {:runtime :basic
   :require [[js.core :as j]
             [xt.lang.base-repl :as repl]
             [xt.lang.base-lib :as k]]})

(fact:global
 {:setup [(test/start-test)
          (l/rt:restart)
          (!.js (:= (. globalThis ["fetch"])
                    (require "node-fetch")))]
  :teardown [(test/stop-test)
             (l/rt:stop)]})

^{:refer js.core.fetch/check-opts :added "4.0"}
(fact "checks for for errors ")

^{:refer js.core.fetch/fetch-fn :added "4.0"}
(fact "creates the fetch form with checks"
  ^:hidden
  
  (js.core.fetch/fetch-fn "https://api.github.com/users/zcaudate"
                          {:headers {"Accept" "application/vnd.github.v3+json"}})
  => '(fetch "https://api.github.com/users/zcaudate"
             {:headers {"Accept" "application/vnd.github.v3+json"}}))

^{:refer js.core.fetch/fetch :added "4.0"}
(fact "fetches a http request"
  ^:hidden

  (notify/wait-on :js
    (-> (j/fetch "http://localhost:6789/success"
                 {:as "json"})
        (j/then  (repl/>notify))))
  => {"status" "ok", "data" {"label" "abc"}}

  (notify/wait-on :js
   (-> (j/fetch "http://localhost:6789/error"
                {:as "json"})
       (j/then  (repl/>notify))))
  => {"status" "error", "data" "Server Error"}

  (notify/wait-on :js
   (-> (j/fetch "http://localhost:6789/not-found"
                {:as "json"})
       (j/then  (repl/>notify))))
  => {"status" "error", "data" "Not Found"})

^{:refer js.core.fetch/toText :added "4.0"}
(fact "returns the next promise as text"
  ^:hidden

  (notify/wait-on :js
   (-> (j/fetch "http://localhost:6789/success")
       (j/toText)
       (j/then (repl/>notify))))
  => "{\"status\":\"ok\",\"data\":{\"label\":\"abc\"}}")

^{:refer js.core.fetch/toJson :added "4.0"}
(fact "returns the next promise as json"
  ^:hidden

  (notify/wait-on :js
   (-> (j/fetch "http://localhost:6789/success")
       (j/toJson)
       (j/then (repl/>notify))))
  => {"status" "ok", "data" {"label" "abc"}})

^{:refer js.core.fetch/toBlob :added "4.0"}
(fact "returns the next promise as blob"
  ^:hidden
  
  @(notify/wait-on :js
    (-> (j/fetch "http://localhost:6789/success")
        (j/toBlob)
        (j/then (repl/>notify))))
  => "[object Blob]")

^{:refer js.core.fetch/toPrint :added "4.0"}
(fact "prints out the promise")

^{:refer js.core.fetch/toAssign :added "4.0"}
(fact "assigns the result to an object")

^{:refer js.core.fetch/fetch-api :added "4.0"
  :setup [(fact:global :setup)]}
(fact "api calls for fetch"
  ^:hidden

  (notify/wait-on :js
   (-> (j/fetch-api "http://localhost:6789/success")
       (j/then (repl/>notify))))
  => {"label" "abc"}

  (notify/wait-on :js
   (-> (j/fetch-api "http://localhost:6789/error")
       (j/catch  (fn:> [err] (err.json)))
       (j/then (repl/>notify))))
  => {"status" "error", "data" "Server Error"}

  (notify/wait-on :js
   (-> (j/fetch-api "http://localhost:6789/not-found")
       (j/catch  (fn:> [err] (err.json)))
       (j/then (repl/>notify))))
  => {"status" "error", "data" "Not Found"})

(comment
  
  (./ns:reset))
