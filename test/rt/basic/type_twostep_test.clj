(ns rt.basic.type-twostep-test
  (:use code.test)
  (:require [rt.basic.type-twostep :as p]
            [std.lang.model.spec-rust :as spec-rust]
            [rt.basic.impl.process-rust :as rust]
            [std.lang.base.pointer :as ptr]
            [std.lang.base.library :as lib]
            [std.lang.base.util :as ut]
            [std.lang.base.emit-prep-lua-test :as prep]
            [std.lang.base.library-snapshot :as snap]
            [std.lang.base.impl-entry :as entry]
            [std.lang.base.book :as book]
            [std.json :as json]
            [std.lib :as h]
            [std.lang :as l]))

(def +library-ext+
  (doto (lib/library:create
         {:snapshot (snap/snapshot {:rust {:id :rust
                                          :book prep/+book-min+}})})
    (lib/install-book! (dissoc spec-rust/+book+ :parent))))

^{:refer rt.basic.type-twostep/sh-exec :added "4.0"}
(fact "basic function for executing the compile and run process")

^{:refer rt.basic.type-twostep/raw-eval-twostep :added "4.0"}
(fact "evaluates the twostep evaluation"
  ^:hidden
  
  (p/raw-eval-twostep
   {:exec ["rustc"]
    :process {:emit {:body {:transform #'rt.basic.impl.process-rust/transform-form}},
              :exec "rustc",
              :extension "rs", :stderr true,
              :flags {:twostep [], :interactive false, :json false, :ws-client false}} }
   "fn main() {\n  println!(\"{}\",1 + 2 + 3 + 4); \n}")
  => "\n10")

^{:refer rt.basic.type-twostep/invoke-ptr-twostep :added "4.0"}
(fact "invokes twostep pointer"
  ^:hidden

  (p/invoke-ptr-twostep
   (p/rt-twostep {:lang :rust
                  :runtime :twostep})
   (ut/lang-pointer :rust {:library +library-ext+})
   ['(+ 1 2 3 4)])
  => 10)

^{:refer rt.basic.type-twostep/rt-twostep-setup :added "4.0"}
(fact "setup params for the twostep runtime"
  ^:hidden
  
  (p/rt-twostep-setup
   :rust :rustc nil nil :twostep )
  => [:rustc {:emit {:body {:transform #'rt.basic.impl.process-rust/transform-form}},
              :exec "rustc", :extension "rs",
              :stderr true, :flags {:twostep [],
                                    :interactive false, :json false, :ws-client false}}
      ["rustc"]])

^{:refer rt.basic.type-twostep/rt-twostep:create :added "4.0"}
(fact "creates a twostep runtime"
  ^:hidden
  
  (->> (p/rt-twostep:create {:lang :rust
                             :program :rustc})
       (into {}))
  => map?)

^{:refer rt.basic.type-twostep/rt-twostep :added "4.0"}
(fact "creates an active twostep runtime"
  ^:hidden
  
  (->> (p/rt-twostep {:lang :rust
                      :program :rustc})
       (into {}))
  => map?)


(comment
  #_#_#_
    (lib/install-module! :rust 'L.util
                         {#_#_#_#_
                          :require '[[L.core :as u]]
                          :import '[["cjson" :as cjson]]})
    (lib/add-entry-single!
     (entry/create-code-base
      '(defn sub-fn
         [a b]
         (return ((u/identity-fn u/sub) a b)))
      {:lang :rust
       :namespace (h/ns-sym)
       :module 'L.util
       :line 1}
      {}))
    (lib/add-entry-single!
     (entry/create-code-base
      '(defn add-fn
         [a b]
         (return ((u/identity-fn u/add) a (-/sub-fn b 0))))
      {:lang :rust
       :namespace (h/ns-sym)
       :module 'L.util
       :line 2}
      {})))
