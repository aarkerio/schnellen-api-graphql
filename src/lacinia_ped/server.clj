(ns lacinia-ped.server
  (:require [io.pedestal.http :as http]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [lacinia-ped.db.core :as db]
            [lacinia-ped.api.schema :as schema]
            [lacinia-ped.service :as service]
            [mount.core :as mount]))

(def service (lacinia/service-map schema/load-schema {:graphiql false}))

;; This is an adapted service map, that can be started and stopped
;; From the REPL you can call server/start and server/stop on this service
(defonce runnable-service (http/create-server service))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (mount/start #'lacinia-ped.config.options/env)
  (mount.core/stop #'db/*db*)
  (http/start runnable-service))
