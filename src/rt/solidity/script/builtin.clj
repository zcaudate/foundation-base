(ns rt.solidity.script.builtin
  (:require [std.lib :as h]
            [std.lang :as l]
            [std.string :as str])
  (:refer-clojure :exclude [assert require bytes]))

(l/script :solidity
  {:macro-only true})

(def +globals+
  '[abi.decode
    abi.encode
    abi.encodePacked
    abi.encodeWithSelector
    abi.encodeCall
    abi.encodeWithSignature
    bytes.concat
    string.concat
    block.basefee
    block.chainid
    block.coinbase
    block.difficulty
    block.gaslimit
    block.number
    block.timestamp
    gasleft
    msg.data
    msg.sender
    msg.sig
    msg.value
    tx.gasprice
    tx.origin
    assert
    require
    revert
    blockhash
    keccak256
    sha256
    ripemd160
    ecrecover
    addmod
    mulmod
    this
    super
    selfdestruct

    bytes
    string
    payable])

(defn- sol-fn-name-raw
  [name]
  (str/join "-" (map str/spear-case (str/split name #"\."))))

(def sol-fn-name (str/wrap sol-fn-name-raw))

(defn- sol-tmpl
  "creates fragments in builtin"
  {:added "4.0"}
  [sym]
  (h/$ (def$.sol ~(sol-fn-name sym) ~sym)))

(h/template-entries [sol-tmpl]
  +globals+)
