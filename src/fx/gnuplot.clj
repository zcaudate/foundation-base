(ns fx.gnuplot
  (:require [fx.gui.data.return :as return]
            [fx.gnuplot.command :as command]
            [std.concurrent :as cc]
            [std.string :as str]
            [std.lib :as h]))

(def ^:dynamic *default* nil)

(def ^:dynamic *command* "gnuplot")

(def ^:dynamic *options*
  {:send     {:quiet true
              :save false}
   :receive  {:save false}})

(defn generate-script
  "creates the gnuplot script"
  {:added "4.0"}
  ([input]
   (str (command/command input))))

(defn gnuplot:create
  "creates a gnuplot
 
   (gnuplot:create {})"
  {:added "3.0"}
  ([opts]
   (cc/relay:create {:type :process
                     :args [*command*]
                     :options (merge opts *options*)})))

(def +resource+
  (h/res:spec-add
   {:type :hara/fx.gnuplot
    :instance {:create   gnuplot:create
               :setup    (fn [gp]
                           (h/start gp)
                           (h/set! *default* gp)
                           gp)
               :teardown (fn [gp]
                           (h/stop gp)
                           (h/set! *default* nil)
                           gp)}}))

(defn get-gnuplot
  "gets existing or creates a new gnuplot
 
   (get-gnuplot)"
  {:added "3.0"}
  ([]
   (or *default*
       (h/res :hara/fx.gnuplot))))

(defn prepare-input
  "prepares command input"
  {:added "3.0"}
  ([input]
   (cond (and (vector? input)
              (coll? (first input)))
         input

         (coll? input)
         [input]

         (nil? input)
         [[]]

         :else
         [[input]])))

(defn cmd
  "enter commands to gnuplot"
  {:added "3.0"}
  ([input]
   (cmd input {}))
  ([input entry]
   (cmd (get-gnuplot) input entry))
  ([gplot input entry]
   (let [input (prepare-input input)
         commands (str (command/command input))]
     (if commands
       (cc/send gplot (merge {:line commands} entry)))
     commands)))

(defn plot-prepare
  "prepares the plot data"
  {:added "3.0"}
  ([input {:keys [terminal] :as settings}]
   (let [terminal (cond (nil? terminal)
                        [:qt :size '(500 340)]

                        (keyword? terminal)
                        [terminal :size '(500 340)]

                        :else terminal)
         settings (assoc settings :terminal terminal)
         input    (if (and (sequential? input)
                           (or (number? (first input))
                               (and (sequential? (first input))
                                    (number? (ffirst input)))))
                    {:data (vec input)}
                    input)
         inputs   (if (vector? input) input [input])
         prepared (mapv (fn [{:keys [data title using input autoscale] :as m
                              :or {input #{"-"}}}]
                          (let [using (or using (if (= input #{"-"})
                                                  "1:2"))
                                title (if title
                                        (if (set? title) title #{title}))]
                            {:plot (cond-> (dissoc m :data)
                                     :then (assoc :input input)
                                     using (assoc :using using)
                                     title (assoc :title title))
                             :data data}))
                        inputs)]
     [settings prepared])))

(defn input-points
  "formats the input points"
  {:added "3.0"}
  ([data]
   (if (vector? (first data))
     (map (fn [arr] (str/join " " (map str arr))) data)
     (map (fn [i p] (str i " " p)) (range) data))))

(defn plot-inputs
  "plots points form inputs"
  {:added "3.0"}
  ([gplot plots dataset]
   (plot-inputs gplot plots dataset :plot))
  ([gplot plots dataset command]
   (let [plot-str (str (command/command [[command (apply list plots)]]))
         _  (cc/send gplot {:op :partial :line plot-str})
         _  (doseq [data dataset]
              (doseq [points (->> (input-points data)
                                  (partition-all 500)
                                  (map #(str/join "\n" %)))]
                (cc/send gplot {:op :partial :line points}))
              (cc/send gplot {:op :partial :line "e"}))]
     (assoc @(cc/send gplot {:op :bytes})
            :plot-str plot-str))))

(defn plot-process
  "processes the plot input"
  {:added "3.0"}
  ([output {:keys [terminal title] :as settings}]
   (let [[type size] (if (vector? terminal)
                       [(first terminal)
                        (if-let [[w h] (:size (apply hash-map (rest terminal)))]
                          [(+ 20 w) (+ 40 h)])]
                       [terminal '(500 340)])]
     (return/process-output output (merge settings
                                          {:title title
                                           :type type
                                           :size size})))))

(defn plot
  "plots data in gnuplot
   
   (plot [{:title \"Lag\" :using \"1:2:3\" :with :yerrorbars :lt [:palette :cb -45]
           :data (mapv vector (range 50) (range 50) (take 50 (repeatedly #(* 5 (rand)))))}
          {:title \"Time\" :with :points :lt [:rgb #{\"#FF00FF\"}]
           :data (vec (take 50 (repeatedly #(rand-int 50))))}]
         {:autoscale true})"
  {:added "3.0"}
  ([input]
   (plot input {}))
  ([input settings]
   (plot (get-gnuplot) input settings))
  ([gplot input {:keys [setup reset] :as settings}]
   (let [[settings prepared] (plot-prepare input settings)
         plots    (map :plot prepared)
         dataset  (keep :data prepared)
         length   (count dataset)
         special  [:return :title :save :command :reset :setup]
         commands (cond->> [(apply dissoc settings special)]
                    setup (concat setup)
                    (not (false? reset)) (cons [:reset :session]))
         cmd-str  (cmd gplot (vec commands) {:op :partial})
         plot-cmd (or (:command settings) :plot)
         {:keys [output] :as m} (plot-inputs gplot plots dataset plot-cmd)
         _        (plot-process output settings)]
     (assoc m
            :counts (mapv count dataset)
            :command-str  cmd-str))))

(comment

  (require 'math.infix)
  (math.infix/>)
  (math.infix)
  
  (plot {:input ["[10000:20000]" (str "50+sin(x/3)+2*sin(x/7)+2*sin(x/11)+2*sin(x/13)+2*sin(x/17)+2*sin(x/19)"
                                  "+4*sin(x/71)+4*sin(x/111)+4*sin(x/131)+4*sin(x/151)"
                                  "+4*sin(x/1111)+4*sin(x/1311)+4*sin(x/1711)")]}
        {:terminal :svg
         :title "Hello"}))
