(ns space.api.db.core
  (:require [clojure.java.jdbc :as sql]))

;; String to use for SQL calls
(def db-spec
  {:dbtype "postgres"
   :dbname "space"
   :user "space"
   :password "nebula"})

(defn setup-db
  "Creates necessary tables for space"
  []
  (println "Checking database..")
  (println "Testing 3 * 5 = " 
    (sql/query db-spec ["SELECT 3*5 AS result"])))
