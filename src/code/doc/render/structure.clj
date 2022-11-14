(ns code.doc.render.structure)

(def containers
  #{:article :chapter :appendix :section :subsection :subsubsection :generic})

(def hierarchy
  (-> (make-hierarchy)
      (derive :article :generic)
      (derive :article :chapter)
      (derive :article :appendix)
      (derive :container :section)
      (derive :appendix :section)
      (derive :chapter :section)
      (derive :section :subsection)
      (derive :subsection :subsubsection)))

(defn inclusive
  "checks is a section is within another
 
   (inclusive :article :chapter)
   => true
 
   (inclusive :chapter :section)
   => true
 
   (inclusive :section :chapter)
   => false"
  {:added "3.0"}
  ([x y]
   (if (get containers y)
     (if (not (isa? hierarchy y x))
       true
       false)
     true)))

(defn separate
  "separates elements into various structures"
  {:added "3.0"}
  ([f v]
   (separate f (rest v) [] (if (first v)
                             [(first v)]
                             [])))
  ([f [ele & more] all current]
   (cond (nil? ele)
         (conj all current)

         (f ele)
         (recur f more (conj all current) [ele])

         :else
         (recur f more all (conj current ele)))))

(defn containify
  "puts a flat element structure into containers"
  {:added "3.0"}
  ([v]
   (->> (containify v [#{:appendix :chapter :generic} :section :subsection :subsubsection])
        (filterv identity)
        (cons {:type :article})
        vec))
  ([v [tag & more]]
   (let [eq? (fn [tag ele]
               (if (set? tag)
                 (get tag (:type ele))
                 (= tag (:type ele))))
         subv (separate (fn [ele] (eq? tag ele)) v)]
     (cond (empty? more)
           subv

           :else
           (mapv (fn [[head & body]]
                   (cond
                     (empty? body) [head]

                     :else
                     (vec (cons head (containify body more)))))
                 subv)))))

(declare mapify)

(defn mapify-unit
  "helper class for mapify"
  {:added "3.0"}
  ([x]
   (cond (map? x) x
         :else (mapify x))))

(defn mapify
  "creates the hierarchical structure for a flat list of elements"
  {:added "3.0"}
  ([[head & more]]
   (cond (get containers (:type head))
         (assoc head :elements (vec (flatten (map mapify-unit more))))

         :else
         (mapv mapify-unit (cons head more)))))

(defn structure
  "creates a structure for the article and its elements"
  {:added "3.0"}
  ([v]
   (cond (empty? v)
         {:type :article :elements []}

         :else
         (-> v
             (containify)
             (mapify)))))

(comment
  (structure [])
  (mapify [])
  (containify []))
