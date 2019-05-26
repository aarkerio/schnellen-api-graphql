(ns lacinia-ped.system
  (:require
    [com.stuartsierra.component :as component]
    [lacinia-ped.api.schema :as schema]
    [lacinia-ped.server :as server]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)))
