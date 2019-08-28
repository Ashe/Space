(ns space.api.db.core
  (:require [clojure.data.json :as json]
            [clojure.math.numeric-tower :as math]
            [next.jdbc.sql :as sql]
            [buddy.auth :as auth]
            [space.common.core :as cmn]
            [space.api.response :as r]
            [space.api.security.core :as s]
            [space.api.db.connection :as db]
            [space.api.db.forum.post :as p]
            [space.api.db.users.privilages :as up]
            [space.api.db.forum.core :as forum]))

;; Forward declarations
(declare valid-url?)

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

