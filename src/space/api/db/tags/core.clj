(ns space.api.db.tags.core
  (:require [clojure.string :as str]
            [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]))

(defn retrieve-tags
  "Return all tags and tag-ids in database"
  []
  (let [result (sql/query @db/spec
          ["SELECT tag_id, tag_label FROM tags"])
        collect 
          (fn [m l]
            (assoc m (:tags/tag_id l) 
                {:label (str/lower-case (:tags/tag_label l))}))]
    (reduce collect {} result)))

(defn get-post-tags
  "Return tags and levels for a specific post"
  [post-ids]
  (let [result (sql/query @db/spec
          [ "SELECT 
              tags.tag_id, 
              post_tags.post_id,
              post_tags.base_value
            FROM post_tags
            INNER JOIN tags ON post_tags.tag_id=tags.tag_id
            WHERE post_tags.post_id = ANY(?)"
            (long-array post-ids)])]
    (group-by :post-number (map 
      (fn [l]
        { :id (:tags/tag_id l)
          :post-number (:post_tags/post_id l)
          :points (:post_tags/base_value l)})
      result))))
