(ns std.log.console-test
  (:use code.test)
  (:require [std.log.console :refer :all]
            [std.log :as log]))

^{:refer std.log.console/style-default :added "3.0"}
(fact  "gets default style"

  (style-default {}))

^{:refer std.log.console/join-with :added "3.0"}
(fact "helper function for string concatenation" ^:hidden

  (join-with " " even? [1 2 3 4 5 6])
  => "2 4 6")

^{:refer std.log.console/console-pprint :added "3.0"}
(fact "prints the item in question")

^{:refer std.log.console/console-format-line :added "3.0"}
(fact "format the output line" ^:hidden

  (console-format-line "hello")
  => "| hello")

^{:refer std.log.console/console-display? :added "3.0"}
(fact "check if item is displayed")

^{:refer std.log.console/console-render :added "3.0"}
(fact "renders the entry based on :console/style")

^{:refer std.log.console/console-header-label :added "3.0"}
(fact "constructs the header label")

^{:refer std.log.console/console-header-position :added "3.0"}
(fact "constructs the header position")

^{:refer std.log.console/console-header-date :added "3.0"}
(fact "constructs the header date")

^{:refer std.log.console/console-header-message :added "3.0"}
(fact "constructs the header message"

  (console-header-message {:log/template "the value is {{log/value}}"
                           :log/value "HELLO"}
                          {:text :blue :background :white})
  => "[22;47;34mthe value is HELLO[0m")

^{:refer std.log.console/console-header :added "3.0"}
(fact "constructs the log header")

^{:refer std.log.console/console-meter-trace :added "3.0"}
(fact "constructs the meter trace")

^{:refer std.log.console/console-meter-form :added "3.0"}
(fact "constructs the meter form")

^{:refer std.log.console/console-meter :added "3.0"}
(fact "constructs the log meter")

^{:refer std.log.console/console-status-outcome :added "3.0"}
(fact "constructs the status outcome")

^{:refer std.log.console/console-status-duration :added "3.0"}
(fact "constructs the status duration")

^{:refer std.log.console/console-status-start :added "3.0"}
(fact "constructs the status start time")

^{:refer std.log.console/console-status-end :added "3.0"}
(fact "constructs the status end time")

^{:refer std.log.console/console-status-props :added "3.0"}
(fact "constructs the status props")

^{:refer std.log.console/console-status :added "3.0"}
(fact "constructs the log status")

^{:refer std.log.console/console-body-console-text :added "3.0"}
(fact "constructs the return text from the console")

^{:refer std.log.console/console-body-console :added "3.0"}
(fact "constructs the body console")

^{:refer std.log.console/console-body-data-context :added "3.0"}
(fact "remove system contexts from the log entry")

^{:refer std.log.console/console-body-data :added "3.0"}
(fact "constructs the body data")

^{:refer std.log.console/console-body-exception :added "3.0"}
(fact "constructn the body exception")

^{:refer std.log.console/console-body :added "3.0"}
(fact "constructs the log body")

^{:refer std.log.console/console-format :added "3.0"}
(fact "formats the entire log")

^{:refer std.log.console/logger-process-console :added "3.0"}
(fact "processes the label for logger")

^{:refer std.log.console/console-write :added "3.0"}
(fact "write function for the console logger")

^{:refer std.log.console/console-logger :added "3.0"}
(fact "creates a console logger"

  (console-logger))

(comment
  (./import))

(comment
  (std.log/with-logger-basic (std.log/info "aoeuaoeua"
                                           "HOUOeu"))

  (std.log/with-debug-logger
    (std.log/error {:log/label "HELLO"} "HOUOeu"))

  (std.log/with-logger-basic (std.log.profile/task "e"))

  (std.log/with-logger-basic (std.log/meter {:log/label "HELLO"} "HOUOeu"))

  nil

  (std.log/meter (do (Thread/sleep 11000)
                     (+ 1 2 3)))
  (std.log/meter (do (Thread/sleep 11000)
                     (+ 1 2 3)))
  (std.log/with-logger-verbose
    (std.log/meter {:log/label "DOCKER UP"}
                   "oeuoe"))
  "oeuoe"
  "oeuoe"
  (ansi)
  (std.log/log (ansi/normal (ansi/bold (ansi/blue "HELLO")))))