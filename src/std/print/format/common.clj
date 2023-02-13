(ns std.print.format.common)

(def ^:dynamic *indent* 0)

(def ^:dynamic *indent-step* 3)

(def +space+ \space)

(def +pad+   \space)

(def +up+ "\033[1A")

(def +clearline+ "\033[2K")

(defn pad
  "creates `n` number of spaces
 
   (pad 1) => \"\"
   (pad 5) => \"\""
  {:added "3.0"}
  ([len & [pad-char]]
   (apply str (repeat len (or pad-char +pad+)))))

(defn pad:right
  "puts the content to the left, padding missing spaces
 
   (pad:right \"hello\" 10)
   => \"hello     \""
  {:added "3.0"}
  ([content length & [pad-char]]
   (str content (pad (- length (count content)) pad-char))))

(defn pad:center
  "puts the content at the center, padding missing spacing
 
   (pad:center \"hello\" 10)
   => \"hello   \""
  {:added "3.0"}
  ([content length & [pad-char]]
   (let [total (- length (count content))
         half  (long (/ total 2))
         [left right] (cond (even? total)
                            [half half]

                            :else
                            [half (inc half)])]
     (str (pad left pad-char) content (pad right pad-char)))))

(defn pad:left
  "puts the content to the right, padding missing spaces
 
   (pad:left \"hello\" 10)
   => \"hello\""
  {:added "3.0"}
  ([content length & [pad-char]]
   (str (pad (- length (count content))
             pad-char)
        content)))

(defn justify
  "justifies the content to a given alignment
 
   (justify :right \"hello\" 10)
   => \"hello\"
 
   (justify :left \"hello\" 10)
   => \"hello     \""
  {:added "3.0"}
  ([align content length]
   (case align
     :center (pad:center content length)
     :right  (pad:left content length)
     (pad:right content length))))

(defn indent
  "format lines with indent"
  {:added "3.0"}
  ([lines]
   (indent lines *indent*))
  ([lines indent]
   (if (or (nil? indent)
           (zero? indent))
     lines
     (let [space (apply str (repeat indent " "))]
       (map (partial str space) lines)))))

(defn pad:lines
  "creates new lines of n spaces
   (pad:lines [] 10 2)
   => [\"          \" \"          \"]"
  {:added "3.0"}
  ([lines length height]
   (let [line (apply str (repeat length +pad+))]
     (concat lines
             (repeat (- height (count lines)) line)))))

(def +style:pad+ {:t [" " " "]
                  :b [" " " "]
                  :v " "
                  :h " "
                  :s +pad+})

(def +style:line+ {:t ["┌" "┐"]
                   :b ["└" "┘"]
                   :v "│"
                   :h "-"
                   :s +pad+})

(defn border
  "formats a border around given lines"
  {:added "3.0"}
  ([lines]
   (border lines nil))
  ([lines width]
   (border lines width +style:line+))
  ([lines width style]
   (let [{:keys [t b v h s]} style
         width  (or width (apply max (map count lines)))
         hr     (apply str (repeat width h))
         top    (str (first t) hr (second t))
         bottom (str (first b) hr (second b))
         lines  (mapv (fn [line]
                        (let [pad (- width (count line))]
                          (str v line (apply str (repeat pad s)) v)))
                      lines)]
     (vec (cons top (conj lines bottom))))))
