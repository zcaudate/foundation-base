(ns documentation.std-block)

[[:chapter {:title "Introduction"}]]

"[std.block](https://github.com/zcaudate-xyz/foundation-base/blob/master/src/std/block.clj) provides methods for representation of code."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:

    [xyz.zcaudate/base \"{{PROJECT.version}}\"]"

"All functionality is in the `std.block` namespace:"

(comment
  (use 'std.block))
  
[[:chapter {:title "Block" :link "std.block" :module ["std.block.base"]}]]

[[:api {:namespace "std.block.base"}]]

[[:chapter {:title "Type" :link "std.block" :module ["std.block.type"]}]]

[[:api {:namespace "std.block.type"}]]

[[:chapter {:title "Construct" :link "std.block" :module ["std.block.construct"]}]]

[[:api {:namespace "std.block.construct"}]]

[[:chapter {:title "Parse" :link "std.block" :module ["std.block.parse"]}]]

[[:api {:namespace "std.block.parse"}]]
