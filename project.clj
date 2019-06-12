(defproject lacinia-ped "0.0.2"
  :description "Graphql API for teachers"
  :url "https://github.com/aarkerio/zentaur-api-graphql"
  :license {:name "GPLv3"}
  :dependencies [[conman "0.8.3"]                        ;; Luminus database connection management and SQL query generation library
                 [com.stuartsierra/component "0.4.0"]    ;; managing the lifecycle of software components which have runtime state.
                 [cprop "0.1.13"]                        ;; likes properties, environments, configs, profiles...
                 [ch.qos.logback/logback-classic "1.2.3" :exclusions [org.slf4j/slf4j-api]]
                 [cheshire "5.8.1"]                      ;; Clojure JSON and BSON encoding/decoding
                 [clj-time "0.15.0"]                     ;; date time-zone library
                 [com.walmartlabs/lacinia "0.33.0-alpha-3"]  ;; Graphql in Clojure
                 [com.walmartlabs/lacinia-pedestal "0.12.0-alpha-1"] ;; Expose Lacinia GraphQL as Pedestal endpoints
                 [funcool/struct "1.3.0"]                ;; database validation
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [mount "0.1.16"]                        ;; managing Clojure and ClojureScript app state
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.7.9"]
                 [org.postgresql/postgresql "42.2.5"]
                 [org.slf4j/jul-to-slf4j "1.7.25"]
                 [org.slf4j/jcl-over-slf4j "1.7.25"]   ;; JCL 1.2 implemented over SLF4J
                 [org.slf4j/log4j-over-slf4j "1.7.25"]]
  :min-lein-version "2.5.0"
  :main ^{:skip-aot true} lacinia-ped.server
  :resource-paths ["config", "resources"]
  ;; If you use HTTP/2 or ALPN, use the java-agent to pull in the correct alpn-boot dependency
  ;:java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :profiles {:dev {:aliases {"run-dev" ["trampoline" "run" "-m" "lacinia-ped.server/-main"]}
                   :jvm-opts ["-Dconf=dev-config.edn"]
                   :repl-options {:resource-paths ["dev-resources"]
                                  :init-ns user
                                  ;; If nREPL takes too long to load it may timeout,
                                  ;; increase this to wait longer before timing out.
                                  ;; Defaults to 30000 (30 seconds)
                                  :timeout 1200000000
                                  } ;; /project/dev ends
                   :dependencies [[io.pedestal/pedestal.service-tools "0.5.5"]
                                  [nrepl "0.6.0"]                         ;; REPL that provides a server and client
                                  [reloaded.repl "0.2.4"]]
                   }
             :uberjar {:aot [lacinia-ped.server]
                       :uberjar-name "zentaur-api-prod-standalone.jar"}
             }
  )

