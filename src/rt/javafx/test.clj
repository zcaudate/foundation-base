(ns rt.javafx.test
  (:require [std.lang :as l]
            [std.lib :as h :refer [defimpl]]
            [std.html :as html]
            [std.json :as json]
            [std.string :as str]
            [org.httpkit.server :as httpkit]))

(defonce ^:dynamic *test-server* nil)

(defonce ^:dynamic *test-port* 6789)

(defn start-test
  "starts the test server"
  {:added "4.0"}
  ([]
   (if-not *test-server*
     (alter-var-root #'*test-server*
                     (fn [_]
                       (httpkit/run-server (fn [req]
                                             (case (:uri req)
                                               "/" {:status 200
                                                    :body (html/html [:html
                                                                      [:head
                                                                       [:meta {:http-equiv "Content-Security-Policy"
                                                                               :content (str/join ";"
                                                                                                  ["default-src * 'unsafe-inline'"
                                                                                                   "connect-src * 'unsafe-inline'"])}]]])}
                                               "/not-found" {:status 404
                                                             :headers {"Access-Control-Allow-Origin" "*"}
                                                             :body (json/write {:status "error"
                                                                                :data "Not Found"})}
                                               "/error" {:status 500
                                                         :headers {"Access-Control-Allow-Origin" "*"}
                                                         :body (json/write {:status "error"
                                                                            :data "Server Error"})}
                                               {:status 200
                                                :headers {"Access-Control-Allow-Origin" "*"}
                                                :body (json/write {:status "ok"
                                                                   :data {:label "abc"}})}))
                                           {:port *test-port*}))))))

(defn stop-test
  "stops the test server"
  {:added "4.0"}
  ([]
   (when *test-server*
     (*test-server*)
     (alter-var-root #'*test-server* (fn [_] nil)))))

