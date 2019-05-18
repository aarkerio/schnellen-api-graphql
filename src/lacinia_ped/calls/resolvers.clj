(ns lacinia-ped.calls.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [io.pedestal.log :as log]
            [lacinia-ped.db.core :as db]))

(defn resolve-test-by-id
  [tests-map context args value]
  (let [{:keys [id]} args
        _ (log/info :msg (str ">>> TSTS KKKKKKKKKKKKKKKKKKKKKK EDN >>>>> " id))]
    (get tests-map id)))

(defn resolver-map
  [component]
  (let [cgg-data (-> (io/resource "cgg-data.edn")
                     slurp
                     edn/read-string)
        test-map (->> cgg-data
                       :tests
                       (reduce #(assoc %1 (:id %2) %2) {}))]
    (log/info :msg (str ">>> TSTS 33 77777777777777777 EDN >>>>> " test-map))
    {:test-by-id (partial resolve-test-by-id test-map)}))
