# Our uberjar
-injars target/uberjar/rt-postgres-4.0.1.jar
# Our output directory
-outjars obfuscated

# ProGuard options. Detailed explanation here http://proguard.sourceforge.net/index.html#manual/usage.html
-dontskipnonpubliclibraryclassmembers
-dontnote
-dontwarn
-printseeds

# -libraryjars /Library/Java/JavaVirtualMachines/openjdk-11.jdk/Contents/Home/jmods
-libraryjars $JAVA_HOME/jmods
-libraryjars ~/.m2/repository/org/clojure/clojure/1.11.1/clojure-1.11.1.jar
-libraryjars ~/.m2/repository/xyz.zcaudate/std.lib/4.0.1/std.lib-4.0.1.jar
-libraryjars ~/.m2/repository/xyz.zcaudate/std.lang/4.0.1/std.lang-4.0.1.jar
-libraryjars ~/.m2/repository/xyz.zcaudate/std.json/4.0.1/std.json-4.0.1.jar
-libraryjars ~/.m2/repository/xyz.zcaudate/std.html/4.0.1/std.html-4.0.1.jar
-libraryjars ~/.m2/repository/org/jsoup/jsoup/1.16.1/jsoup-1.16.1.jar
-libraryjars ~/.m2/repository/com/impossibl/pgjdbc-ng/pgjdbc-ng/0.8.9/pgjdbc-ng-0.8.9.jar
-libraryjars ~/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.15.2/jackson-core-2.15.2.jar
-libraryjars ~/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.15.2/jackson-databind-2.15.2.jar
-libraryjars ~/.m2/repository/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.15.2/jackson-datatype-jsr310-2.15.2.jar

# What we will be doing is obfuscating, shrinking and optimizing the jar. 
# If you experience any problems start out with obfuscation and add the 
# -dontoptimize  and the -dontshrink flags and see if it works.

# Tell proguard to leave the clojure runtime alone
# You would need to add any other classes that you wish to preserve here.
-keep class clojure.** { *; }
-keep class java.** { *; }
-keep class javax.** { *; }
# -keep class rt.postgres.** { *; }