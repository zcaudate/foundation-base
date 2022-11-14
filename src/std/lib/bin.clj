(ns std.lib.bin
  (:require [std.lib.bin.buffer :as buffer]
            [std.lib.bin.type :as type]
            [std.lib.foundation :as h])
  (:refer-clojure :exclude [bytes]))

(h/intern-in buffer/buffer
             buffer/buffer-read
             buffer/buffer-write
             buffer/byte-buffer
             buffer/char-buffer
             buffer/short-buffer
             buffer/int-buffer
             buffer/long-buffer
             buffer/float-buffer
             buffer/double-buffer

             type/input-stream
             type/output-stream
             type/bitseq
             type/bitstr
             type/bitset
             type/bytes
             type/number)
