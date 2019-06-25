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
  (let [pre-answers       (db/get-answers {:question-id (:id question)})
        answers           (map #(update % :id str) pre-answers)
        question-updated  (update question :created_at #(helpers/format-time %))]
    (assoc question-updated :answers answers)))

(defn- ^:private attach-questions
  "Get the questions for the test"
  [test-id]
  (let [questions (db/get-questions test-id)]
    (->> questions
         (map get-answers)
         (map #(update % :id str)))))

(defn- ^:private resolver-get-questions-by-test
  "Resolver to get and convert to map keyed"
  [context args value]
  (let [pre-test-id    (:id args)
        test-id        { :test-id (Integer/parseInt pre-test-id) }
        pre-full-test  (db/get-one-test test-id)
        full-test      (update pre-full-test :id str) ;; Graphql needs string IDs
        questions      (attach-questions test-id)]
    (log/info :msg (str ">>> TEST >>>>> " full-test))
    (assoc {} :test full-test :questions questions)))

(defn- ^:private resolve-test-by-id
  [context args value]
  (let [pre-test-id  (:id args)
        test-id      (Integer/parseInt pre-test-id)]
    (db/get-one-test { :test-id test-id })))

(defn- ^:private get-last-ordnen
  [table id]
  (case table
    "answers"   (db/get-last-ordnen-answer {:question-id id})
    "questions" (db/get-last-ordnen-questions {:test-id id})))

(defn- ^:private create-question!
  [context args value]
  (log/info :msg (str ">>> ARGGSSSS >>>>> " args))
  ;; (let [full-params (-> params
  ;;                       (update :qtype   #(Integer/parseInt %))
  ;;                       (update :test-id #(Integer/parseInt %)))
  ;;       errors      (val-test/validate-question full-params)]
  ;;   (if (nil? errors)
  ;;     (as-> full-params v
  ;;       (db/create-question! v)
  ;;       (link-test-question! v (:test-id full-params))
  ;;       (db/get-last-question {:test-id (:test-id full-params)}))
  ;;     {:flash errors :ok false})
    )


(defn resolver-map
  "Public. Match resolvers."
  [component]
  {:test-by-id (partial resolve-test-by-id)
   :questions-by-test (partial resolver-get-questions-by-test)
   :add-question (partial create-question!)})
