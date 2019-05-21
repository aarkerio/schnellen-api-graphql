(ns lacinia-ped.calls.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [io.pedestal.log :as log]
            [lacinia-ped.db.core :as db]))

(defn- ^:private resolver-get-questions-by-test
  "get and convert to map keyed"
  [context args value]
  (let [test-id (:id args)
        id      (Integer/parseInt test-id)]
    (log/info :msg (str ">>> PARAM >>>>> " (db/get-questions { :test-id id })))
    (db/get-questions { :test-id id })))

(defn- ^:private resolve-test-by-id
  [context args value]
  (let [test-id (:id args)
        id      (Integer/parseInt test-id)]
    (db/get-one-test { :id id })))

(defn resolver-map
  [component]
  {:test-by-id (partial resolve-test-by-id)
   :questions-by-test (partial resolver-get-questions-by-test)})
