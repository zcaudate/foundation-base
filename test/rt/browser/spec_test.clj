(ns rt.browser.spec-test
  (:use code.test)
  (:require [rt.browser.spec :as spec]
            [rt.browser.util :as util]))

^{:refer rt.browser.spec/spec-download :added "4.0"}
(fact "downloads the chrome devtools spec")

^{:refer rt.browser.spec/list-domains :added "4.0"}
(fact "lists all domains"
  ^:hidden
  
  (spec/list-domains)
  => '("Accessibility"
       "Animation"
       "Audits"
       "BackgroundService"
       "Browser"
       "CSS"
       "CacheStorage"
       "Cast"
       "Console"
       "DOM"
       "DOMDebugger"
       "DOMSnapshot"
       "DOMStorage"
       "Database"
       "Debugger"
       "DeviceOrientation"
       "Emulation"
       "EventBreakpoints"
       "Fetch"
       "HeadlessExperimental"
       "HeapProfiler"
       "IO"
       "IndexedDB"
       "Input"
       "Inspector"
       "LayerTree"
       "Log"
       "Media"
       "Memory"
       "Network"
       "Overlay"
       "Page"
       "Performance"
       "PerformanceTimeline"
       "Profiler"
       "Runtime"
       "Schema"
       "Security"
       "ServiceWorker"
       "Storage"
       "SystemInfo"
       "Target"
       "Tethering"
       "Tracing"
       "WebAudio"
       "WebAuthn"))

^{:refer rt.browser.spec/get-domain-raw :added "4.0"}
(fact "gets the raw domain"
  ^:hidden
  
  (spec/get-domain-raw "Console")
  => {"clearMessages"
      {"name" "clearMessages", "description" "Does nothing."},
      "disable"
      {"name" "disable",
       "description"
       "Disables console domain, prevents further console messages from being reported to the client."},
      "enable"
      {"name" "enable",
       "description"
       "Enables console domain, sends the messages collected so far to the client by means of the\n`messageAdded` notification."}})

^{:refer rt.browser.spec/list-methods :added "4.0"}
(fact "lists all spec methods"
  ^:hidden
  
  (spec/list-methods "Runtime")
  => '("addBinding"
       "awaitPromise"
       "callFunctionOn"
       "compileScript"
       "disable"
       "discardConsoleEntries"
       "enable"
       "evaluate"
       "getHeapUsage"
       "getIsolateId"
       "getProperties"
       "globalLexicalScopeNames"
       "queryObjects"
       "releaseObject"
       "releaseObjectGroup"
       "removeBinding"
       "runIfWaitingForDebugger"
       "runScript"
       "setAsyncCallStackDepth"
       "setCustomObjectFormatterEnabled"
       "setMaxCallStackSizeToCapture"
       "terminateExecution"))

^{:refer rt.browser.spec/get-method :added "4.0"}
(fact "gets the method"
  ^:hidden
  
  (-> (spec/get-method "Runtime"
                       "evaluate")
      (update "parameters"
              (fn [parameters]
                (mapv (fn [m]
                        (select-keys m ["name" "optional"]))
                      parameters))))
  => {"returns"
      [{"name" "result",
        "$ref" "RemoteObject",
        "description" "Evaluation result."}
       {"optional" true,
        "name" "exceptionDetails",
        "$ref" "ExceptionDetails",
        "description" "Exception details."}],
      "name" "evaluate",
      "parameters"
      [{"name" "expression"}
       {"name" "objectGroup", "optional" true}
       {"name" "includeCommandLineAPI", "optional" true}
       {"name" "silent", "optional" true}
       {"name" "contextId", "optional" true}
       {"name" "returnByValue", "optional" true}
       {"name" "generatePreview", "optional" true}
       {"name" "userGesture", "optional" true}
       {"name" "awaitPromise", "optional" true}
       {"name" "throwOnSideEffect", "optional" true}
       {"name" "timeout", "optional" true}
       {"name" "disableBreaks", "optional" true}
       {"name" "replMode", "optional" true}
       {"name" "allowUnsafeEvalBlockedByCSP", "optional" true}
       {"name" "uniqueContextId", "optional" true}],
      "description" "Evaluates expression on global object."})

^{:refer rt.browser.spec/tmpl-connection :added "4.0"}
(fact "creates a connection form given template"
  ^:hidden
  
  (spec/tmpl-connection '[runtime-evaluate ["Runtime" "evaluate"]])
  => '(defn runtime-evaluate
        [conn expression & [{:keys [allow-unsafe-eval-blocked-by-csp
                                    await-promise
                                    context-id
                                    disable-breaks
                                    generate-preview
                                    include-command-line-api
                                    object-group
                                    repl-mode
                                    return-by-value
                                    silent
                                    throw-on-side-effect
                                    timeout
                                    unique-context-id
                                    user-gesture],
                             :as m}
                            timeout
                            opts]]
        (rt.browser.connection/send
         conn
         "Runtime.evaluate"
         (merge {"expression" expression} m)
         timeout
         opts)))

^{:refer rt.browser.spec/tmpl-browser :added "4.0"}
(fact "constructs the browser template"

  (spec/tmpl-browser '[page-navigate util/page-navigate])
  => '(def page-navigate (rt.browser.impl/wrap-browser-state util/page-navigate)))

(comment

  (spec/list-domains)
  
  (spec/list-methods "Runtime")

  (spec/list-methods "Target")
  
  
  
  
  #(mapv (fn [m]
           (select-keys m ["name" "optional"]))
         (get % "parameters"))

  (def +runtime+
    (h/map-juxt [#(get % "name")
                 #(mapv (fn [m]
                          (select-keys m ["name" "optional"]))
                        (get % "parameters"))]
                (get +domains+ "Runtime"))))
