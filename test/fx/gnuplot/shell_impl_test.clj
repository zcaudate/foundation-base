(ns fx.gnuplot.shell-impl-test)

(comment
  (do (require '[std.dispatch :as exe]
               '[std.log :as log]
               '[platform.scaffold :as scaffold]
               '[platform.cache :as cache]
               '[std.lib.component :as component]
               '[std.math :as math]
               '[platform.bench :as bench]
               '[hara.data.base.map :as map]
               '[std.string :as str]
               '[std.lib.walk :as walk]
               '[hara.shell :as sh]
               '[hara.shell.base.runtime :as rt]
               '[std.print.graphic :as graph]
               '[platform.bench.results :as results]
               '[platform.bench.stats :as stats]
               '[fx.gnuplot :as g])

      (def -cache- (atom {}))
      (def POOL_SIZE 100)
      (def LOAD_TIME 100)  ;; ms
      (def LOAD_UNIQUE 100)
      (def LOAD_DEPENDENCE (fn [] (math/rand-sample [1 2 3 4 5 6] [5 5 3 1 1 1])))

      (def ^:dynamic *topology*
        {:logger     [log/create]
         :executor   [exe/create :logger]
         :bench      [bench/create :executor]
         :collector  [bench/create :executor :bench]})

      (defn config []
        {:logger    {:type :cache
                     :publish "EVENTS"
                     :transform identity
                     :cache -cache-}
         :executor  {:type :board
                     :handler (fn [_ e]
                                (Thread/sleep LOAD_TIME)
                                e)
                     :hooks {:on-complete (fn [{:keys [logger] :as exe} _ _]
                                            (let [info (exe/info exe)
                                                  {:keys [busy dependent]} @(:board (:runtime exe))]
                                              (log/info logger
                                                        "COMPLETE"
                                                        {:current (:current info)
                                                         :counter (:counter info)
                                                         :busy (count busy)
                                                         :dependent (count dependent)})))}
                     :options {:board {:group-fn (fn [_ e]
                                                   e)}
                               :pool {:size POOL_SIZE :max POOL_SIZE}}}
         :bench     {:type    :scheduled
                     :profile {:init 2}
                     :pool    {:size 500}
                     :load    {:interval 100 :size 5000}
                     :stats   {:lag-10-secs   {:key :lag :group {:time 10000} :aggregate :mean :fn identity}
                               :lag           {:key :lag :group {:last 10} :aggregate :mean}
                               :time-10-secs  {:key :duration :group {:time 10000} :fn #(/ % 1000000)}
                               :time          {:key :duration :group {:last 10} :fn #(/ % 1000000)}
                               :items-10-secs {:key :output :group {:time 100000} :fn count}
                               :items         {:key :output :group {:last 10} :fn count}}
                     :stats2  {:lag-10-secs   {:key :lag :interval :10s :aggregate :max}
                               :lag           {:key :lag :interval 10 :sample 4}
                               :time-20-secs  {:key :duration :interval :20s :fn #(/ % 1000000)}
                               :time          {:key :duration :interval 20 :fn #(/ % 1000000)}
                               :items-20-secs {:key :output :interval :20s :fn count}
                               :items         {:key :output :interval 20 :fn count}}
                     :args    (fn [{:keys [executor] :as bench}]
                                {:executor executor})
                     :handler (fn [{:keys [executor]}]
                                @(exe/submit executor (->> (repeatedly (fn [] (rand-int LOAD_UNIQUE)))
                                                           (take (LOAD_DEPENDENCE)))))}
         :collector {:type   :collector
                     :profile {:init 1}
                     :load    {:interval 1000}
                     :args    (fn [bench] bench)
                     :indicators {:executor  (fn [{:keys [executor]}]
                                               (component/info executor))
                                  :bench     (fn [{:keys [bench]}]
                                               (component/info bench))}}})

      (defonce ^:dynamic *instance*
        (atom nil))
      (scaffold/register))

  (scaffold/create-system)
  (ident?)
  (scaffold/stop)
  (scaffold/stop)
  (scaffold/restart)

  (def +indicators+
    '{:lag-norm   (/ :bench.stats.lag   [:stats/max :bench.stats.lag])
      :time-norm  (/ :bench.stats.time  [:stats/max :bench.stats.time])
      :delay-norm (float (/ (- :executor.counter.queued :executor.counter.complete)
                            [:stats/max (- :executor.counter.queued :executor.counter.complete)]))})

  (def +funcs+ (stats/compile +indicators+))

  (def +outputs+ (drop 40 (map :output (:results (results/first-load (:collector @*instance*))))))

  (def +series+ (map/map-vals (fn [f] (f +outputs+))
                              +funcs+))

  (:delay-norm +series+)
  (:time-norm +series+)

  (g/plot (mapv (fn [i [k v]]
                  {:title (name k)
                   :with :lines
                   :lt i
                   :data (vec v)})
                (range (rand-int 10) 12)
                +series+)
          {:autoscale true})


  ;; group    {:cluster 1000 :sample 1 :span [:1m for :5m]}
  ;; group    {:cluster "1s" :sample 4 :aggregate :max :span [2m for 2m]}
  ;; group    {:cluster "1s" :sample 4 :aggregate :max :span [:0m :end]}

;; time      {:choose 4 :interval 1000 :span [40000 ]}
;; span      {:until 0 :duration 10000}
;; span      {:start 0 :end 10000 :sample 1000}
;; group     {:time    10000 :fn mean}
;; group     {:cluster 1000 :fn mean}
;; aggregate {:fn mean}


  (ss/process (:collector @*instance*)
              {:group {:sample 4}}

              :group  {:time 1000 :fn :mean}
              '{:lag-norm   (/ :bench.stats.lag   [:stats/max :bench.stats.lag])
                :time-norm  (/ :bench.stats.time  [:stats/max :bench.stats.time])
                :delay-norm (float (/ (- :executor.counter.queued :executor.counter.complete)
                                      [:stats/max (- :executor.counter.queued :executor.counter.complete)]))})

  #_(mapv vector
          (range)
          (map :start (drop 40 (:results (results/first-load (:collector @*instance*)))))
          (def +lag+ (stats/lag-fn (drop 40 (map :output (:results (results/first-load (:collector @*instance*)))))))
          (def +time+ (stats/time-fn (drop 40 (map :output (:results (results/first-load (:collector @*instance*)))))))
          #'hara.shell.gnuplat-impl-test/+lag+)

  (results/summary

   (:collector @*instance*))

  (first (results/summary

          (:bench @*instance*)))

  (-> (results/first-load (:collector @*instance*))
      (update :results
              #(take 30 (drop 40 %))))

  (results/first-load (time (prn (:bench @*instance*))))

  (do @(exe/submit (:executor @*instance*)
                   (->> (repeatedly (fn [] (rand-int LOAD_UNIQUE)))
                        (take (LOAD_DEPENDENCE))))
      (count (get-in @-cache- ["EVENTS" :value])))

  (type @*instance*)

  (def -sys- (-> (component/system *topology* *config*)
                 (component/start)))

  (take 10 (apply results/merge-sorted :start
                  (map :results (vals (results/active-loads (:bench @*instance*))))))

  (results/summary
   (:bench @*instance*))
  (results/summary
   (:collector @*instance*))

  (:executor @*instance*)
  (bench/increase-load (:bench @*instance*))
  (bench/decrease-load (:bench @*instance*))
  (bench/clear-load (:bench @*instance*))
  (def +p+ (future (bench/increase-load (:collector @*instance*))))
  [+p+ (:collector @*instance*)]
  (bench/decrease-load (:collector @*instance*))
  (map/map-vals (comp :step deref) (:active @(:runtime (:collector @*instance*))))
  (map/map-vals (comp :step deref) (:active @(:runtime (:bench @*instance*))))

  '("[3:3]")

  (map :step (:complete @(:runtime (:bench @*instance*))))

  (bench/info  (:collector @*instance*))

  (take 10 (:results @(first (vals (:active @(:runtime (:collector @*instance*)))))))

  (defn compile-accessor [accessor]
    (list 'fn '[output]
          (walk/postwalk (fn [x]
                           (cond (keyword? x)
                                 (list 'get-in 'output (mapv keyword (str/split (name x) #"\.")))

                                 (vector? x)
                                 (apply list x)

                                 :else x))
                         accessor)))

  (def -accessors-
    (map/map-vals (comp eval compile-accessor)
                  {:lag      ['- ['or :executor.counter.queued 0] '[or :executor.counter.complete 0]]
                   :duration ['quot ['or :bench.call.duration.last 0] 100000000]
                   :load     ['- ['or :bench.load.active 0] ['or :bench.load.stopping 0]]}))

  ;; (graph/print-sparkline (take-nth 3 (:duration -graphs-)))

  (sh/sh "gnuplot" {:process false
                    :in {:quiet true}})
  (def -rt- *1)
  (do (def -graphs-
        (map/map-vals (fn [f]
                        (mapv (fn [entry]
                                (f (:output entry)))
                              (reverse (:results @(first (vals (:active @(:runtime (:collector @*instance*)))))))
                              ;;(reverse (:results (first (:complete @(:runtime (:collector @*instance*))))))
                              ))
                      -accessors-))

      (count (:results @(first (vals (:active @(:runtime (:collector @*instance*)))))))
      (count (:results (first (:complete @(:runtime (:collector @*instance*))))))

      ;;(sh/in "set terminal qt")
      (do (sh/in "set datafile separator \",\"; plot '-' using 0:2 with lines title \"Load\", '' using 0:2 with lines title \"Duration\", '' using 0:2 with lines title \"Lag\"")
          (doseq [point (map (fn [i v]
                               (format "%d,%d" i (* v 10)))
                             (range)
                             (:load -graphs-))]
            (sh/in point))
          (sh/in "e")
          (doseq [point (map (fn [i v]
                               (format "%d,%d" i v))
                             (range)
                             (:duration -graphs-))]
            (sh/in point))
          (sh/in "e")
          (doseq [point (map (fn [i v]
                               (format "%d,%d" i v))
                             (range)
                             (:lag -graphs-))]
            (sh/in point))
          (sh/in "e")))

  (:duration -graphs-)
  ()

  (sh/stop))
