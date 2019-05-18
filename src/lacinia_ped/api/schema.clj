(ns lacinia-ped.api.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as schema]
    [clojure.edn :as edn]
    [lacinia-ped.calls.resolvers :as resolvers]))

(defn- foo []
  {:id "1236"
   :name "Tiny Epic Galaxies"
   :summary "Fast dice-based sci-fi space game with a bit of chaos"
   :min_players 1
   :max_players 4})

(defn load-schema
  []
  (-> (io/resource "graphql/schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers {:test-by-id 'foo})
      schema/compile))
