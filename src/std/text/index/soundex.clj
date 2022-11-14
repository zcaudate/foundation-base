;; An implementation of the American Soundex algorithm, as described
;; at <http://en.wikipedia.org/wiki/Soundex>

(ns std.text.index.soundex
  (:require [std.string.common :as str]))

;; Consonant codings. `h`, `w` and the vowels are special cases, as we'll see later.
(def char-map
  {\b 1, \f 1, \p 1, \v 1
   \c 2, \g 2, \j 2, \k 2, \q 2, \s 2, \x 2, \z 2
   \d 3, \t 3
   \l 4
   \m 5, \n 5
   \r 6})

(defn stem
  "classifies a word based on its stem
 
   (stem \"hello\")
   => \"H400\"
 
   (stem \"hellah\")
   => \"H400\"
 
   (stem \"supercalifragilistiic\")
   => \"S162\""
  {:added "3.0"}
  ([word]
   (let [index (first word)]
     (loop [w (rest word)
            code-seq []
            collapse-next? false]
       (if (or (empty? w)
               (>= (count code-seq) 3))
         (.toUpperCase ^String (apply str index
                                      (take 3 (concat code-seq (repeat 0)))))
         (cond
         ;; if we have a `w` or `h`, we ignore it and allow collapsing
           (#{\w \h} (first w))          (recur (rest w) code-seq true)

         ;; if it's a vowel, we ignore it, but don't collapse any
         ;; following duplicate
           (#{\a \e \i \o \u} (first w)) (recur (rest w) code-seq false)

         ;; otherwise, we conj the next code on, respecting `collapse-next?`
           :else (let [next-code (char-map (first w))]
                   (if (and collapse-next? (= next-code (last code-seq)))
                     (recur (rest w) code-seq true)
                     (recur (rest w) (conj code-seq next-code) true)))))))))
