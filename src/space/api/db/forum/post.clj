(ns space.api.db.forum.post
  (:require [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]
            [space.api.db.forum.post :as p]))

;; Forward declarations
(declare get-post-query prepare-forum-post)

(defn id->post
  "Get a post from an ID"
  [id post-id]
  (let [[result] (sql/query @db/spec
          [ (str 
            "SELECT " (get-post-query true) 
            "FROM posts
            LEFT OUTER JOIN users On posts.poster_id=users.user_id
            WHERE posts.post_id=?
            LIMIT 1")
            post-id])]
    (if result
      (prepare-forum-post id true result)
      (println "Error (id->post): Could not find post: " post-id))))

(defn get-post-query
  "Get a tailored query string to reduce load on db"
  [show-content?]
  (apply str 
    (interpose ", "
      (filter some?
        [ "posts.post_id"
          "posts.post_title"
          "posts.post_date"
          "posts.post_summary"
          (when show-content?  "posts.post_content")
          "posts.post_image"
          "posts.is_anonymous" 
          "users.user_id"
          "users.username"
          "users.user_nick"
          "users.user_image"
          "users.is_admin "]))))

(defn prepare-forum-post
  "Passes only important information to the client"
  [id show-content? p]
  (cond-> 

    ;; Share post details by default
    ;; - Anonymous posts are anonymous regardless 
    ;;   of if you're signed in
    { :post-number (:posts/post_id p)
      :post-title (:posts/post_title p)
      :post-date (:posts/post_date p)
      :post-summary (:posts/post_summary p)
      :post-content (when show-content? (:posts/post_content p))
      :post-image (:posts/post_image p)
      :is-anonymous (:posts/is_anonymous p)}

    ;; Reveal user information IF
    ;; - current user is admin
    ;; - post IS NOT anonymous
    ;; - post IS anonymous but owned by current user
    (or 
        (:is-admin id)
        (not (:posts/is_anonymous p)) 
        (= (:users/user_id p) (:usr id)))
      (assoc 
        :user-id (:users/user_id p)
        :username (:users/username p)
        :user-nick (:users/user_nick p)
        :user-image (:users/user_image p)
        :is-admin (:users/is_admin p))))

