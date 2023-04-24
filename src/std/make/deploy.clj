(ns std.make.deploy
  (:require [std.lib :as h]
            [std.string :as str]
            [std.print.format.common :as format]
            [std.make.common :as common]
            [std.make.compile :as compile]
            [std.make.project :as project]
            [std.make.github :as github]
            [std.fs :as fs]))

(defn make-deploy-get-keys
  "deploy get keys"
  {:added "4.0"}
  [changed config]
  (let [changed-keys (set (keys (h/filter-vals identity changed)))
        deploy-order  (h/topological-sort
                      (h/map-vals (comp set :deps) config))
        deploy-keys   (reduce (fn [acc k]
                               (if (empty?
                                    (h/intersection
                                     acc
                                     (or (get-in config [k :deps])
                                         #{})))
                                 acc
                                 (conj acc k)))
                             changed-keys
                             deploy-order)]
    [(filter deploy-keys deploy-order)
     deploy-order]))

(defn make-deploy-build
  [m]
  (let [{:keys [name
                build
                refresh
                scaffold
                config]} m
        built    (h/map-entries
                  (fn [[key plan]]
                    (let [_       (h/local :print
                                           (format/pad:right
                                            (str (h/strn key)  " (" (:tag @(:instance plan)) ")")
                                            30))
                          [ms result]  (h/meter-out (project/build-all plan))
                          changed (project/changed-files result)]
                      (h/local :print
                               (if (not-empty changed) "UPDATE REQUIRED" "NO CHANGE")
                               " (" (h/format-ms ms) ")\n")
                      (when changed
                        (doseq [file changed]
                          (h/p file)))
                      [key [ms changed]]))
                  (if (vector? build)
                    (select-keys scaffold build)
                    scaffold))
        changed  (h/map-vals (comp not empty? second) built)
        changed  (cond (= refresh :all)
                       (h/map-vals h/T config)

                       (vector? refresh)
                       (merge changed
                              (h/map-juxt [identity h/T] refresh))
                       
                       :else
                       changed)]
    [built changed]))

(defn make-deploy
  "make deploy"
  {:added "4.0"}
  [m]
  (let [{:keys [name
                build
                refresh
                scaffold
                config]} m
        _      (do (h/p)
                   (h/p "--------------------------------------------------------------------")
                   (h/p "DEPLOYMENT STARTED --" (str/upper-case name))
                   (h/p "--------------------------------------------------------------------"))
        [built changed]    (make-deploy-build m)
        [deploying
         deploy-order] (make-deploy-get-keys changed config)
        _        (do (h/p)
                     (if (empty? deploying)
                       (h/p "ALL UPDATED")
                       (h/p "DEPLOYING" (str/join ", " (map h/strn deploying)))))
        [ms-total deployed] (h/meter-out
                       (h/map-juxt
                        [identity
                         (fn [key]
                           (Thread/sleep 50)
                           (first
                            (common/make-run-internal
                             (or (get scaffold key)
                                 (h/error "SCAFFOLD NOT FOUND"
                                          {:key key
                                           :options (keys scaffold)}))
                             (or (get-in config [key :action])
                                 (h/error "CONFIG NOT FOUND"
                                          {:key key
                                           :options (keys config)})))))]
                        deploying))
        _         (Thread/sleep 50)
        _         (do (h/p "--------------------------------------------------------------------")
                      (h/p "DEPLOMENT COMPLETE --" (str/upper-case name))
                      (h/p "--------------------------------------------------------------------")
                      (doseq [k deploy-order]
                        (let [[t-b] (get built k)
                              [t-d] (get deployed k)]
                          (when (or t-b t-d)
                            (h/p (format/pad:right (h/strn k) 15)
                                 (format/pad:right (str "BUILD  " (h/format-ms (or t-b 0)))
                                                   20)
                                 (if t-d
                                   (format/pad:right (str "DEPLOY " (h/format-ms (or t-d 0)))
                                                     20)
                                   "-")))))
                      (h/p)
                      (h/p "TOTAL" (h/format-ms ms-total))
                      (h/p))]
    {:built built
     :deployed deployed
     :total ms-total}))

(defn make-deploy-gh-init
  "make deploy init github"
  {:added "4.0"}
  [scaffold & [message]]
  (h/map-vals (fn [mcfg]
                (h/p "--------------------------------------------------------------------")
                (h/p "GITHUB INIT --" (:tag @(:instance mcfg)))
                (h/p "--------------------------------------------------------------------")
                (project/build-all mcfg)
                (github/gh-dwim-init mcfg message))
              (sort-by first scaffold)))

(defn make-deploy-gh-push
  "make deploy push github"
  {:added "4.0"}
  [scaffold & [message]]
  (h/map-vals (fn [mcfg]
                (h/p "--------------------------------------------------------------------")
                (h/p "GITHUB PUSH --" (:tag @(:instance mcfg)))
                (h/p "--------------------------------------------------------------------")
                (project/build-all mcfg)
                (github/gh-dwim-push mcfg message))
              (sort-by first scaffold)))
