(ns code.test.checker.logic
  (:require [code.test.checker.common :as common]))

(defn is-not
  "checker that allows negative composition of checkers
 
   (mapv (is-not even?)
         [1 2 3 4 5])
   => [true false true false true]"
  {:added "3.0"}
  ([ck]
   (is-not ck identity))
  ([ck function]
   (let [ck (common/->checker ck)]
     (common/checker
      {:tag :is-not
       :doc "Checks if the result is not an outcome"
       :fn (fn [res]
             (not (ck (function res))))
       :expect ck}))))

(defn any
  "checker that allows `or` composition of checkers
 
   (mapv (any even? 1)
         [1 2 3 4 5])
   => [true true false true false]"
  {:added "3.0"}
  ([& cks]
   (let [cks (mapv common/->checker cks)]
     (common/checker
      {:tag :any
       :doc "Checks if the result matches any of the checkers"
       :fn (fn [res]
             (or (->> cks
                      (map #(common/verify % res))
                      (some common/succeeded?))
                 false))
       :expect cks}))))

(defn all
  "checker that allows `and` composition of checkers
 
   (mapv (all even? #(< 3 %))
         [1 2 3 4 5])
   => [false false false true false]"
  {:added "3.0"}
  ([& cks]
   (let [cks (mapv common/->checker cks)]
     (common/checker
      {:tag :all
       :doc "Checks if the result matches all of the checkers"
       :fn (fn [res]
             (->> cks
                  (map #(common/verify % res))
                  (every? common/succeeded?)))
       :expect cks}))))
