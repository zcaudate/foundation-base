(ns std.lang.base.compile-test
  (:use code.test)
  (:require [std.lang.base.compile :refer :all]))

^{:refer std.lang.base.compile/compile-script :added "4.0"}
(fact "compiles a script")

^{:refer std.lang.base.compile/compile-module-single :added "4.0"}
(fact "compiles a single module")

^{:refer std.lang.base.compile/compile-module-graph :added "4.0"}
(fact "compiles a module graph")

^{:refer std.lang.base.compile/compile-module-schema :added "4.0"}
(fact "compiles all namespaces into a single file (for sql)")


(comment
  ^{:refer std.make.compile/compile-script :added "4.0"}
  (fact "compiles a script with dependencies when given a "
    ^:hidden
    
    (with:mock-compile
     (compile-script {:root   ".build"
                      :target "src"
                      :file   "pkg/file.lua"
                      :main f/to-array
                      :layout :flat
                      :entry {:label true}}))
    => [".build/src/pkg/file.lua"
        (std.string/|
         "-- L.core.functional/reduce"
         "local function reduce(f,acc,seq)"
         "  if (type(seq) == 'function') then"
         "    for v in seq do"
         "      local res, terminate = f(acc,v)"
         "      acc = res"
         "      if terminate then"
         "        return acc"
         "      end"
         "    end"
         "  else"
         "    for _, v in ipairs(seq) do"
         "      local res, terminate = f(acc,v)"
         "      acc = res"
         "      if terminate then"
         "        return acc"
         "      end"
         "    end"
         "  end"
         "  return acc"
         "end"
         ""
         "-- L.core.functional/to-array"
         "local function to_array(seq)"
         "  local out = {}"
         "  reduce(function (_,v)"
         "    table.insert(out,v)"
         "  end,nil,seq)"
         "  return out"
         "end")])

  ^{:refer std.make.compile/compile-module :added "4.0"}
  (fact "compiles a module"
    ^:hidden

    
    (with:mock-compile
     (compile-module 'L.core
                     {:lang :lua
                      :root   ".build"
                      :target "src"
                      :file   "pkg/file.lua"}))
    => (contains
        [".build/src/pkg/file.lua"
         string?]))

  ^{:refer std.make.compile/compile-graph :added "4.0"}
  (fact "compiles a root module and all dependencies"
    ^:hidden
    
    (require 'js.blessed.form-test)
    (with:mock-compile
     (compile-graph 
      {:lang :js
       :main 'js.blessed.form-test
       :root   ".build"
       :target "src"}))
    => coll?)

  ^{:refer std.make.compile/compile-schema :added "4.0"}
  (fact "compiles entire schema of")

  
  )
