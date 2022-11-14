(ns std.protocol.watch)

(defprotocol IWatch
  (-add-watch    [obj k f opts])
  (-has-watch    [obj k opts])
  (-remove-watch [obj k opts])
  (-list-watch   [obj opts]))
