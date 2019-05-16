(ns lacinia-ped.config.options
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config))
