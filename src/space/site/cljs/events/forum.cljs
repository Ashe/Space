(ns space.site.cljs.events.forum
  (:require [re-frame.core :as rf]))

;; Add posts to the post-list
(rf/reg-event-db
  :fetch-forum-posts
  (fn [db [_ posts]]
    (assoc db :posts posts)))

;; Allow querying of posts
(rf/reg-sub
  :posts
  (fn [db _]
    (:posts db))) 

(defn dispatch-fetch-posts
  "Dispatch a request to collect forum post data"
  [page-num]
  (rf/dispatch [:http-get 
      [(str "forum/page-" page-num) 
      :fetch-forum-posts :bad-http-result]]))

