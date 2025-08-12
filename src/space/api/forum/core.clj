(ns space.api.forum.core
  (:require [space.common.core :as cmn]
            [space.api.response :as r]
            [space.api.db.users.privilages :as up]
            [space.api.db.forum.post :as p]
            [space.api.db.forum.pages :as pg]
            [space.api.db.tags.core :as t]))

(defn get-forum-page-count
  "Gets the number of pages on the forum"
  [_]
  (if-let [page-count (pg/get-page-count)]
    (r/ok {:pages page-count})
    (r/bad-request {:message "API Error: (get-forum-page-count)"})))

(defn get-forum-post
  "Get an individual forum post from the database"
  [request]
  (let [id (up/check-privilages (:identity request))
        post-id (cmn/str->num (get-in request [:params :post]))
        post (p/id->post id post-id)
        tags (get (t/get-post-tags [post-id]) post-id)]
    (if post
      (r/ok { :post (assoc post :tags tags)})
      (r/bad-request {:message (str "API Error: (get-forum-post) 
                                - could not find post: " post-id)}))))

(defn get-forum-page
  "Get a forum page from the database"
  [request]
  (let [id (up/check-privilages (:identity request))
        page-number (cmn/str->num (get-in request [:params :page]))
        page (pg/get-page id page-number)
        tags (t/get-post-tags (map :post-number page))]
    (if page
      (r/ok {:posts 
        (map 
          (fn [post]
            (assoc post :tags 
              (get tags (:post-number post))))
          page)})
      (r/bad-request {:message "API Error: (get-forum-page)"}))))

