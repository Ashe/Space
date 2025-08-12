(ns space.api.users.core
  (:require [space.api.response :as r]
            [space.api.db.users.core :as u]
            [space.api.db.forum.core :as p]
            [space.api.db.tags.core :as t]
            [space.api.db.users.privilages :as up]))

(defn get-user-data
  "Retrieve information about a given user from database"
  [request]
  (let [id (up/check-privilages (:identity request))
        username (get-in request [:params :username])
        user-id (u/username->id username)
        user (u/get-user user-id)
        user-posts (p/get-posts-from-user id user-id 5)
        post-tags (t/get-post-tags (map :post-number user-posts))]
    (if (and user user-posts post-tags)
      (r/ok { :viewed-user user
              :posts
                (filter 
                  #(not (nil? (:user-id %))) 
                  (map 
                    (fn [post]
                      (assoc post :tags 
                        (get post-tags (:post-number post))))
                      user-posts))})
      (r/bad-request {:message
          (str "Couldn't find user: " username)}))))
