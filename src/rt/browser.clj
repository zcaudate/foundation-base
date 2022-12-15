(ns rt.browser
  (:require [std.lib :as h]
            [std.lang :as l]
            [rt.browser.spec :as spec]
            [rt.browser.impl :as impl]
            [rt.browser.connection :as conn]
            [rt.browser.util :as util])
  (:refer-clojure :exclude [send get-method]))

(h/intern-in
 spec/get-domain
 spec/get-method
 spec/list-domains
 spec/list-methods
 impl/browser
 impl/browser:create)

(h/template-entries [spec/tmpl-browser]
  [[send conn/send]
   [evaluate util/runtime-evaluate]
   [screenshot util/page-capture-screenshot]
   [target-close  util/target-close]
   [target-create util/target-create]
   [target-info   util/target-info]
   [page-navigate util/page-navigate]])

(defn goto
  "goto a given page"
  {:added "4.0"}
  [url & [timeout rt]]
  (let [rt (or rt (l/rt :js))]
    @(page-navigate rt url {} (or timeout 5000))
    @(evaluate rt impl/+bootstrap+)
    @(target-info rt)))

(comment
  playground/play-url
  playground/play-file
  playground/play-script
  playground/play-files
  playground/play-worker
  playground/start-playground
  playground/stop-playground)