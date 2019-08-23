(ns space.api.db.core
  (:require [clojure.data.json :as json]
            [clojure.math.numeric-tower :as math]
            [next.jdbc.sql :as sql]
            [buddy.auth :as auth]
            [space.common.core :as cmn]
            [space.api.response :as r]
            [space.api.security.core :as s]))

;; Forward declarations
(declare prepare-forum-post valid-url? check-privilages)

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
  [_]
  (if-let [[q] (sql/query @db-spec ["SELECT COUNT(*) FROM Posts"])]
    (r/ok {:pages (math/ceil (/ (:count q) posts-per-page))})
    (r/bad-request {:message "API Error: (get-forum-page-count)"})))

(defn get-forum-page
  "Get a forum page from the database"
  [request]
  (let [page (cmn/str->num (get-in request [:params :page]))
        id (check-privilages (:identity request))]
    (if-let [query
        (sql/query @db-spec
          [ "SELECT * FROM Posts 
            LEFT OUTER JOIN Users ON Posts.PosterID=Users.UserID
            LIMIT ? OFFSET ?"
            posts-per-page
            (max 0 (* page posts-per-page))])]
      (r/ok {:posts (map (partial prepare-forum-post id false) query)})
      (r/bad-request {:message "API Error: (get-forum-page)"}))))

(defn get-forum-post
  "Get an individual forum post from the database"
  [request]
  (let [post-id (cmn/str->num (get-in request [:params :post]))
        id (check-privilages (:identity request))]
    (if (pos? post-id)
      (let [[query] (sql/query @db-spec
            [ "SELECT * FROM Posts
              LEFT OUTER JOIN Users On Posts.PosterID=Users.UserID
              WHERE Posts.PostID=?
              LIMIT 1"
              post-id])]
        (if query
          (r/ok {:post (prepare-forum-post id true query)})
          (r/bad-request {:message "API Error: (get-forum-post) 
                                   - post not found"})))
      (r/bad-request {
          :message (str "API User Error: (get-forum-post) - 
                        invalid post id (" post-id ")")}))))

(defn submit-forum-post
  "Validate and upload a post to the database, return the post ID on success"
  [request]
  (let [body (:body request)
        auth? (auth/authenticated? request)
        id (:identity request)]
    (if body
      (if-let [result
          (sql/insert! @db-spec :Posts
              { :PostTitle (:post-title body)
                :PosterID (:usr id)
                :PostSummary (:post-summary body)
                :PostContent (:post-content body)
                :PostImage 
                    (if (valid-url? (:post-image body)) 
                        (:post-image body) 
                        nil)
                :IsAnonymous (when auth? (:is-anonymous body))})]
        (let [postid (:posts/postid result)] 
          (println "Submitted new post: " postid)
          (r/ok {:new-post-id postid}))
        (r/bad-request {:message "API Error: (submit-forum-post)"}))
      (r/bad-request {:message "API User Error: (submit-forum-post) - 
                               no body provided."}))))

(defn attempt-sign-in
  "Attempt to authenticate and authorize a user"
  [request]
  (let [body (:body request)
        username (:username body)
        password (:password body)
        [query] (sql/query @db-spec
            [ "SELECT * FROM Users
              WHERE Username=? AND Password=?
              LIMIT 1"
              username password])
        id (:users/userid query)
        vname (:users/username query)
        vnick (:users/usernick query)]
      (if (and id vname vnick)
        (r/ok { :user
                { :token (s/make-token id)
                  :userid id
                  :username vname
                  :usernick vnick}})
        (r/bad-request {:message "Unrecognised credentials."}))))

(defn get-user-data
  "Retrieve information about a given user from database"
  [request]
  (let [id (check-privilages (:identity request))
        username (get-in request [:params :username])
        [usr-query] (sql/query @db-spec
            [ "SELECT * FROM Users
              WHERE Username=?
              LIMIT 1"
              username])
        posts-query (sql/query @db-spec
            [ "SELECT * FROM Posts
              INNER JOIN Users On Posts.PosterID=Users.UserID
              WHERE Users.Username=?
              LIMIT 5"
              username])]
    (if (and usr-query posts-query)
      (r/ok { :viewed-user
              { :username (:users/username usr-query)
                :usernick (:users/usernick usr-query)
                :user-bio (:users/userbio usr-query)
                :user-image (:users/userimage usr-query)
                :is-admin (:users/isadmin usr-query)}
              :posts
                (filter #(not (nil? (:user-id %)))
                  (map (partial 
                       prepare-forum-post id false) 
                    posts-query))})
      (r/bad-request {:message "Couldn't find user"}))))

(defn- check-privilages
  "Checks if the userID is an admin"
  [id]
  (let [[query] (sql/query @db-spec
      [ "SELECT IsAdmin From Users
        WHERE UserID=?
        LIMIT 1"
        (:usr id)])]
    (assoc id :is-admin 
      (true? (:users/isadmin query)))))

(defn- prepare-forum-post
  "Passes only important information t the client"
  [id show-content? p]
  (cond-> 

    ;; Share post details by default
    ;; - Anonymous posts are anonymous regardless 
    ;;   of if you're signed in
    { :post-number (:posts/postid p)
      :post-title (:posts/posttitle p)
      :post-date (:posts/postdate p)
      :post-summary (:posts/postsummary p)
      :post-content (when show-content? (:posts/postcontent p))
      :post-image (:posts/postimage p)
      :is-anonymous (:posts/isanonymous p)
      :tag-ids [0 1 2 3]}

    ;; Reveal user information IF
    ;; - current user is admin
    ;; - post IS NOT anonymous
    ;; - post IS anonymous but owned by current user
    (or 
        (:is-admin id)
        (not (:posts/isanonymous p)) 
        (= (:users/userid p) (:usr id)))
      (assoc 
        :user-id (:users/userid p)
        :username (:users/username p)
        :usernick (:users/usernick p)
        :user-image (:users/userimage p)
        :is-admin (:users/isadmin p))))

;; @TODO: This should probably go somewhere else?
(import 'org.apache.commons.validator.UrlValidator)
(defn- valid-url? 
  "Validate a URL"
  [url-str]
  (let [validator (UrlValidator.)]
    (.isValid validator url-str)))
