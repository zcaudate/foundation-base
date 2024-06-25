(ns std.lang.model.spec-rust-test
  (:use code.test)
  (:require [std.lang.model.spec-rust :refer :all]
            [std.lang.base.script :as script]
            [std.lang.base.util :as ut]
            [std.lang :as l]
            [std.lib :as h]
            [std.fs :as fs]
            [std.string]))

(script/script- :rust
  {;;:require [[std :as h]]
   :import  [["std::mem"]]
   })

(comment
  ^{:# [[lang := "owned_box"]
        [lang := "owned_box"]]}
  hello)

(fact "various test cases for rust code"
  ^:hidden

  (!.rs
    (defstruct ^{:- [[T Sized]]}
      Box))
  
  (!.rs
    (defimpl  ^{:- [[T Sized]]}
      Box
      

      (fn ^{:- [:String]} get [&self])))
  

  (!.rs
    (deftrait UsernameWidget

      (fn ^{:- [:String]} get [&self])))
  
  )

(fact "various test cases for rust code"
  ^:hidden

  (!.rs
    (defn main []
      (println! "{}" 123)))
  => (std.string/|
      "fn main() {"
      "  println!(\"{}\",123);"
      "}")
  
  (!.rs
    (var :mutable (:f64 number) 1.0))
  => "let mutable number: f64 = 1.0;"
  
  (!.rs
    (var (:f64 number) 1.0))
  => "let number: f64 = 1.0;"

  (!.rs
    (var (:f64 n) 1.0))
  => "let n: f64 = 1.0;"


  (!.rs
    (:to 0 3))
  
  (!.rs
    (Class$$new))
  => "Class::new();"
  
  (!.rs
    Class$$new)
  => "Class::new();"
  
  
  (!.rs
    (defn main []
      (println! "{subject} {verb} {object}"
                :object "the lazy dog"
                :verb   "jumps over"
                :subject "the quick brown fox")))
  => (std.string/|
      "fn main() {"
      "  println!("
      "    \"{subject} {verb} {object}\","
      "    object=\"the lazy dog\","
      "    verb=\"jumps over\","
      "    subject=\"the quick brown fox\""
      "  );"
      "}")
  
  (!.rs
    (defn is-divisible-by [:u32 lhs
                           :u32 rhs]
      (if (== rhs 0)
        (return false))
      
      (return
       (== 0 (mod lhs rhs)))))
  => (std.string/|
      "fn is_divisible_by(lhs: u32, rhs: u32) {"
      "  if(rhs == 0){"
      "    return false;"
      "  }"
      "  return 0 == (lhs % rhs);"
      "}")

  (!.rs
    (defn main []
      (println! "Base 10:    {}"    69420)
      (println! "Base 2:     {:b}"  69420)
      (println! "Base 8:     {:o}"  69420)
      (println! "Base 16:    {:x}"  69420)

      (println! "{number:>5}"  :number 1)
      (println! "{number:0>5}"  :number 1)
      (println! "{number:0<5}"  :number 1)
      (println! "{number:0>width$}"
                :number 1
                :width  3)

      (println! "My name is {0}, {1} {0}", "Bond" "James")))
  => (std.string/|
      "fn main() {"
      "  println!(\"Base 10:    {}\",69420);"
      "  println!(\"Base 2:     {:b}\",69420);"
      "  println!(\"Base 8:     {:o}\",69420);"
      "  println!(\"Base 16:    {:x}\",69420);"
      "  println!(\"{number:>5}\",number=1);"
      "  println!(\"{number:0>5}\",number=1);"
      "  println!(\"{number:0<5}\",number=1);"
      "  println!(\"{number:0>width$}\",number=1,width=3);"
      "  println!(\"My name is {0}, {1} {0}\",\"Bond\",\"James\");"
      "}"))

(comment
  (l/rt:restart)
  (do (spit "hello.rs"
            (!.rs
              (defn main []
                (println! "Base 10:    {}"    69420)
                (println! "Base 2:     {:b}"  69420)
                (println! "Base 8:     {:o}"  69420)
                (println! "Base 16:    {:x}"  69420)

                (println! "{number:>5}"  :number 1)
                (println! "{number:0>5}"  :number 1)
                (println! "{number:0<5}"  :number 1)
                (println! "{number:0>width$}"
                          :number 1
                          :width  3)

                (println! "My name is {0}, {1} {0}", "Bond" "James"))))
      (h/sh {:args ["rustc" "hello.rs"]})
      (h/sh {:args ["hello"]})))

(comment

  (defenum WebEvent
    [[PageLoad]
     [PageUnload]
     [KeyPress [:char]]
     [Paste [:String]]
     [Click [[:i64 x]
             [:i64 y]]]])
  
  
  (defstruct Person
    [[:String name]
     [:u8 age]])
  
  (defstruct Unit)

  (defstruct Pair [:i32 :f32])

  (defstruct Point
    [[:f32 x]
     [:f32 y]])
  
  (defstruct Rectangle
    [[-/Point top-left]
     [-/Point bottom-right]])
  
  "
enum WebEvent {
    // An `enum` variant may either be `unit-like`,
    PageLoad,
    PageUnload,
    // like tuple structs,
    KeyPress(char),
    Paste(String),
    // or c-like structures.
    Click { x: i64, y: i64 },
}
"

  "
fn inspect(event: WebEvent) {
    match event {
        WebEvent::PageLoad => println!(\"page loaded\"),
        WebEvent::PageUnload => println!(\"page unloaded\"),
        // Destructure `c` from inside the `enum` variant.
        WebEvent::KeyPress(c) => println!(\"pressed '{}'.\", c),
        WebEvent::Paste(s) => println!(\"pasted \\\"{}\\\".\", s),
        // Destructure `Click` into `x` and `y`.
        WebEvent::Click { x, y } => {
            println!(\"clicked at x={}, y={}.\", x, y);
        },
    }
}
"
  
  (do (spit "hello.rs"
            "
#[derive(Debug)]
struct Person<'a> {
    name: &'a str,
    age: u8
}

fn main() {
    let name = \"Peter\";
    let age = 27;
    let peter = Person { name, age };

    // Pretty print
    println!(\"{:#?}\", peter);
}")
      (h/sh {:args ["rustc" "hello.rs"]})
      (h/sh {:args ["hello"]})))

  
  
  


(comment

  (!.rs
    (^{:- [:i32]} fn [:i32 i]
     (+ i 10)))
  
  (!.rs
    (mut (:f64 n) 1.0))
  
  (!.rs
    (var (:f64 n)))
  
  (!.rs
    (var (:f64  number) 1.0)
    (var (:bool number) 1.0)
    (var :mut number 1.0))
  
  (!.rs
    #_#_
    (var (:f64  number) 1.0)
    (var (:bool number) 1.0)
    (var :mut number 1.0))
  
  (!.rs
    (defn main []
      (println! "Base 10:    {}"    69420)
      (println! "Base 2:     {:b}"  69420)
      (println! "Base 8:     {:o}"  69420)
      (println! "Base 16:    {:x}"  69420)))
  
  
  
  )
