(ns space.api.users.core
  (:require [space.api.response :as r]
            [space.api.db.users.core :as u]
            [space.api.db.forum.core :as p]
            [space.api.db.users.privilages :as up]))

(defn get-user-data
  "Retrieve information about a given user from database"
  [request]
  (let [id (up/check-privilages (:identity request))
        username (get-in request [:params :username])
        user-id (u/username->id username)
        user (u/get-user user-id)
        user-posts (p/get-posts-from-user id user-id 5)]
    (if (and user user-posts)
      (r/ok { :viewed-user user
              :posts
                (filter 
                    #(not (nil? (:user-id %))) 
                    user-posts)})
      (r/bad-request {:message
          (str "Couldn't find user: " username)}))))
