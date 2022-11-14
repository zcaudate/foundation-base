(ns std.concurrent.thread)

(defn thread:current
  "returns the current thread"
  {:added "3.0"}
  ([]
   (Thread/currentThread)))

(defn thread:id
  "returns the id of a thread"
  {:added "3.0"}
  ([] (thread:id (thread:current)))
  ([^Thread thread]
   (.getId thread)))

(defn thread:interrupt
  "interrupts a thread"
  {:added "3.0"}
  ([^Thread thread]
   (.interrupt thread)))

(defn thread:sleep
  "sleeps for n milliseconds"
  {:added "3.0"}
  ([^long ms]
   (Thread/sleep ms)))

(defn thread:spin
  "waits using onSpin"
  {:added "3.0"}
  ([]
   (Thread/onSpinWait)))

(defn thread:wait-on
  "waits for a lock to notify"
  {:added "3.0"}
  ([^Object lock]
   (locking lock (.wait lock))))

(defn thread:notify
  "notifies threads waiting on lock"
  {:added "3.0"}
  ([^Object lock]
   (locking lock (.notify lock))))

(defn thread:notify-all
  "notifies all threads waiting on lock"
  {:added "3.0"}
  ([^Object lock]
   (locking lock (.notifyAll lock))))

(defn thread:has-lock?
  "checks if thread has the lock"
  {:added "3.0"}
  ([^Object lock]
   (Thread/holdsLock lock)))

(defn thread:yield
  "calls yield on current thread"
  {:added "3.0"}
  ([]
   (Thread/yield)))

(defn stacktrace
  "returns thread stacktrace
 
   (stacktrace)"
  {:added "3.0"}
  ([]
   (stacktrace (thread:current)))
  ([^Thread thread]
   (.getStackTrace thread)))

(defn all-stacktraces
  "returns all available stacktraces"
  {:added "3.0"}
  ([]
   (Thread/getAllStackTraces)))

(defn thread:all
  "lists all threads"
  {:added "3.0"}
  ([]
   (keys (all-stacktraces))))

(defn thread:all-ids
  "lists all thread ids"
  {:added "3.0"}
  ([]
   (set (map #(.getId ^Thread %) (thread:all)))))

(defn thread:dump
  "dumps out current thread information"
  {:added "3.0"}
  ([]
   (Thread/dumpStack)))

(defn thread:active-count
  "returns active threads"
  {:added "3.0"}
  ([]
   (Thread/activeCount)))

(defn thread:alive?
  "checks if thread is alive"
  {:added "3.0"}
  ([^Thread thread]
   (.isAlive thread)))

(defn thread:daemon?
  "checks if thread is a daemon"
  {:added "3.0"}
  ([]
   (thread:daemon? (thread:current)))
  ([^Thread thread]
   (.isDaemon thread)))

(defn thread:interrupted?
  "checks if thread has been interrupted"
  {:added "3.0"}
  ([^Thread thread]
   (.isInterrupted thread)))

(defn thread:has-access?
  "checks if thread allows access to current"
  {:added "3.0"}
  ([^Thread thread]
   (try
     (.checkAccess thread)
     true
     (catch SecurityException e
       false))))

(defn thread:start
  "starts a thread"
  {:added "3.0"}
  ([^Thread thread]
   (.start thread)))

(defn thread:run
  "runs the thread function locally"
  {:added "3.0"}
  ([^Thread thread]
   (.run thread)))

(defn thread:join
  "calls join on a thread"
  {:added "3.0"}
  ([]
   (thread:join (thread:current)))
  ([^Thread thread]
   (.join thread))
  ([^Thread thread millis]
   (.join thread millis))
  ([^Thread thread millis nanos]
   (.join thread millis nanos)))

(defn thread:uncaught
  "gets and sets the uncaught exception handler"
  {:added "3.0"}
  ([]
   (thread:uncaught (thread:current)))
  ([^Thread thread]
   (.getUncaughtExceptionHandler thread))
  ([^Thread thread f]
   (.setUncaughtExceptionHandler thread f)))

(defn thread:global-uncaught
  "gets and sets the global uncaught exception handler"
  {:added "3.0"}
  ([]
   (Thread/getDefaultUncaughtExceptionHandler))
  ([f]
   (Thread/setDefaultUncaughtExceptionHandler f)))

(defn thread:classloader
  "gets and sets the context classloader"
  {:added "3.0"}
  ([]
   (thread:classloader (thread:current)))
  ([^Thread thread]
   (.getContextClassLoader thread))
  ([^Thread thread loader]
   (.setContextClassLoader thread loader)))

(defn ^Thread thread
  "creates a new thread"
  {:added "3.0"}
  ([{:keys [^Runnable handler ^bool daemon priority classloader uncaught name start]}]
   (cond-> (Thread. handler)
     name        (doto (.setName name))
     daemon      (doto (.setDaemon daemon))
     priority    (doto (.setPriority priority))
     classloader (doto (.setContextClassLoader classloader))
     uncaught    (doto (.setUncaughtExceptionHandler uncaught))
     start (doto (.start)))))
