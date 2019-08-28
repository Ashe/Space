(ns space.api.db.users.core
  (:require [next.jdbc.sql :as sql]
            [space.api.response :as r]
            [space.api.db.connection :as db]
            [space.api.db.forum.post :as p]
            [space.api.db.users.privilages :as up]))

(defn get-user-data
  "Retrieve information about a given user from database"
  [request]
  (let [id (up/check-privilages (:identity request))
        username (get-in request [:params :username])
        [usr-query] (sql/query @db/spec
            [ "SELECT * FROM users
              WHERE username=?
              LIMIT 1"
              username])
        posts-query (sql/query @db/spec
            [ "SELECT * FROM posts
              INNER JOIN users On posts.poster_id=users.user_id
              WHERE users.username=?
              LIMIT 5"
              username])]
    (if (and usr-query posts-query)
      (r/ok { :viewed-user
              { :username (:users/username usr-query)
                :user-nick (:users/user_nick usr-query)
                :user-bio (:users/user_bio usr-query)
                :user-image (:users/user_image usr-query)
                :is-admin (:users/is_admin usr-query)}
              :posts
                (filter #(not (nil? (:user-id %)))
                  (map (partial 
                       p/prepare-forum-post id false) 
                    posts-query))})
      (r/bad-request {:message "Couldn't find user"}))))
