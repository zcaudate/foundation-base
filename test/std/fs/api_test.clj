(ns std.fs.api-test
  (:use code.test)
  (:require [std.fs.api :refer :all]
            [std.fs.path :as path])
  (:refer-clojure :exclude [list resolve]))

^{:refer std.fs.api/create-directory :added "3.0" :class [:operation]}
(fact "creates a directory on the filesystem"

  (do (create-directory "test-scratch/.hello/.world/.foo")
      (path/directory? "test-scratch/.hello/.world/.foo"))
  => true

  (delete "test-scratch/.hello"))

^{:refer std.fs.api/create-symlink :added "3.0" :class [:operation]}
(fact "creates a symlink to another file"

  (do (create-symlink "test-scratch/project.lnk" "project.clj")
      (path/link? "test-scratch/project.lnk"))
  => true

  ^:hidden
  (delete "test-scratch/project.lnk"))

^{:refer std.fs.api/create-tmpfile :added "3.0" :class [:operation]}
(fact "creates a tempory file"

  (create-tmpfile)
  ;;#file:"/var/folders/rc/4nxjl26j50gffnkgm65ll8gr0000gp/T/tmp2270822955686495575"
  => java.io.File)

^{:refer std.fs.api/create-tmpdir :added "3.0" :class [:operation]}
(comment "creates a temp directory on the filesystem"

  (create-tmpdir)
  ;;=> #path:"/var/folders/d6/yrjldmsd4jd1h0nm970wmzl40000gn/T/4870108199331749225"
  )

^{:refer std.fs.api/select :added "3.0" :class [:operation]}
(fact "selects all the files in a directory"

  (->> (select "src/std/fs")
       (map #(path/relativize "std/fs" %))
       (map str)
       (sort)))

^{:refer std.fs.api/list :added "3.0"  :class [:operation]}
(comment "lists the files and attributes for a given directory"

  (list "src")

  (list "../hara/src/std/fs" {:recursive true}))

^{:refer std.fs.api/copy :added "3.0" :class [:operation]}
(fact "copies all specified files from one to another"

  (copy "src" ".src" {:include [".clj"]})
  => map?

  (delete ".src"))

^{:refer std.fs.api/copy-single :added "3.0" :class [:operation]}
(fact "copies a single file to a destination"

  (copy-single "project.clj"
               "test-scratch/project.clj.bak"
               {:options #{:replace-existing}})
  => (path/path "." "test-scratch/project.clj.bak")

  (delete "test-scratch/project.clj.bak"))

^{:refer std.fs.api/copy-into :added "3.0" :class [:operation]}
(fact "copies a single file to a destination"

  (copy-into "src/std/fs.clj"
             "test-scratch/std.fs")
  => vector?

  (delete "test-scratch/std.fs"))

^{:refer std.fs.api/delete :added "3.0" :class [:operation]}
(fact "copies all specified files from one to another"

  (do (copy "src/std/fs.clj" ".src/std/fs.clj")
      (delete ".src" {:include ["fs.clj"]}))
  => #{(str (path/path ".src/std/fs.clj"))}

  (delete ".src")
  => set?)

^{:refer std.fs.api/move :added "3.0" :class [:operation]}
(fact "moves a file or directory"

  (do (move "shortlist" ".shortlist")
      (move ".shortlist" "shortlist"))

  (move ".non-existent" ".moved")
  => {})
