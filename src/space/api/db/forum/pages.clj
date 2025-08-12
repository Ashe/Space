(ns space.api.db.forum.pages
  (:require [clojure.math.numeric-tower :as math]
            [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]
            [space.api.db.forum.post :as p]))

;; How many posts from the database to send per page
(def posts-per-page 10)

(defn get-page-count
  "Get how many pages there are in the database"
  []
  (if-let [[q] (sql/query @db/spec ["SELECT COUNT(post_id) FROM posts"])]
    (math/ceil (/ (:count q) posts-per-page))
    (println "Error (get-forum-page-count): Could not get page count")))

(defn get-page
  "Get a forum page from the database"
  [id page-number]
  (let [result (sql/query @db/spec
          [ (str
            "SELECT " (p/get-post-query false) 
            "FROM posts 
            LEFT OUTER JOIN users ON posts.poster_id=users.user_id
            LIMIT ? OFFSET ?")
            posts-per-page
            (max 0 (* page-number posts-per-page))])]
    (if result
      (map (partial p/prepare-forum-post id false) result)
      (println "Error (get-forum-page): Could not find posts 
               on page: " page-number))))
