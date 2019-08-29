(ns space.api.db.forum.core
  (:require [clojure.math.numeric-tower :as math]
            [next.jdbc.sql :as sql]
            [buddy.auth :as auth]
            [space.common.core :as cmn]
            [space.api.response :as r]
            [space.api.security.url :as url]
            [space.api.db.connection :as db]
            [space.api.db.users.privilages :as up]
            [space.api.db.forum.post :as p]))

;; How many posts from the database to send per page
(def posts-per-page 10)

(defn get-forum-page-count
  "Get how many pages there are in the database"
  [_]
  (if-let [[q] (sql/query @db/spec ["SELECT COUNT(post_id) FROM posts"])]
    (r/ok {:pages (math/ceil (/ (:count q) posts-per-page))})
    (r/bad-request {:message "API Error: (get-forum-page-count)"})))

(defn get-forum-page
  "Get a forum page from the database"
  [request]
  (let [page (cmn/str->num (get-in request [:params :page]))
        id (up/check-privilages (:identity request))]
    (if-let [query
        (sql/query @db/spec
          [ (str
            "SELECT " (p/get-post-query false) 
            "FROM posts 
            LEFT OUTER JOIN users ON posts.poster_id=users.user_id
            LIMIT ? OFFSET ?")
            posts-per-page
            (max 0 (* page posts-per-page))])]
      (r/ok {:posts (map (partial p/prepare-forum-post id false) query)})
      (r/bad-request {:message "API Error: (get-forum-page)"}))))

(defn get-forum-post
  "Get an individual forum post from the database"
  [request]
  (let [post-id (cmn/str->num (get-in request [:params :post]))
        id (up/check-privilages (:identity request))]
    (if (pos? post-id)
      (let [[query] (sql/query @db/spec
            [ (str 
              "SELECT " (p/get-post-query true) 
              "FROM posts
              LEFT OUTER JOIN users On posts.poster_id=users.user_id
              WHERE posts.post_id=?
              LIMIT 1")
              post-id])]
        (if query
          (r/ok {:post (p/prepare-forum-post id true query)})
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
          (sql/insert! @db/spec :posts
              { :post_title (:post-title body)
                :poster_id (:usr id)
                :post_summary (:post-summary body)
                :post_content (:post-content body)
                :post_image 
                    (if (url/valid-url? (:post-image body)) 
                        (:post-image body) 
                        nil)
                :is_anonymous (when auth? (:is-anonymous body))})]
        (let [postid (:posts/post_id result)] 
          (println "Submitted new post: " postid)
          (r/ok {:new-post-id postid}))
        (r/bad-request {:message "API Error: (submit-forum-post)"}))
      (r/bad-request {:message "API User Error: (submit-forum-post) - 
                               no body provided."}))))

(defn get-posts-from-user
  "Retrieve posts by a user given a user_id"
  [id user-id & [amount]]
  (let [result (sql/query @db/spec
          [ (str 
              "SELECT "
                (p/get-post-query false)
              "FROM posts
              INNER JOIN users On 
                posts.poster_id=users.user_id
              WHERE users.user_id=?
              LIMIT ?")
           user-id
           (or amount 50)])]
    (if result
      (map 
          (partial p/prepare-forum-post id false)
          result)
      (println "Error (get-posts-from-user): Could
               not find posts by user: " user-id))))

