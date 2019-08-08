(ns space.site.cljs.events.post
  (:require [re-frame.core :as rf]))

(defn dispatch-submit-post
  "Submit a post to the API"
  [title content is-anonymous]
  (rf/dispatch [:http-post [
      "forum/submit"
      { :post-title title
        :post-content content 
        :is-anonymous is-anonymous}
      :good-http-result
      :bad-http-result]]))
