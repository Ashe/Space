(ns space.api.db.users.sign-in
  (:require [next.jdbc.sql :as sql]
            [space.api.response :as r]
            [space.api.security.core :as s]
            [space.api.db.connection :as db]))

(defn attempt-sign-in
  "Attempt to authenticate and authorize a user"
  [request]
  (let [body (:body request)
        username (:username body)
        password (:password body)
        [query] (sql/query @db/spec
            [ "SELECT * FROM users
              WHERE username=? AND password=?
              LIMIT 1"
              username password])
        id (:users/user_id query)
        vname (:users/username query)
        vnick (:users/user_nick query)]
      (if (and id vname vnick)
        (r/ok { :user
                { :token (s/make-token id)
                  :user-id id
                  :username vname
                  :user-nick vnick}})
        (r/bad-request {:message "Unrecognised credentials."}))))
