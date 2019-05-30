(ns lacinia-ped.system
  (:require
    [com.stuartsierra.component :as component]
    [lacinia-ped.api.schema :as schema]
    [lacinia-ped.server :as server])
  (:gen-class))

(defn -main
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)))
