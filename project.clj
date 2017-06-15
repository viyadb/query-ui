(defproject query-ui "0.1.0"
  :description "Cohort UI"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.slf4j/slf4j-api "1.7.13"]
                 [org.slf4j/log4j-over-slf4j "1.7.13"]
                 [compojure "1.6.0"]
                 [http-kit "2.2.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-core "1.6.1"]
                 [cheshire "5.7.1"]]
  :main query-ui.core
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all
                       :dependencies [[ch.qos.logback/logback-classic "1.2.3"]]}
             :dev {:dependencies [[org.slf4j/slf4j-simple "1.7.13"]]
                   :jvm-opts ["-Dorg.slf4j.simpleLogger.defaultLogLevel=debug"]}})
