(ns space.api.db.core
  (:require [clojure.java.jdbc :as sql]
            [clojure.data.json :as json]
            [clojure.math.numeric-tower :as math]))

(declare prepare-forum-post)

;; Clojure.java.json JSON cannot translate java.sql.Timestamp
(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

;; String to use for SQL calls
(def db-spec (atom 
  { :dbtype "postgresql"
    :host "localhost:5432"
    :dbname "space"
    :user "space"
    :password "nebula"}))

(defn setup-db
  "Creates necessary tables for space"
  [new-db-host]
  (when new-db-host 
    (do
      (println "Configuring database..")
      (println "- Setting host to: " new-db-host)
      (swap! db-spec #(assoc % :host new-db-host))))
  (println "Checking database..")
  (println "- Number of posts: " (map :count
      (sql/query @db-spec ["SELECT COUNT(*) FROM Posts"])))
  (println "- Number of users: " (map :count
      (sql/query @db-spec ["SELECT COUNT(*) FROM Users"]))))

;; How many posts from the database to send per page
(def posts-per-page 10)

(defn get-forum-page-count
  "Get how many pages there are in the database"
  []
  (let [[q] (sql/query @db-spec ["SELECT COUNT(*) FROM Posts"])]
    (when q
      (json/write-str (math/ceil (/ (:count q) posts-per-page))))))

(defn get-forum-page
  "Get a forum page from the database"
  [page]
  (json/write-str 
    (map prepare-forum-post 
      (sql/query @db-spec
        ["SELECT * FROM Posts 
          INNER JOIN Users ON Posts.PosterID=Users.UserID
          LIMIT ? OFFSET ?"
          posts-per-page
          (max 0 (* page posts-per-page))]))))

(defn submit-forum-post
  "Validate and upload a post to the database"
  [post]
  (let [body (:body post)]
    (sql/insert! @db-spec :Posts
      { :PostTitle (:post-title body)
        :PostContent (:post-content body)})
    (json/write-str {:result "Success!"})))

(defn- prepare-forum-post
  "Passes only important information to the client"
  [p]
  { :post-number (:postid p)
    :post-title (:posttitle p)
    :is-admin-post (:isadmin p)
    :user-id (:userid p)
    :username (:username p)
    :user-handle (:userhandle p)
    :post-date (:postdate p)
    :post-summary (:postcontent p)
    :tag-ids [0 1 2 3]})
