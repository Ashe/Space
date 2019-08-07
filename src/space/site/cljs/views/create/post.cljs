(ns space.site.cljs.views.create.post
  (:require [space.site.cljs.events.notifications :as n]))

(defn create-post
  "Submit a new post"
  []
  [:article.container
    [:div.field
      [:label.label "Post title"]
      [:div.control.has-icons-left.has-icons-right
        [:input.input.is-danger
          {:type "text"
           :placeholder "I've been thinking.."}]
        [:span.icon.is-small.is-left
          [:i.fas.fa-pencil]]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]
    ]])
