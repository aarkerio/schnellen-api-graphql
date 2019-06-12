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

(defn- ^:private get-answers
  "Get the answers for each question"
  [question]
  (let [answers          (db/get-answers {:question-id (:id question)})
        keys-answers     (map #(assoc % :key (str "keyed-" (:id %))) answers)
        question-updated (update question :created_at #(helpers/format-time %))]
    (assoc question-updated :answers keys-answers)))

(defn- ^:private attach-questions
  "Get the answers for each question"
  [questions index-seq]
  (->> questions
       (map get-answers)
       (zipmap index-seq)))  ;; add the index

(defn- ^:private resolver-get-questions-by-test
  "Resolver to get and convert to map keyed"
  [context args value]
  (let [pre-test-id    (:id args)
        test-id        (Integer/parseInt pre-test-id)
        pre-full-test  (db/get-one-test { :test-id test-id })
        full-test      (update pre-full-test :id str)
        questions      (db/get-questions { :test-id test-id })
        index-seq      (map #(% :id) questions)   ;; extract sequence
        integrated-q   (attach-questions questions index-seq)
        _              (log/info :msg (str ">>>  5555 integrated-q  >>>>> " integrated-q))]
        integrated-q))

(defn- ^:private resolve-test-by-id
  [context args value]
  (let [pre-test-id  (:id args)
        test-id      (Integer/parseInt pre-test-id)]
    (db/get-one-test { :test-id test-id })))

(defn resolver-map
  "Public. Match resolvers."
  [component]
  {:test-by-id (partial resolve-test-by-id)
   :questions-by-test (partial resolver-get-questions-by-test)})
