(ns space.site.cljs.views.create.core
  (:require [space.site.cljs.events.notifications :as n]
            [space.site.cljs.views.create.post :as p]))

(declare show-post-types type-box)

(defn create-new
  "Show different post types to create"
  []
  ((n/dispatch-notification
      "Continuing as Guest"
      "You should sign in before going further to earn points!"
      "is-info"
      "fa-user-times"))
  (fn [{:keys [_ path-params _]}]
    (case (:post-type path-params)
      "post" [p/create-post]
      [show-post-types])))

(defn- show-post-types
  "Show all enabled post types"
  []
  [:section.container
    [:div.columns.is-multiline.is-centered.is-vcentered
      [type-box
          "Post"
          "Post something for others to read and 
          gain points for positive or interesting posts."
          "fa-pencil"
          "post"]
      [type-box
          "Question"
          "Ask a question and reward everyone who tried 
          to help solve your problems with points."
          "fa-question"]
      [type-box
          "Chat"
          "Start a conversation on a subject
          where everyone has the chance to earn points." 
          "fa-comment-dots"]
    ]])

(defn type-box
  "A box containing a post type"
  [title sub icon link]
  [:article.column.is-one-third.has-text-centered
    [:a.box
        { :href (when link (str "/new/" link))
          :on-click (when (not link) 
            (n/dispatch-notification
                (str "Cannot create new " title)
                "Not yet implemented."
                "is-danger"
                "fa-exclamation-triangle"))}
      [:div.level
        [:div.level-item
          [:span.icon.is-large
              {:style { :height "50px"
                        :width "50px"}}
            [:i.fa-3x.fas
              {:class icon}]]]]
      [:article
        [:h1.is-size-3.has-text-weight-bold title]
        [:h2.is-size-5 sub]]]])
