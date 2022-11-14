(ns js.lib.metamask
  (:require [std.lang :as l]
            [std.lib :as h]))

(l/script :js
  {:macro-only true
   :bundle {:onboarding   [["@metamask/onboarding" :as MetaMaskOnboarding]]
            :provider     [["@metamask/detect-provider" :as MetaMaskDetectProvider]]}
   :import  [["@metamask/onboarding" :as MetaMaskOnboarding]]
   :require [[melbourne.ui-text :as ui-text]
             [js.lib.eth-lib :as eth-lib :include [:fn]]
             [js.react :as r :include [:fn]]
             [js.react-native :as n :include [:fn]]
             [js.core :as j]
             [xt.lang.base-lib :as k]]
   :export [MODULE]})

(h/template-entries [l/tmpl-entry {:type :fragment
                                   :base "MetaMaskOnboarding"
                                   :tag "js"}]

  [isMetaMaskInstalled])

(h/template-entries [l/tmpl-macro {:base "MetaMaskOnboarding"
                                   :inst "mtm"
                                   :tag "js"}]
  [[startOnboarding   []]
   [stopOnboarding    []]])

(defmacro.js onboarding
  "creates the onboarding object"
  {:added "4.0"}
  []
  '(new MetaMaskOnboarding))

(defmacro.js detectProvider
  "detects the metamask provider"
  {:added "4.0"}
  []
  '(MetaMaskDetectProvider))

(defmacro.js hasMetaMask?
  "checks that platform contains metamask"
  {:added "4.0"}
  []
  '(and window
        window.ethereum
        window.ethereum.isMetaMask))

(defmacro.js request
  "creates a metamask request"
  {:added "4.0"}
  [m & [f]]
  (apply list '. '(!:G ethereum)
         (list 'request m)
         (if f
           [(list 'then f)]
           [])))

(defmacro.js on
  "creates a metamask request"
  {:added "4.0"}
  [event & [f]]
  (list '. '(!:G ethereum)
        (list 'on event f)))

(defmacro.js removeListener
  "creates a metamask request"
  {:added "4.0"}
  [event & [f]]
  (list '. '(!:G ethereum)
        (list 'removeListener event f)))

(defmacro.js isConnected
  "creates a metamask request"
  {:added "4.0"}
  []
  (list '. '(!:G ethereum) '(isConnected)))

(defglobal.js MetamaskContext (r/createContext))


(defn.js format-address-string
  [s]
  (return (+ "0x" (j/substring s 2 20) "...")))

(defn.js format-chain-id
  [n]
  (return (+ "0x" (. n (toString 16)))))

(defn.js MetamaskProvider
  "constructs an isolated context for portals and gateways to appear"
  {:added "4.0"}
  [#{children}]
  (var #{Provider} -/MetamaskContext)
  (var value (r/const (eth-lib/new-web3-provider
                       (. window ethereum))))
  (return
   [:% Provider
    {:value value}
    children]))

(defn.js MetamaskConsumer
  [#{[component
      (:.. rprops)]}]
  (var #{Consumer} -/MetamaskContext)
  (return [:% Consumer
           (fn [provider]
             (return
              (r/% component (j/assign rprops
                                       #{provider}))))]))

(defn.js MetamaskEnsureInstall
  [#{design
     fallback
     children}]
  (var [installed setInstalled] (r/local))
  (var [t #{stopNow}] (r/useNow 1100))
  (var onboarding (r/const
                   (new MetaMaskOnboarding)))

  (r/run [installed t]
    (when (and (not installed)
               (-/isMetaMaskInstalled))
      (setInstalled true)
      (stopNow)))
  (return
   (:? (not installed)
       (or fallback
           [:% n/Row
            [:% ui-text/ButtonAccent
             {:design design
              :text  "Install Extension"
              :onPress
              (fn []
                (-/startOnboarding onboarding)
                (-/stopOnboarding onboarding))}]])
       
       (or children [:% n/View]))))

(defn.js MetamaskEnsureConnected
  [#{design
     onChange
     addresses
     fallback
     children}]
  (var [accounts setAccounts] (r/local {}))
  (var setTable (r/const
                 (fn [arr]
                   (var accounts (k/arr-juxt (or arr [])
                                             (fn [s]
                                               (return (+ "0x" (j/toUpperCase (j/substring s 2)))))
                                             k/T))
                   (setAccounts accounts)
                   (when onChange (onChange accounts)))))
  (var requestFn
       (r/const (fn:>
                  (. (-/request {:method "eth_requestAccounts"} setTable)
                     (catch k/identity)))))
  (r/init []
    (requestFn)
    (-/on "accountsChanged" setTable)
    (return
     (fn []
       (-/removeListener "accountsChanged" setTable))))
  (return
   (:? (k/is-empty? accounts)
       (or fallback
           [:% n/Row
            [:% ui-text/ButtonAccent
             {:design design
              :text  "Link Site"
              :onPress requestFn}]])
       (or children [:% n/View]))))

(defn.js useChainId
  []
  (var [chainId setChainId] (r/local))
  (r/init []
    (. (-/request {:method "eth_chainId"} setChainId)
       (catch k/identity))
    (-/on "chainChanged" setChainId)
    (return
     (fn []
       (-/removeListener "chainChanged" setChainId))))
  (return chainId))


(defn.js MetamaskEnsureChain
  [#{design
     chainId
     children}]
  (var [id setId] (r/local ""))
  (r/init []
    (. (-/request {:method "eth_chainId"} setId)
       (catch k/identity))
    (-/on "chainChanged" setId)
    (return
     (fn []
       (-/removeListener "chainChanged" setId))))
  (return
   (:? (== chainId (j/toString id))
       (or children [:% n/View])
       [:% n/Row
        [:% ui-text/H5
         {:design design
          :variant {:fg {:key "error"}}}
         "Metamask Incorrect Chain"]])))

(defn.js MetamaskEnsureAddress
  [#{design
     addresses
     fallback
     children}]
  (var [accounts setAccounts] (r/local {}))
  (var setTable (r/const
                 (fn:> [arr]
                   (setAccounts
                    (k/arr-juxt (or arr [])
                                k/to-string
                                k/T)))))
  (var requestFn
       (r/const (fn:>
                  (. (-/request {:method "eth_requestAccounts"} setTable)
                     (catch k/identity)))))
  (r/init []
    (requestFn)
    (-/on "accountsChanged" setTable)
    (return
     (fn []
       (-/removeListener "accountsChanged" setTable))))
  (return
   (:? (k/is-empty? accounts)
       [:% n/Row
        [:% ui-text/ButtonAccent
         {:design design
          :text  "Link Site"
          :onPress requestFn}]]
       
       (k/arr-some addresses (fn:> [addr] (. accounts [addr])))
       (or children [:% n/View])
       
       :else
       (:? fallback
           (r/% fallback #{design accounts})
           [:% n/Row
            [:% ui-text/H5
             {:design design}
             "Metamask Incorrect Account"]]))))

(def.js MODULE (!:module))

(comment
  (def +provider-keys+
    ["_events"
     "_eventsCount"
     "_maxListeners"
     "_log"
     "_state"
     "selectedAddress"
     "chainId"
     "_handleAccountsChanged"
     "_handleConnect"
     "_handleChainChanged"
     "_handleDisconnect"
     "_handleUnlockStateChanged"
     "_rpcRequest"
     "request"
     "_rpcEngine"
     "_handleStreamDisconnect"
     "_jsonRpcConnection"
     "_sentWarnings"
     "networkVersion"
     "isMetaMask"
     "_sendSync"
     "enable"
     "send"
     "sendAsync"
     "_warnOfDeprecation"
     "_metamask"]))

(comment)
