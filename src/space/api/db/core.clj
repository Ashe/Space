(ns space.api.db.core
  (:require [clojure.data.json :as json]
            [next.jdbc.sql :as sql]
            [space.api.response :as r]
            [space.api.db.connection :as db]
            [space.api.db.tags.core :as tags]))

;; Clojure.java.json JSON cannot translate java.sql.Timestamp
(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

(defn setup-db
  "Creates necessary tables for space"
  [db-host db-port]
  (println "Configuring database..")
  (when db-host (db/set-host db-host))
  (when db-port (db/set-port db-port))
  (println "Checking database..")
  (println "- Number of posts: " (map :count
      (sql/query @db/spec ["SELECT COUNT(post_id) FROM posts"])))
  (println "- Number of users: " (map :count
      (sql/query @db/spec ["SELECT COUNT(user_id) FROM users"]))))

;; @TODO: Send tags from database
(defn send-space-info
  "Sends information about this Space"
  [_]
  (r/ok { :space-info
    { :name "Space"
      :tags (tags/retrieve-tags)}}))
