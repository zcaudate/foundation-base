(ns rt.postgres.grammar.common-tracker
  (:require [std.string :as str]
            [std.lib :as h :refer [defimpl]]))

(defn- tracker-string
  ([{:keys [name disable in out] :as tracker}]
   (str "#pg.tracker [" name "] "
        (cond-> {}
          disable (assoc :disable disable)
          in   (assoc :in true)
          out  (assoc :out true)))))

(defimpl Tracker []
  :string tracker-string)

(defn add-tracker
  "call to adjust data to that of the tracker"
  {:added "4.0"}
  ([params tracker spec op]
   (let [_ (if (get (:disable tracker) op)
             (h/error (str op " disabled for spec" {:op op
                                                    :spec spec})))]
     (cond (= :ignore (:track params))
           params
                 
           (and (:in tracker)
                (not (:track params))
                (not (get (:ignore tracker) op)))
           (h/error (str "`:track` key required for spec") {:spec spec})

           :else
           (assoc params :static/tracker tracker)))))

(defn tracker-map-in
  "creates the insert map"
  {:added "4.0"}
  ([{:static/keys [tracker]
     :keys [track]:as params}]
   (if-let [kmp (:create (:in tracker))]
     (h/map-vals (fn [k]
                   (list :->> track (str/snake-case (name k))))
                 kmp))))

(defn tracker-map-modify
  "creates the modify map"
  {:added "4.0"}
  ([{:static/keys [tracker]
     :keys [track]:as params}]
   (if-let [kmp (:modify (:in tracker))]
     (h/map-vals (fn [k]
                   (list :->> track (str/snake-case (name k))))
                 kmp))))
