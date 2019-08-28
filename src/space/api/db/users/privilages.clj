(ns space.api.db.users.privilages
  (:require [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]))

(defn check-privilages
  "Checks if the userID is an admin"
  [id]
  (let [[query] (sql/query @db/spec
      [ "SELECT is_admin From users
        WHERE user_id=?
        LIMIT 1"
        (:usr id)])]
    (assoc id :is-admin 
      (true? (:users/is_admin query)))))
