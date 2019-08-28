(ns space.api.db.tags.core
  (:require [clojure.string :as str]
            [next.jdbc.sql :as sql]
            [space.api.db.connection :as db]))

(defn retrieve-tags
  "Return all tags and tag-ids in database"
  []
  (let [result (sql/query @db/spec
          ["SELECT * FROM tags"])
        collect 
          (fn [m l]
            (assoc m (:tags/tag_id l) 
                {:label (str/lower-case (:tags/tag_label l))}))]
    (reduce collect {} result)))
