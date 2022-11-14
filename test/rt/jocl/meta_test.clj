(ns rt.jocl.meta-test
  (:use code.test)
  (:require [rt.jocl.meta :refer :all]
            [std.lib :as h])
  (:import (org.jocl CL)))

^{:refer rt.jocl.meta/cl-size :added "3.0"}
(fact "helper for cl-types"

  (cl-size "float")
  => '(org.jocl.Sizeof/float))

^{:refer rt.jocl.meta/cl-types :added "3.0"}
(fact "creates a lookup of all standard cl sizes"

  (cl-types))

^{:refer rt.jocl.meta/lookup:fn :added "3.0"}
(fact "builds a lookup function"
  
  ((lookup:fn {:a 1 :b 2}) 1)
  => :a)

^{:refer rt.jocl.meta/lookup:flags :added "3.0"}
(fact "builds a flags function"

  ((lookup:flags {:hello 1 :world 2 :foo 4 :bar 8}) 13)
  => #{:bar :hello :foo})

^{:refer rt.jocl.meta/lookup:build :added "3.0"}
(fact "builds a lookup for the metadata"

  ((lookup:build [:enum :device-type]) [4])
  => :gpu)

^{:refer rt.jocl.meta/array-form :added "3.0"}
(fact "creates a form given inputs"

  (array-form :int [1 2 3])
  => '(int-array [1 2 3])

  (array-form 'Object [1 2 3])
  => '(clojure.core/make-array Object [1 2 3]))

^{:refer rt.jocl.meta/to-ptr :added "3.0"}
(fact "pointer for array inputs")

^{:refer rt.jocl.meta/cl-call :added "3.0"}
(fact "creates a cl call given parameters"
  ^:hidden

  (cl-call
   {:sizet :long,
    :pointer true,
    :return h/string,
    :arrayt :byte,
    :prefix "CL_PLATFORM",
    :fn CL/clGetPlatformInfo,
    :key :platform,
    :class cl_platform_id}
   (platform:default)
   CL/CL_PLATFORM_NAME)
  => string?)

^{:refer rt.jocl.meta/fn-call :added "3.0"}
(fact "creates a cl call given a lookup key"
  ^:hidden
  
  (fn-call :platform-info {} (platform:default) CL/CL_PLATFORM_NAME)
  => string?)

^{:refer rt.jocl.meta/info-template:defn :added "3.0"}
(fact "creates a property template"
  ^:hidden

  (info-template:defn "platform" "CL_PLATFORM" [:name {}])
  => '[:name (defn platform-info:name
               ([platform]
                (fn-call :platform-info {} platform CL/CL_PLATFORM_NAME)))])

^{:refer rt.jocl.meta/info-template:print :added "3.0"}
(fact "creates a print template"
  ^:hidden
  
  (info-template:print :platform 'cl_platform_id [])
  => '(clojure.core/defmethod
        clojure.core/print-method cl_platform_id
        ([obj w] (.write w (clojure.core/str "#cl." "platform" " "
                                             (clojure.core/dissoc (platform-info obj)))))))

^{:refer rt.jocl.meta/info-template:gen :added "3.0"}
(fact "helper for `info-template`"

  (info-template:gen :platform nil))

^{:refer rt.jocl.meta/info-template :added "3.0"}
(fact "generates the info functions")

^{:refer rt.jocl.meta/info-gen :added "3.0"}
(fact "generates multiple info functions")

^{:refer rt.jocl.meta/list-platforms :added "3.0"}
(fact "lists all platforms"

  (list-platforms))

^{:refer rt.jocl.meta/platform:default :added "3.0"}
(fact "gets the default platform"

  (platform:default))

^{:refer rt.jocl.meta/list-devices :added "3.0"}
(fact "lists all devices"

  (list-devices (platform:default)))

^{:refer rt.jocl.meta/device-info :added "3.0"}
(fact "shows the device info"

  (device-info (device:cpu))
  => map?)

^{:refer rt.jocl.meta/device:cpu :added "3.0"}
(fact "gets the default cpu device")

^{:refer rt.jocl.meta/device:gpu :added "3.0"}
(fact "gets the default gpu device")
