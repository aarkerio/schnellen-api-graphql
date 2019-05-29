(ns lacinia-ped.server
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [lacinia-ped.db.core :as db]
            [lacinia-ped.api.schema :as schema]
            [lacinia-ped.service :as service]
            [mount.core :as mount])
  (:gen-class))

(defrecord Server [schema-provider server]

  component/Lifecycle
  (start [this]
    (assoc this :server (-> schema-provider
                            :schema
                            (lacinia/service-map {:graphiql true})
                            (merge {::http/allowed-origins (constantly true)})
                            http/create-server
                            http/start)))
  (stop [this]
    (http/stop server)
    (assoc this :server nil)))

(defn new-server
  []
  (println "\nCreating your server...")
  (mount/start #'lacinia-ped.config.options/env)
  (mount.core/start #'db/*db*)
  {:server (component/using (map->Server {})
                            [:schema-provider])})
