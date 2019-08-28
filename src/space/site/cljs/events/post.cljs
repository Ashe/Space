(ns space.site.cljs.events.post
  (:require [re-frame.core :as rf]))

;; Fetch a post for the current page
(rf/reg-event-db
  :fetch-post
  (fn [db [_ response]]
    (let [post (:post response)]
      (assoc db :post post))))

;; Clear the current post
(rf/reg-event-db
  :clear-post
  (fn [db _]
    (assoc db :post nil)))

;; Read response from server
;; - Either redirect to new post or remain on page
;; - Notify user of success or failure
(rf/reg-event-fx
  :post-submission-success
  (fn [_ [_ response]]
    (let [postid (:new-post-id response)]
      (if (pos? postid)
        (let [link (str "/posts/" postid)]
          (println "Submitted post: " postid)
          { :nav-to link
            :dispatch
            [ :new-notification
              [ "Post submission successful!"
                `("Click " 
                    [:a {:href ~link} "here"] 
                    " to see it.")
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
        :fetch-post :clear-post]]))

(defn dispatch-submit-post
  "Submit a post to the Space API"
  [title content summary image is-anonymous]
  (fn [] (rf/dispatch [:http-post 
      [ "forum/submit"
        { :post-title title
          :post-summary summary
          :post-content content 
          :post-image image
          :is-anonymous is-anonymous}
        :post-submission-success
        :bad-http-result]])))
