(defproject zcaudate/foundation-base "4.0.1"
  :description "base libraries for foundation"
  :url "https://www.gitlab.com/zcaudate/foundation-base"
  :aliases
  {"test"  ["exec" "-ep" "(use 'code.test) (def res (run :all)) (System/exit (+ (:failed res) (:thrown res)))"]
   "test-unit"   ["run" "-m" "code.test" "exit"]
   "publish"     ["exec" "-ep" "(use 'code.doc)     (deploy-template :all) (publish :all)"]
   "incomplete"  ["exec" "-ep" "(use 'code.manage)  (incomplete :all) (System/exit 0)"]
   "install"     ["exec" "-ep" "(use 'code.maven)  (install :all {:tag :all}) (System/exit 0)"]
   "deploy"      ["exec" "-ep" "(use 'code.maven)  (deploy :all {:tag :all}) (System/exit 0)"]
   "push-native-code"  ["run" "-m" "component.task-native-index"]}
  :dependencies
  [;; dev
   [org.clojure/clojure "1.11.1"]
   [javax.xml.bind/jaxb-api "2.4.0-b180830.0359"]
   [com.sun.xml.bind/jaxb-core "4.0.1"]
   [com.sun.xml.bind/jaxb-impl "4.0.1"]
   
   ;; code.doc
   [markdown-clj/markdown-clj "1.11.3"] ;; not mustache

   ;; code.manage
   [org.clojure/tools.reader "1.3.6"]

   ;; lib.aether
   [org.eclipse.aether/aether-api "1.1.0"]
   [org.eclipse.aether/aether-spi "1.1.0"]
   [org.eclipse.aether/aether-util "1.1.0"]
   [org.eclipse.aether/aether-impl "1.1.0"]
   [org.eclipse.aether/aether-connector-basic "1.1.0"]
   [org.eclipse.aether/aether-transport-wagon "1.1.0"]
   [org.eclipse.aether/aether-transport-http "1.1.0"]
   [org.eclipse.aether/aether-transport-file "1.1.0"]
   [org.eclipse.aether/aether-transport-classpath "1.1.0"]
   [org.apache.maven/maven-aether-provider "3.3.9"]

   ;; lib.lucene
   [org.apache.lucene/lucene-core "9.4.1"]
   [org.apache.lucene/lucene-queryparser "9.4.1"]
   [org.apache.lucene/lucene-analyzers-common "8.11.2"]
   [org.apache.lucene/lucene-suggest "9.4.1"]

   ;; lib.openpgp
   [org.bouncycastle/bcprov-jdk15on "1.65"]
   [org.bouncycastle/bcpg-jdk15on "1.65"]

   ;; lib.postgres
   [com.impossibl.pgjdbc-ng/pgjdbc-ng "0.8.9"]
   
   ;; lib.oshi
   [com.github.oshi/oshi-core "6.3.1"]

   ;; math.stat
   [net.sourceforge.jdistlib/jdistlib "0.4.5"]

   ;; math.infix
   [org.scijava/parsington "3.0.0"]
   
   ;; rt.basic
   [http-kit "2.5.3"]

   ;; rt.jep
   [black.ninia/jep "4.1.0"]
   
   ;; rt.graal
   [org.graalvm.sdk/graal-sdk "21.2.0"]
   [org.graalvm.truffle/truffle-api "21.2.0"]
   [org.graalvm.js/js "21.2.0"]
   [org.graalvm.js/js-scriptengine "21.2.0"]
   [commons-io/commons-io "2.11.0"]
   
   ;; std.pretty
   [org.clojure/core.rrb-vector "0.1.2"]

   ;; script.css
   [garden "1.3.10"]
   [net.sourceforge.cssparser/cssparser "0.9.29"]
   
   ;; script.graphql
   [district0x/graphql-query "1.0.6"]

   ;; script.toml
   [com.moandjiezana.toml/toml4j "0.7.2"]

   ;; script.yaml
   [org.yaml/snakeyaml "1.33"]

   ;; std.fs.archive
   [org.apache.commons/commons-compress "1.21"]

   ;; std.config
   [borkdude/edamame "1.0.0"]

   ;; std.contract
   [metosin/malli "0.2.1"]

   ;; std.html
   [org.jsoup/jsoup "1.15.3"]

   ;; std.image
   [com.twelvemonkeys.imageio/imageio-bmp  "3.9.3"]
   [com.twelvemonkeys.imageio/imageio-tiff "3.9.3"]
   [com.twelvemonkeys.imageio/imageio-icns "3.9.3"]
   [com.twelvemonkeys.imageio/imageio-jpeg "3.9.3"]

   ;; std.json
   [com.fasterxml.jackson.core/jackson-core "2.14.0"]
   [com.fasterxml.jackson.core/jackson-databind "2.14.0"]
   [com.fasterxml.jackson.datatype/jackson-datatype-jsr310 "2.14.0"]

   ;; std.math
   [org.apache.commons/commons-math3 "3.6.1"]

   ;; std.text.diff
   [com.googlecode.java-diff-utils/diffutils "1.3.0"]
   

   ;; TESTS - std.object
   [org.eclipse.jgit/org.eclipse.jgit "5.13.0.202109080827-r"]]
  :global-vars {*warn-on-reflection* true}
  :cljfmt {:file-pattern #"^[^\.].*\.clj$"
           :indents {script [[:inner 0]]
                     template-vars [[:inner 0]]
                     fact [[:inner 0]]
                     comment [[:inner 0]]}}
  :profiles {:dev {:plugins [[lein-ancient "0.6.15"]
                             [lein-exec "0.3.7"]
                             [lein-cljfmt "0.7.0"]
                             [cider/cider-nrepl "0.25.11"]]}
             :repl {:injections [(try (require 'jvm.tool)
                                      (require '[std.lib :as h])
                                      (catch Throwable t (.printStackTrace t)))]}}
  :resource-paths    ["resources" "src-build" "test-data" "test-code"]
  :java-source-paths ["src-java" "test-java"]
  :java-output-path  "target/classes"
  :repl-options {:host "0.0.0.0" :port 51311}
  :jvm-opts
  ["-Xms2048m"
   "-Xmx2048m"
   "-XX:MaxMetaspaceSize=1048m"
   "-XX:-OmitStackTraceInFastThrow"
   
   ;;
   ;; GC FLAGS
   ;;
   "-XX:+UseAdaptiveSizePolicy"
   "-XX:+AggressiveHeap"
   "-XX:+ExplicitGCInvokesConcurrent"
   "-XX:+UseCMSInitiatingOccupancyOnly"
   "-XX:+CMSClassUnloadingEnabled"
   "-XX:+CMSParallelRemarkEnabled"

   ;;
   ;; GC TUNING
   ;;   
   "-XX:MaxNewSize=256m"
   "-XX:NewSize=256m"
   "-XX:CMSInitiatingOccupancyFraction=60"
   "-XX:MaxTenuringThreshold=8"
   "-XX:SurvivorRatio=4"
   
   ;;
   ;; JVM
   ;;
   "-Djdk.tls.client.protocols=\"TLSv1,TLSv1.1,TLSv1.2\""
   "-Djdk.attach.allowAttachSelf=true"
   "--add-opens" "javafx.graphics/com.sun.javafx.util=ALL-UNNAMED"
   "--add-opens" "java.base/java.lang=ALL-UNNAMED"
   "--illegal-access=permit"])
