(ns space.site.cljs.events.forum
  (:require [re-frame.core :as rf]))

;; Add posts to the post-list
(rf/reg-event-db
  :fetch-forum-posts
  (fn [db [_ response]]
    (if-let [posts (:posts response)]
      (assoc db :posts posts)
      (println "Site Error: (event :fetch-forum-posts)"))))

;; Fetch the current number of pages
(rf/reg-event-db
  :fetch-forum-page-count
  (fn [db [_ response]]
    (if-let [pg-count (:pages response)]
      (assoc db :page-count pg-count)
      (println "Site Error: (event :fetch-forum-page-count)"))))

;; Allow querying of posts
(rf/reg-sub
  :posts
  (fn [db _]
    (:posts db))) 

(defn dispatch-fetch-posts
  "Dispatch a request to collect forum post data"
  [page-num]
  (when (not (neg? page-num))
    (rf/dispatch [:http-get 
        [ (str "forum/page-" page-num) 
          :fetch-forum-posts :bad-http-result]])))

(defn dispatch-fetch-page-count
  "Dispatch a request to fetch how many pages there are"
  []
  (rf/dispatch [:http-get 
      [ "forum/get-page-count" 
        :fetch-forum-page-count :bad-http-result]]))

