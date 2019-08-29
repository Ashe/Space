(ns space.api.db.forum.core
  (:require [next.jdbc.sql :as sql]
            [buddy.auth :as auth]
            [space.api.response :as r]
            [space.api.security.url :as url]
            [space.api.db.connection :as db]
            [space.api.db.forum.post :as p]))

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

