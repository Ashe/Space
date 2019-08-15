(ns space.site.cljs.events.post
  (:require [re-frame.core :as rf]))

;; Fetch a post for the current page
(rf/reg-event-db
  :fetch-post
  (fn [db [_ post]]
    (assoc db :post post)))

;; Read response from server
;; - Either redirect to new post or remain on page
;; - Notify user of success or failure
(rf/reg-event-fx
  :post-submission-success
  (fn [_ [_ response]]
    (let [postid (:new-post-id response)]
      (if (pos? postid)
        (do
          (println "Submitted post: " postid)
          { :nav-to (str "/post/" postid)
            :dispatch
            [ :new-notification
              [ "Post submission successful!"
                ""
                "is-success"
                "fa-file-check"]]})
        (do
          (println "Failed to submit post.")
          {:dispatch 
            [:new-notification
              [ "Post Submission failed"
                "Something went wrong. Please try again later."
                "is-danger"
                "fa-file-times"]]})))))

;; Get the currently loaded post
(rf/reg-sub
  :post
  (fn [db _]
    (:post db))) 

(defn dispatch-fetch-post
  "Get a post by its postID"
  [post-id]
  (rf/dispatch [:http-get
      [ (str "post/" post-id)
        :fetch-post :bad-http-result]]))

(defn dispatch-submit-post
  "Submit a post to the Space API"
  [title content is-anonymous]
  (rf/dispatch [:http-post 
      [ "forum/submit"
        { :post-title title
          :post-content content 
          :is-anonymous is-anonymous}
        :post-submission-success
        :bad-http-result]]))
