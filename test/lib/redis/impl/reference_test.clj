(ns lib.redis.impl.reference-test
  (:use code.test)
  (:require [lib.redis.impl.reference :refer :all]))

^{:refer lib.redis.impl.reference/command-doc :added "3.0"}
(fact "converts an entry to the redis doc format"

  (command-doc (:set (parse-commands)))
  => "SET key value [EX seconds|PX milliseconds] [NX|XX] [KEEPTTL]")

^{:refer lib.redis.impl.reference/parse-main :added "3.0"}
(fact "parses file for main reference")

^{:refer lib.redis.impl.reference/parse-supplements :added "3.0"}
(fact "parses file for supplement reference")

^{:refer lib.redis.impl.reference/parse-commands :added "3.0"}
(fact "returns all commands"

  (parse-commands)
  => map?)

^{:refer lib.redis.impl.reference/command-list :added "3.0"}
(fact "returns all commands"

  (command-list)

  (command-list :hash))

^{:refer lib.redis.impl.reference/command :added "3.0"}
(fact "gets the command info"

  (command :hset)
  => map?)

^{:refer lib.redis.impl.reference/command-groups :added "3.0"}
(fact "lists all command group types"

  (command-groups)
  => (list :cluster :connection :generic :geo :hash
           :hyperloglog :list :pubsub :scripting
           :server :set :sorted-set :stream :string :transactions))
