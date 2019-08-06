(ns space.api.db.core
  (:require [clojure.java.jdbc :as sql]
            [clojure.data.json :as json]
            [clojure.math.numeric-tower :as math]))

;; Clojure.java.json JSON cannot translate java.sql.Timestamp
(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

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

;; How many posts from the database to send per page
(def posts-per-page 10)

(defn get-forum-page
  "Get a forum page from the database"
  [page]
  (json/write-str (sql/query @db-spec
    ["SELECT * FROM Posts 
      INNER JOIN Users ON Posts.PosterID=Users.UserID
      LIMIT ? OFFSET ?"
        posts-per-page
        (* (max page 0) posts-per-page)])))

(defn get-forum-page-count
  "Get how many pages there are in the database"
  []
  (let [[q] (sql/query @db-spec ["SELECT COUNT(*) FROM Posts"])]
    (when q
      (json/write-str (math/ceil (/ (:count q) posts-per-page))))))
