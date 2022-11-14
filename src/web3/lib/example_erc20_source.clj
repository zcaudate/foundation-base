(ns web3.lib.example-erc20-source
  (:require [std.lang :as l]
            [std.lib :as h])
  (:refer-clojure :exclude [name symbol]))

(l/script :solidity
  {:require [[rt.solidity :as s]]
   :static {:contract ["ERC20Basic"]}})

(def.sol ^{:- [:string :public :constant]}
  name "ERC20Basic")

(def.sol ^{:- [:string :public :constant]}
  symbol "BSC")

(def.sol ^{:- [:uint8 :public :constant]}
  decimals 18)

(defevent.sol Event
  [:string  event-type
   :address from-address
   :address to-address
   :uint amount])

(defmapping.sol ^{:- [:public]}
  balances
  [:address :uint])

(defmapping.sol ^{:- [:public]}
  allowed
  [:address (:mapping [:address :uint])])

(def.sol ^{:- [:uint]}
  totalSupply_)

(defconstructor.sol
  __init__
  [:uint total]
  (:= -/totalSupply_ total)
  (:= (. balances [s/msg-sender])
      total))

(defn.sol ^{:- [:external :view]
            :static/returns :uint}
  totalSupply
  []
  (return -/totalSupply_))

(defn.sol ^{:- [:external :view]
            :static/returns :uint}
  balanceOf
  [:address tokenOwner]
  (return (. -/balances [tokenOwner])))

(defn.sol ^{:- [:external]
            :static/returns :bool}
  transfer
  [:address receiver
   :uint amount]
  (:-= (. -/balances [s/msg-sender]) amount)
  (emit (-/Event {:event-type "transfer"
                  :from-address s/msg-sender
                  :to-address receiver
                  :amount amount}))
  (:+= (. -/balances [receiver]) amount)
  (return true))

(defn.sol ^{:- [:external]}
  transferFrom
  [:address from
   :address receiver
   :uint amount]
  (:-= (. -/allowed  [from] [s/msg-sender]) amount)
  (emit (-/Event {:event-type "transfer_from"
                  :from-address from
                  :to-address receiver
                  :amount amount}))
  (:+= (. -/balances [receiver]) amount))

(defn.sol ^{:- [:external]}
  approve
  [:address spender
   :uint amount]
  (:-= (. -/balances [s/msg-sender]) amount)
  (emit (-/Event {:event-type "approve"
                  :from-address s/msg-sender
                  :to-address spender
                  :amount amount}))
  (:+= (. -/allowed  [s/msg-sender] [spender]) amount)
  )

;;
;; Contract
;;

(def +default-contract+
  {:ns   (h/ns-sym)
   :name "ERC20Basic"
   :args ["10000000000000000"]})
