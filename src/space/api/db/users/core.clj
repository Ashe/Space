(ns space.api.db.users.core
  (:require [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]))

(defn username->id
  "Get a user's ID from their username"
  [username]
  (let [[result] (sql/query @db/spec
          [ "SELECT user_id FROM users
            WHERE username=?
            LIMIT 1"
            username])]
    (if result
      (:users/user_id result)
      (println "Error (username->id): Could
               not find user with name: " username))))

(defn get-user
  "Retrieves all non-sensitive user data for a given ID"
  [id]
  (let [[result] (sql/query @db/spec
            [ "SELECT 
                username, user_nick, user_bio, 
                user_image, is_admin
              FROM users
              WHERE user_id=?
              LIMIT 1"
              id])]
    (if result
      { :username (:users/username result)
        :user-nick (:users/user_nick result)
        :user-bio (:users/user_bio result)
        :user-image (:users/user_image result)
        :is-admin (:users/is_admin result)}
      (println "Error (get-user): Could
               not find user with id: " id))))
