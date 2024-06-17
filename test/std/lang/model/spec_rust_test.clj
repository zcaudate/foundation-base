(ns std.lang.model.spec-rust-test
  (:use code.test)
  (:require [std.lang.model.spec-rust :refer :all]
            [std.lang.base.script :as script]
            [std.lang.base.util :as ut]
            [std.lib :as h]
            [std.fs :as fs]))

(script/script- :rust
  {})

(comment
  (do (spit "hello.rs"
            (!.rs
              (defn main []
                (println! "Base 10:    {}"    69420)
                (println! "Base 2:     {:b}"  69420)
                (println! "Base 8:     {:o}"  69420)
                (println! "Base 16:    {:x}"  69420))
              ))
      (h/sh {:args ["rustc" "hello.rs"]})
      (h/sh {:args ["hello"]})))


(comment

  (!.rs
    (defn main []
      (println! "Base 10:    {}"    69420)
      (println! "Base 2:     {:b}"  69420)
      (println! "Base 8:     {:o}"  69420)
      (println! "Base 16:    {:x}"  69420)))
  
  
  (!.rs
    (defn main []
      (println! "{subject} {verb} {object}"
                :object "the lazy dog"
                :verb   "jumps over"
                :subject "the quick brown fox"))
    )
  
  (!.rs
    (defn main []
      (println! "Hello, Woeuoeoeuorld!"))
    )
  
  (!.rs
    (defn main []
      (println! "{}" 123))
    )
  )
