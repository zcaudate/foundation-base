(ns play.tui-003-sensor.main
  (:require [std.lang :as l]
            [std.lib :as h]
            [std.string :as str]))

(l/script :js
  {:require  [[js.core  :as j :include [:node :util]]
              [js.react :as r]
              [js.blessed :as b :include [:fn]]]})

(defn.js useActuation
  [#{[onActive
      onIndicator
      hold
      (:= initial  false)
      (:= interval 20)
      (:= speed  3)
      (:= resolution 1000)]}]
  (let [clear        (r/ref)
        [active
         setActive]  (r/local initial)
        _            (r/sync active onActive)


        
        [indicator
         setIndicator] (r/local (:? active [resolution 0]))
        _    (r/watch [active indicator]
               (if onIndicator (onIndicator (/ indicator resolution))))]
    
    ;; Transient
    (r/watch [active indicator]
      (cond hold
            (r/curr:set clear nil)

            (and (< indicator resolution)
                 active)
            (let [tmr (j/delayed [interval]
                        (setIndicator (+ indicator
                                         (j/ceil
                                          (* (j/sqrt (- resolution
                                                         indicator))
                                             speed)))))]
              (r/curr:set clear (fn [] (j/clearInterval tmr)))
              (return (r/curr clear)))
            
            (and (> indicator 0)
                 (not active))
            (let [tmr (j/delayed [interval]
                        (setIndicator (- indicator
                                         (j/ceil
                                          (* (j/sqrt indicator)
                                             speed)))))]
              (r/curr:set clear (fn [] (j/clearInterval tmr)))
              (return (r/curr clear)))

            :else
            (r/curr:set clear nil)))
    
    (return [active    setActive
             indicator setIndicator
             clear])))


(defn.js Switch
  ([#{disabled
      selected
      onSelected
      onIndicator
      textOn
      textOff
      paramsIndicator}]
   (let [_ (:= paramsIndicator
               (or paramsIndicator
                   {:interval 20
                    :speed 3
                    :resolution 1000}))
         [__selected
          __setSelected] (-/useActuation (j/assign
                                          {:active  selected
                                           :onActive onSelected 
                                           :onIndicator onIndicator
                                           :initial selected}
                                          paramsIndicator))]
     (return [:button {:shrink true
                       :mouse true
                       :onClick (fn [e] (__setSelected (not __selected)))
                       :content (:?  __selected
                                         [textOn
                                          textOff])}]))))

(defn.js Pressure
  ([#{disabled
      pressed
      onPressed
      onPressure
      textPressed
      textReleased
      paramsPressure}]
   (let [_ (:= paramsPressure
               (or paramsPressure
                   {:interval 20
                    :speed 3
                    :resolution 1000}))
         [__pressed
          __setPressed] (-/useActuation (j/assign
                                         {:active  selected
                                          :onActive onPressed
                                          :onIndicator onPressure
                                          :initial pressed}
                                         paramsPressure))]
     (return [:button {:shrink true
                       :mouse true
                       :onMouse (fn [e]
                                  (if (== (. e button) "left")
                                    (if (and (== (. e action)
                                                 "mouseup"))
                                      (__setPressed false)
                                      (__setPressed true))))
                       :content (:? __pressed
                                    [textPressed
                                     textReleased])}]))))


(defn.js MenuToggle
  ([#{disabled
      pressed
      onPressed
      selected
      onSelected
      onIntensity
      text
      paramsPressure
      paramsIndicator}]
   (let [ref   (r/ref)
         _ (:= paramsPressure
               (j/assign 
                {:interval 50
                 :speed 6
                 :resolution 1000}
                paramsPressure))
         
         _ (:= paramsIndicator
               (j/assign
                {:interval 50
                 :speed 6
                 :resolution 1000}
                paramsIndicator))

         ;;
         ;;
         ;; INTENSITY
         ;;
         ;; 
         [__intensity
          __setIntensity]   (r/local (:? selected [1 0]))
         _    (r/sync __intensity onIntensity)
         
         
         ;;
         ;;
         ;; PRESSURE
         ;; 
         ;;
         [__pressed
          __setPressed
          __pressure 
          __setPressure
          __stopPressure]   (-/useActuation
                             (j/assign
                              {:onActive onPressed
                               :initial pressed}
                              paramsPressure))
         
         ;;
         ;;
         ;; SELECTED
         ;; 
         ;;
         [__selected
          __setSelected
          __indicator
          __setIndicator
          __stopIndicator] (-/useActuation
                             (j/assign
                              {:hold    __pressed
                               :active  selected
                               :onActive onSelected 
                               :initial selected}
                              paramsIndicator))

         ;; TWO WAY BINDING
         _    (r/sync __selected onSelected)
         _    (r/watch [selected]
                (if (not= selected __selected)
                  (__setSelected selected)))]

     
     
     ;; For continuity of on indicator when pressure is released
     (r/watch [__pressed __selected]
       (when (and (not __pressed)
                  __selected)
         (if (r/curr __stopIndicator)
           ((r/curr __stopIndicator)))
         (if (r/curr __stopPressure)
           ((r/curr __stopPressure)))
         (__setIndicator (j/max __indicator __pressure))
         (__setPressure (- __pressure 1))))
                  

     ;; For continuity of on indicator when pressure reaches the max amount
     (r/watch [__indicator __pressure]
       (cond (and __pressed
                  (not __selected)
                  (< __indicator 900))
             (when (> __pressure 990)
               (__setIndicator __pressure)
               (__setPressure 0)))
                    
       (if (> __indicator 990)
         (__setIntensity (/ (+ (* 0.8 (j/max __indicator __pressure))
                               (* 0.2 __pressure))
                            1000))
         (__setIntensity (/ (* 0.8 (j/max __indicator __pressure))
                            1000))))

     
     ;;
     ;; ISSUE-to4jrshyr9sz
     ;;
     ;; This adds pressure release for mouseout
     ;; A better solution should be found other
     ;; than listening to the Window cursor but
     ;; this works for now.
     ;;
     #_(let [#{x y} (r/useSnapshot -/Window)]
       (r/watch [x y]
         (when __pressed
           (when (and (< x ref.current.aleft)
                      (> x (+ ref.current.aleft ref.current.width))
                      (< y ref.current.atop)
                      (> y (+ ref.current.atop ref.current.height))))
           (__setPressed false)
           (__setIndicator (- __indicator 1)))))
     
     
     (return [:box
              [:button {:ref ref
                        :shrink true
                        :bg "blue"
                        :mouse true
                        :onMouse (fn [e]
                                   (if (== (. e button) "left")
                                     (do (cond
                                           (== (. e action) "mouseup")
                                           (__setPressed false)
                                           
                                                
                                           (== (. e action) "mousedown")
                                           (__setPressed true))) ))
                        :onClick (fn [e] (__setSelected (not __selected)))
                        :content (+ text " " #_(j/write #{__selected
                                                        selected
                                                        __intensity
                                                        __indicator
                                                        __pressure}))}]
              [:box   {:bg "red"
                       :top 1
                       :height 1
                       :width (* __intensity 30)}]]))))




(defn.js Menu
  ([props]
   (let [[index setIndex] (r/local 1)]
     (return [:box
              [:box {:top 0}
               [:% -/MenuToggle {:onSelected  (fn [v]
                                                (if v
                                                  (setIndex 0)
                                                  (if (== index 0)
                                                    (setIndex nil))))
                                 :selected (== index 0)
                                 :paramsPressure {:speed 6}
                                 :text "CHOICE 0 "}]]
              [:box {:top 3}
               [:% -/MenuToggle {:onSelected  (fn [v]
                                                (if v
                                                  (setIndex 1)
                                                  (if (== index 1)
                                                    (setIndex nil))))
                                 :selected (== index 1)
                                 :paramsPressure {:speed 6}
                                 :text "CHOICE 1 "}]]
              [:box {:top 6}
               [:% -/MenuToggle {:onSelected  (fn [v]
                                                (if v
                                                  (setIndex 2)
                                                  (if (== index 2)
                                                    (setIndex nil))))
                                 :selected (== index 2)
                                 :paramsPressure {:speed 6}
                                 :text "CHOICE 2 "}]]]))))

(defn.js App
  ([]
   (let [[pressed  setPressed]   (r/local false)
         [selected  setSelected] (r/local false)
         [intensity setIntensity] (r/local 0)]
     (return [:box
              [:box {:top 5 :left 10}
               [:% -/Menu]]
              [:box {:top 20
                     :shrink true
                     :left 10}
               
               [:% -/MenuToggle {:onSelected  setSelected
                                 :onPressed   setPressed
                                 
                                 :onIntensity setIntensity
                                 :paramsPressure {:speed 6}
                                 :text (+ (:? selected [" ON " " OFF "])
                                          (j/toFixed intensity 3))}]]]))))

(defrun.js __init__
  (do (:# (!:uuid))
      (b/run [:% -/App] "Tui 003 - Sensor")))

(comment
  (h/make:setup)
  (h/make:init)
  
  (h/make:dev)
  (binding [std.lib.make/*tmux* false]
    (h/make:dev)))






