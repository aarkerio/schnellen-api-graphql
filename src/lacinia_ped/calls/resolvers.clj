(ns lacinia-ped.calls.resolvers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [io.pedestal.log :as log]
            [lacinia-ped.db.core :as db]
            [lacinia-ped.libs.helpers :as helpers]
            [lacinia-ped.calls.validations.validations-test :as val-test]))

;;  End with ! functions that change state for atoms, metadata, vars, transients, agents and io as well.
(defn create-test! [params user-id]
  (let [full-params (assoc params :user-id user-id)
        errors      (-> full-params (val-test/validate-test))]
    (if (= errors nil)
      (db/create-minimal-test! full-params)
      {:flash errors})))

(defn- ^:private get-answers [question]
  (let [answers          (db/get-answers {:question-id (:id question)})
        keys-answers     (map #(assoc % :key (str "keyed-" (:id %))) answers)
        question-updated (update question :created_at #(helpers/format-time %))]
    (assoc question-updated :answers keys-answers)))

(defn- ^:private get-questions
  "get and convert to map keyed"
  [test-id]
  (let [questions  (db/get-questions { :test-id test-id })
        index-seq  (map #(% :id) questions)]
        (->> questions
             (map get-answers)
             (zipmap index-seq)  ;; add the index
             )))


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
