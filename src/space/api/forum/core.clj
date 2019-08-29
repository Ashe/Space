(ns space.api.forum.core
  (:require [clojure.math.numeric-tower :as math]
            [space.common.core :as cmn]
            [space.api.response :as r]
            [space.api.db.users.privilages :as up]
            [space.api.db.forum.core :as f]
            [space.api.db.tags.core :as t]))

(defn get-forum-post
  "Get an individual forum post from the database"
  [request]
  (let [id (up/check-privilages (:identity request))
        post-id (cmn/str->num (get-in request [:params :post]))
        post (f/id->post id post-id)
        tags (t/get-post-tags post-id)]
    (if (and post tags)
      (r/ok { :post (assoc post :tags tags)})
      (r/bad-request {:message (str "API Error: (get-forum-post) 
                                - could not find post: " post-id)}))))
