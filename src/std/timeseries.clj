(ns std.timeseries
  (:require [std.timeseries.common :as common]
            [std.timeseries.process :as process]
            [std.timeseries.journal :as journal]
            [std.lib :as h])
  (:refer-clojure :exclude [merge derive]))

(h/intern-in common/create-template

             journal/journal
             journal/add-bulk
             journal/add-entry
             journal/update-meta
             journal/derive
             journal/merge
             journal/select

             process/process)
