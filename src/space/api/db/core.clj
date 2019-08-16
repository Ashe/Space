(ns space.api.db.core
  (:require [next.jdbc.sql :as sql]
            [clojure.data.json :as json]
            [clojure.math.numeric-tower :as math]))

(declare prepare-forum-post valid-url?)

;; Clojure.java.json JSON cannot translate java.sql.Timestamp
(extend-type java.sql.Timestamp
  json/JSONWriter
  (-write [date out]
  (json/-write (str date) out)))

;; String to use for SQL calls
(def db-spec (atom 
  { :dbtype "postgresql"
    :dbname "space"
    :user "space"
    :password "nebula"}))

(defn setup-db
  "Creates necessary tables for space"
  [db-host db-port]
  (println "Configuring database..")
  (when db-host 
    (do
      (println "- Setting host to: " db-host)
      (swap! db-spec #(assoc % :host db-host))))
  (when db-port 
    (do
      (println "- Setting port to: " db-port)
      (swap! db-spec #(assoc % :port db-port))))
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
  (let [query
      (sql/query @db-spec
        [ "SELECT * FROM Posts 
          LEFT OUTER JOIN Users ON Posts.PosterID=Users.UserID
          LIMIT ? OFFSET ?"
          posts-per-page
          (max 0 (* page posts-per-page))])]
    (json/write-str (map prepare-forum-post query))))

(defn get-forum-post
  "Get an individual forum post from the database"
  [post-id]
  (when (pos? post-id)
    (let [query
        (sql/query @db-spec
          [ "SELECT * FROM Posts
            LEFT OUTER JOIN Users On Posts.PosterID=Users.UserID
            WHERE PostID=?"
            post-id])]
      (json/write-str (map prepare-forum-post query)))))

(defn submit-forum-post
  "Validate and upload a post to the database, return the post ID on success"
  [request]
  (let [body (:body request)]
    (when body
      (let [result
          (sql/insert! @db-spec :Posts
              { :PostTitle (:post-title body)
                :PostContent (:post-content body)
                :PostImage (if (valid-url? (:post-image body)) (:post-image body) nil)
                :IsAnonymous (:is-anonymous body)})
            postid (:posts/postid result)]
        (println "Submitted new post: " postid)
        (json/write-str {:new-post-id postid})))))

;; @TODO: Differentiate between post-summary and post-content based on needs
(defn- prepare-forum-post
  "Passes only important information to the client"
  [p]
  (cond-> 
    { :post-number (:posts/postid p)
      :post-title (:posts/posttitle p)
      :post-date (:posts/postdate p)
      :post-summary (:posts/postcontent p)
      :post-image (:posts/postimage p)
      :tag-ids [0 1 2 3]}

    ;; When there is a user, send stats depending on anonymous
    ;; @TODO: Change 'true' to user's ID
    true
      (#(if (:posts/isanonymous p)
        (assoc % 
          :is-anonymous true)
        (assoc % 
          :user-id (:users/userid p)
          :username (:users/username p)
          :usernick (:users/usernick p)
          :user-image (:users/userimage p)
          :is-admin (:users/isadmin p))))))

;; @TODO: This should probably go somewhere else?
(import 'org.apache.commons.validator.UrlValidator)
(defn- valid-url? [url-str]
  "Validate a URL"
  (let [validator (UrlValidator.)]
    (.isValid validator url-str)))
