(ns lacinia-ped.api.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.schema :as schema]
            [clojure.edn :as edn]
            [io.pedestal.log :as log]
            [lacinia-ped.calls.resolvers :as resolvers]))

(defn load-schema
  [component]
  (-> (io/resource "graphql/schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolvers/resolver-map component))
      schema/compile))

(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :schema (load-schema this)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  "Called by system.clj"
  []
  {:schema-provider (map->SchemaProvider {})})

