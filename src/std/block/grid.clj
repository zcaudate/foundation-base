(ns std.block.grid
  (:require [clojure.java.io :as io]
            [std.block.base :as base]
            [std.block.construct :as construct]
            [std.block.parse :as parse]
            [std.block.type :as type]
            [std.lib :as h]))

(def read-resource (comp read-string slurp io/resource))

(def ^:dynamic *bind-length* 2)

(def ^:dynamic *indent-length* 1)

(defn trim-left
  "removes whitespace nodes on the left
 
   (->> (trim-left [(construct/space)
                    :a
                    (construct/space)])
        (mapv str))
   => [\":a\" \"␣\"]"
  {:added "3.0"}
  ([blocks]
   (drop-while type/void-block? blocks)))

(defn trim-right
  "removes whitespace nodes on the right
 
   (->> (trim-right [(construct/space)
                     :a
                     (construct/space)])
        (mapv str))
   => [\"␣\" \":a\"]"
  {:added "3.0"}
  ([blocks]
   (->> (reverse blocks)
        (drop-while type/linespace-block?)
        (reverse))))

(defn split-lines
  "splits the nodes, retaining the linebreak nodes
 
   (split-lines [:a :b (construct/newline) :c :d])
   => [[:a :b]
       [(construct/newline) :c :d]]"
  {:added "3.0"}
  ([blocks]
   (loop [acc []
          out []
          [block & more] blocks]
     (cond (nil? block)
           (if (empty? acc)
             out
             (conj out acc))

           (type/linebreak-block? block)
           (recur [block] (conj out acc) more)

           :else
           (recur (conj acc block) out more)))))

(defn remove-starting-spaces
  "removes extra spaces from the start of the line
 
   (remove-starting-spaces [[(construct/newline)
                             (construct/space)
                             (construct/space) :a :b]
                            [(construct/newline) (construct/space) :c :d]])
   => [[(construct/newline) :a :b]
       [(construct/newline) :c :d]]"
  {:added "3.0"}
  ([lines]
   (map (fn [[start & more :as line]]
          (cond (type/linebreak-block? start)
                (cons start (drop-while type/linespace-block? more))

                :else line))
        lines)))

(defn adjust-comments
  "adds additional newlines after comments
 
   (->> (adjust-comments [(construct/comment \";hello\") :a])
        (mapv str))
   => [\";hello\" \"\\\n\" \":a\"]"
  {:added "3.0"}
  ([blocks]
   (loop [out []
          [block & more] blocks]
     (cond (nil? block)
           out

           (and (type/comment-block? block)
                (not (type/linebreak-block? (first more))))
           (recur (conj out block (construct/newline)) more)

           :else
           (recur (conj out block) more)))))

(defn remove-extra-linebreaks
  "removes extra linebreaks
 
   (remove-extra-linebreaks [[:a]
                             [(construct/newline)]
                             [(construct/newline)]
                             [(construct/newline)]
                             [:b]])
   => [[:a]
       [(construct/newline)]
       [:b]]"
  {:added "3.0"}
  ([lines]
   (loop [out []
          [[lchar & lrest :as line] & [[nchar & nrest] :as more]] lines]
     (cond (nil? line)
           out

           (and (type/linebreak-block? lchar)
                (type/linebreak-block? nchar)
                (every? type/void-block? lrest)
                (every? type/void-block? nrest))
           (recur out more)

           :else
           (recur (conj out line) more)))))

(defn grid-scope
  "calculates the grid scope for child nodes
 
   (grid-scope [{0 1} 1])
   => [{0 1} 0]"
  {:added "3.0"}
  ([pscope]
   (mapv (fn [input]
           (cond (map? input)
                 input

                 :else
                 (dec input)))
         pscope)))

(defn grid-rules
  "creates indentation rules for current block
 
   (grid-rules :list nil nil nil)
   => {:indent 0, :bind 0, :scope []}
 
   (grid-rules :vector nil nil nil)
   => {:indent 0, :bind 0, :scope [0]}
 
   (grid-rules :list 'add [1] nil)
   => {:indent 1, :bind 0, :scope [0]}
 
   (grid-rules :list 'if nil '{if {:indent 1}})
   => {:indent 1, :bind 0, :scope []}"
  {:added "3.0"}
  ([tag sym pscope rules]
   (cond (and (= :list tag) pscope)
         {:indent *indent-length*
          :bind 0
          :scope (grid-scope pscope)}

         (or (= :list tag)
             (= :fn tag))
         (let [{:keys [bind indent scope] :as entry
                :or {indent 0
                     bind 0
                     scope []}} (get rules sym)]
           {:indent indent
            :bind bind
            :scope scope})

         :else {:indent 0 :bind 0 :scope [0]})))

(defn indent-bind
  "returns the number of lines indentation
 
   (indent-bind [[(construct/token 'if-let)]
                 [(construct/newline)]
                 [(construct/newline) (construct/block '[i (pos? 0)])]
                 [(construct/newline) (construct/block '(+ i 1))]]
                1)
   => 2"
  {:added "3.0"}
  ([lines bind]
   (let [expr-sum   (map (fn [line]
                           (count (filter base/expression? line)))
                         lines)
         expr-cul  (reduce (fn [out i]
                             (conj out (+ (last out) i)))
                           [(first expr-sum)]
                           (rest expr-sum))
         idx  (h/index-at #(<= (inc bind) %) expr-cul)
         idx  (if (neg? idx)
                (dec (count lines))
                idx)]
     idx)))

(defn indent-lines
  "indent lines given an anchor and rule
 
   (-> (indent-lines [[(construct/token 'if-let)]
                      [(construct/newline)]
                      [(construct/newline) (construct/block '[i (pos? 0)])]
                     [(construct/newline) (construct/block '(+ i 1))]]
                     1
                     {:indent 1
                      :bind 1})
       (construct/contents))"
  {:added "3.0"}
  ([[start & more :as lines] anchor {:keys [indent bind scope] :as rule}]
   (let [start-num        (count (filter base/expression? start))
         [initial others]  (cond (zero? bind)
                                 (cond (and (= 1 start-num)
                                            (empty? (filter zero? (filter number? scope))))
                                       [nil indent]

                                       (not (empty? (filter zero? (filter number? scope))))
                                       [nil indent]

                                       :else
                                       [nil (+ indent
                                               (base/block-width (first start))
                                               (->> (rest start)
                                                    (take-while type/void-block?)
                                                    (map base/block-width)
                                                    (apply +)))])
                                 :else
                                 (let [bind-num (indent-bind lines bind)]
                                   [(repeat bind-num (+ indent *bind-length*))
                                    indent]))
         indents (concat initial (repeat (count more) others))
         lines   (->> (map (fn [[nl & tokens] indent]
                             (->> tokens
                                  (concat (construct/spaces (+ anchor indent)))
                                  (cons nl)))
                           more
                           indents)
                      (cons start))]
     lines)))

(defn grid
  "formats lines given indentation rules and scope
 
   (-> (construct/block ^:list ['if-let
                                (construct/newline)
                                (construct/newline) (construct/block '[i (pos? 0)])
                               (construct/newline) (construct/block '(+ i 1))])
       (grid 1 {:rules {'if-let {:indent 1
                                 :bind 1}}})
       (construct/contents))"
  {:added "3.0"}
  ([container anchor {:keys [rules scope] :as opts}]
   (let [tag     (base/block-tag container)
         blocks  (base/block-children container)
         prefix  (base/block-prefixed container)
         blocks  (-> blocks
                     (trim-left)
                     (trim-right)
                     (adjust-comments))
         sym     (base/block-value (first blocks))
         rule    (grid-rules tag sym scope rules)
         lines   (->> (split-lines blocks)
                      (remove-starting-spaces))
         lines   (indent-lines lines (+ anchor prefix) rule)
         lines   (remove-extra-linebreaks lines)
         blocks  (apply concat lines)]
     (base/replace-children container blocks))))

(comment)
