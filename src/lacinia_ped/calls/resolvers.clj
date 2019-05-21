(ns lacinia-ped.calls.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [io.pedestal.log :as log]
            [lacinia-ped.db.core :as db]))

(defn- ^:private resolver-get-questions-by-test
  "get and convert to map keyed"
  [context args value]
  (let [test-id   (args :id)
        questions (db/get-questions { :test-id test-id })]
    questions))

(defn- ^:private resolve-test-by-id
  [context args value]
  (let [_       (log/info :msg (str ">>>  ARGS  >>>>> " args))
        test-id (args :id)]
    (db/get-one-test {:id test-id})))

(defn resolver-map
  [component]
  {:test-by-id (partial resolve-test-by-id)
   :questions-by-test (partial resolver-get-questions-by-test)})
