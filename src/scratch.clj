(ns hello
  (:require [std.lang :as l]
            [std.lib :as h]
            [clojure.set :as set]))

(l/script :js)

#_{:clj-kondo/ignore [:unresolved-symbol]}
(!.js
  (fn []
    #_{:clj-kondo/ignore [:invalid-arity]}
    (var a 1)
    (var b 2)
    (return
     (+ 1 2 3))))


(defn.js add-10
  [x]
  (return (+ x 10)))







(l/script :python
  {:runtime :basic
   :require [[xt.lang.base-lib :as k]]})


(defn.py add-20
  [y]
  (return (+ y 20)))


;;;
;;; TIC TAC TOE
;;;
;;; [[AA AB AC]]
;;; [[BA BB BC]]
;;; [[CA CB CC]]
;;;

(defn new-game
  []
  {:board {:bg #{:aa :ab :ac
                 :ba :bb :bc
                 :ca :cb :cc}
           :p1 #{}
           :p2 #{}}
   :turn   :p1
   :status :active
   :winner nil})

(def +winning-conditions+
  [#{:aa :ab :ac}
   #{:ba :bb :bc}
   #{:ca :cb :cc}

   #{:aa :ba :ca}
   #{:ab :bb :cb}
   #{:ac :bc :cc}

   #{:aa :bb :cc}
   #{:ac :bb :ca}])

(defn check-win
  [board]
  (boolean
   (some (fn [c]
           (set/subset? c board))
         +winning-conditions+)))

(defn next-move
  [game move]
  (let [[side pos] move
        {:keys [board turn status]} game
        _ (when (#{:finished} status)
            (throw (ex-info "Game has finished." {:game game :move move})))
        _ (when (not= turn side)
            (throw (ex-info (str "Not " side "'s turn") {:game game :move move})))
        {:keys [bg p1 p2]} board
        _ (when (not (contains? bg pos))
            (throw (ex-info "Position already taken." {:game game :move move})))

        ;; Update board
        new-board (-> board
                      (update :bg disj pos)
                      (update side conj pos))
        
        ;; Check for winner 
        is-winner (check-win (get new-board side))

        ;; Check for full
        is-full   (empty? (:bg new-board))]
    {:board new-board
     :turn   (if (= side :p1) :p2 :p1)
     :status (if (or is-winner
                     is-full)
               :done
               :active)
     :winner (cond is-winner
                   side

                   is-full
                   :draw)}))








(comment
  
  
  (check-win #{:aa :ab :cc})
  
  (add-10 10)
  (add-20 10)

  )
