(ns rt.minio
  (:require [rt.minio.bench :as bench]
            [std.lib :as h]))

(h/intern-in bench/start-minio-array
             bench/stop-minio-array
             bench/all-minio-ports)
