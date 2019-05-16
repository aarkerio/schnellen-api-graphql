(defproject lacinia-ped "0.0.1"
  :description "Graphql API for teachers"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[conman "0.8.3"]                        ;; Luminus database connection management and SQL query generation library
                 [cprop "0.1.13"]                        ;; likes properties, environments, configs, profiles..
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [cheshire "5.8.1"]                      ;; Clojure JSON and BSON encoding/decoding
                 [clj-time "0.14.0"]                     ;; date time-zone library
                 [com.walmartlabs/lacinia "0.33.0-alpha-3"]  ;; Graphql in Clojure
                 [io.pedestal/pedestal.service "0.5.5"]
                 ;; Remove this line and uncomment one of the next lines to
                 ;; use Immutant or Tomcat instead of Jetty:
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 ;; [io.pedestal/pedestal.immutant "0.5.5"]
                 ;; [io.pedestal/pedestal.tomcat "0.5.5"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.slf4j/jul-to-slf4j "1.7.25"]
                 [org.slf4j/jcl-over-slf4j "1.7.25"]
                 [org.slf4j/log4j-over-slf4j "1.7.25"]]
  :min-lein-version "2.5.0"
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "lacinia-ped.server/run-dev"]}
                   :repl-options {:port 8000}
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.5"]]}
             :uberjar {:aot [lacinia-ped.server]}}
  :main ^{:skip-aot true} lacinia-ped.server)

