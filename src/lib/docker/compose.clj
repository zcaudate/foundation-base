(ns lib.docker.compose
  (:require [std.lib :as h]
            [std.string :as str]))

(defn create-compose-single
  [compose environment depends-on network ip-address]
  (let [keys (mapv first compose)
        vals (mapv second compose)
        lu   (zipmap keys vals)
        m    (cond-> (update-in lu [:environment] merge environment)
               (not-empty depends-on) (assoc :depends_on depends-on))
        keys (cond-> keys
               (not (get lu :environment))   (conj :environment)
               :then (conj :depends_on))]
    (cond-> (map (fn [k] [k (get m k)]) keys)
      :then (->> (filterv second))
      (not (:networks lu)) (conj 
                            [:networks
                             {network
                              (if ip-address
                                {:ipv4_address ip-address}
                                {})}]))))

(defn create-compose
  [{:keys [config
           network
           scaffold]}]
  (let [config  (h/map-entries (fn [[k m]]
                                 [k (assoc m :name (name k))])
                               config)
        order   (h/map-vals (fn [{:keys [name ip]}]
                              (if ip
                                (h/parse-long (last (str/split ip #"\.")))
                                name))
                            config)
        prep    (h/map-vals (fn [{:keys [type] :as m}]
                              ((or (scaffold type)
                                   (h/error "NOT FOUND:" {:type type}))
                               m))
                            config)
        environments (h/map-vals (fn [{:keys [deps environment]}]
                                   
                                   (->> deps
                                        (map (fn [k]
                                               (get-in prep [k :export])))
                                        (apply merge environment)))
                                 config)
        depends-on  (h/map-vals (fn [{:keys [deps]}]
                                  (mapv name deps))
                                config)]
    (->> prep
         (map (fn [[k {:keys [compose]}]]
                [k (create-compose-single
                    compose
                    (get environments k)
                    (get depends-on k)
                    network
                    (get-in config [k :ip]))]))
         (sort-by (comp order first))
         vec)))
