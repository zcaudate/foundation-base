(ns xt.demo.brainfuck
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :xtalk
  {:require [[xt.lang.base-lib :as k]]
   :export [MODULE]})

(defn.xt brainfuck-interpreter
  "Interprets and executes Brainfuck code"
  {:added "1.0"}
  [bfCode]
  ;; Initialize memory, pointer, and output
  (var memory {})
  (k/for:index [i [(x:offset 0)
                   (x:offset 30000)]]
    (= (. memory [i]) 0))
  (var output "")
  (var codeLength (k/len bfCode))
  (var i 0) ;; Instruction pointer

  ;; Execute Brainfuck commands
  (while (< i codeLength)
    (var command (k/get-idx bfCode i))
    (cond
      ;; Increment the pointer
      (== command ">") (:= pointer (+ pointer 1))

      ;; Decrement the pointer
      (== command "<") (:= pointer (- pointer 1))

      ;; Increment the value at the current cell
      (== command "+") 
      (k/set-idx memory pointer (mod (+ (k/get-idx memory pointer) 1) 256))

      ;; Decrement the value at the current cell
      (== command "-") 
      (k/set-idx memory pointer (mod (- (+ (k/get-idx memory pointer) 256) 1) 256))

      ;; Output the character at the current cell
      (== command ".") 
      (:= output (k/cat output (k/char (k/get-idx memory pointer))))

      ;; Input a character into the current cell
      (== command ",") 
      (do
        (var input (k/read "Input a character:"))
        (k/set-idx memory pointer (:? input (k/char-code (k/first input)) 0)))

      ;; Jump forward if the current cell is 0
      (== command "[") 
      (if (== (k/get-idx memory pointer) 0)
        (do
          (var bracketCounter 1)
          (while (> bracketCounter 0)
            (:= i (+ i 1))
            (if (== (k/get-idx bfCode i) "[")
              (:= bracketCounter (+ bracketCounter 1)))
            (if (== (k/get-idx bfCode i) "]")
              (:= bracketCounter (- bracketCounter 1))))))

      ;; Jump backward if the current cell is non-zero
      (== command "]") 
      (if (not= (k/get-idx memory pointer) 0)
        (do
          (var bracketCounter 1)
          (while (> bracketCounter 0)
            (:= i (- i 1))
            (if (== (k/get-idx bfCode i) "]")
              (:= bracketCounter (+ bracketCounter 1)))
            (if (== (k/get-idx bfCode i) "[")
              (:= bracketCounter (- bracketCounter 1))))))))
  ;; Move to the next instruction
  (:= i (+ i 1))

  ;; Return the output
  (return output))

(def.xt MODULE (!:module))
