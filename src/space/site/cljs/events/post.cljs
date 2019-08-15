(ns space.site.cljs.events.post
  (:require [re-frame.core :as rf]))

;; Read response from server
;; - Either redirect to new post or remain on page
;; - Notify user 
(rf/reg-event-fx
  :post-submission-success
  (fn [cofx [_ response]]
    {:dispatch 
      [:new-notification
        [ "Submission successful!"
          response
          "is-success"
          "fa-file-check"]]}))

(defn dispatch-submit-post
  "Submit a post to the API"
  [title content is-anonymous]
  (rf/dispatch [:http-post 
      [ "forum/submit"
        { :post-title title
          :post-content content 
          :is-anonymous is-anonymous}
        :post-submission-success
        :bad-http-result]]))
