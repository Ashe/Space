(ns space.api.db.forum.post)

(defn get-post-query
  "Get a tailored query string to reduce load on db"
  [show-content?]
  (apply str 
    (interpose ", "
      (filter some?
        [ "post_id"
          "post_title"
          "post_date"
          "post_summary"
          (when show-content?  "post_content")
          "post_image"
          "is_anonymous" 
          "user_id"
          "username"
          "user_nick"
          "user_image"
          "is_admin "]))))

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
      :is-anonymous (:posts/is_anonymous p)
      :tag-ids [0 1 2 3]}

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

