(ns lacinia-ped.server
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [lacinia-ped.db.core :as db]
            [lacinia-ped.api.schema :as schema]
            [lacinia-ped.service :as service]
            [mount.core :as mount]))

;; (def service (lacinia/service-map schema/load-schema {:graphiql true}))

;; ;; This is an adapted service map, that can be started and stopped
;; ;; From the REPL you can call server/start and server/stop on this service
;; (defonce runnable-service (http/create-server service))

;; (defn -main
;;   "The entry-point for 'lein run'"
;;   [& args]
;;   (println "\nCreating your server...")
;;   (mount/start #'lacinia-ped.config.options/env)
;;   (mount.core/stop #'db/*db*)
;;   (http/start runnable-service))

(defrecord Server [schema-provider server]

  component/Lifecycle
  (start [this]
    (assoc this :server (-> schema-provider
                            :schema
                            (lacinia/service-map {:graphiql true})
                            http/create-server
                            http/start)))
  (stop [this]
    (http/stop server)
    (assoc this :server nil)))

(defn new-server
  []
  {:server (component/using (map->Server {})
                            [:schema-provider])})
