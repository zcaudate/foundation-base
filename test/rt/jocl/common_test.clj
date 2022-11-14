(ns rt.jocl.common-test
  (:use code.test)
  (:require [rt.jocl.common :refer :all]
            [std.lang :as l]
            [rt.jocl.meta :as meta]))

^{:refer rt.jocl.common/parse-spec :added "3.0"}
(fact "parses a kernel arglist"

  (parse-spec {:modifiers #{:__global :const :float :*}, :symbol 'a}
              '{c {:output true}})
  => '{:key :a, :symbol a, :type :float,
       :dsize 4, :buffer true, :const true, :input true}
  
  (parse-spec {:modifiers #{:__global :long :*}, :symbol 'c}
              '{c {:output true}})
  => '{:key :c, :symbol c, :type :long, :dsize 8,
       :buffer true, :const false, :output true})

^{:refer rt.jocl.common/entry-spec :added "3.0"}
(fact "converts a form entry into a spec"
  
  (entry-spec {:form '(defn sym [:__global :const :float :* a
                                 :__global :const :float :* b
                                 :__global :float :* c])}
              '{c {:output true}})
  => '({:key :a, :symbol a, :type :float, :dsize 4, :buffer true, :const true, :input true}
       {:key :b, :symbol b, :type :float, :dsize 4, :buffer true, :const true, :input true}
       {:key :c, :symbol c, :type :float, :dsize 4, :buffer true, :const false, :output true}))

^{:refer rt.jocl.common/with-error :added "3.0"}
(fact "helper function for creation within context")

^{:refer rt.jocl.common/cl-context :added "3.0"}
(fact "creates a cl context"

  (cl-context)
  => org.jocl.cl_context)

^{:refer rt.jocl.common/cl-queue :added "3.0"}
(fact "creates a command queue (within a context)"

  (cl-queue (cl-context) (meta/device:gpu))
  => org.jocl.cl_command_queue)

^{:refer rt.jocl.common/cl-program :added "3.0"}
(fact "creates a program from source"
  ^:hidden
  
  (cl-program (cl-context)
              (meta/device:gpu)
              (l/emit-as
               :c ['(defn ^{:- [:__kernel :void]} simple
                      [:__global :const :float :* a]
                      (var :int gid (get-global-id 0)))]))
  => org.jocl.cl_program)

^{:refer rt.jocl.common/cl-kernel :added "3.0"}
(fact "creates a kernel from the program"
  ^:hidden
  
  (cl-kernel (cl-program (cl-context)
                         (meta/device:gpu)
                         (l/emit-as
                          :c ['(defn ^{:- [:__kernel :void]} simple
                                 [:__global :const :float :* a]
                                 (var :int gid (get-global-id 0)))]))
             "simple")
  => org.jocl.cl_kernel)
