(ns space.api.db.core
  (:require [clojure.java.jdbc :as sql]))

;; String to use for SQL calls
(def db-spec (atom 
  {:dbtype "postgresql"
   :host "localhost:5432"
   :dbname "space"
   :user "space"
   :password "nebula"}))

(defn setup-db
  "Creates necessary tables for space"
  [db-host]
  (when db-host 
    (do
      (println "Configuring database..")
      (println "- Setting host to: " db-host)
      (swap! db-spec #(assoc % :host db-host))))
  (println "Checking database..")
  (println "- Number of posts: " (map :count
      (sql/query @db-spec ["SELECT COUNT(*) FROM Posts"])))
  (println "- Number of users: " (map :count
      (sql/query @db-spec ["SELECT COUNT(*) FROM Users"]))))
