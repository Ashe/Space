(ns space.site.cljs.views.create.core
  (:require [space.site.cljs.events.notifications :as n]))

(declare show-post-types type-box)

(defn create-new
  "Show different post types to create"
  []
  (fn [{:keys [_ path-params _]}]
    (case (:post-type path-params)
      [show-post-types])))

(defn- show-post-types
  "Show all enabled post types"
  []
  [:section.container
    [:div.columns.is-multiline.is-centered.is-vcentered
      [type-box
          "Post"
          "Post something for others to read and 
          gain points from valuble posts."
          "fa-pencil"]
      [type-box
          "Question"
          "Ask a question and reward those who
          solve your problems with points."
          "fa-question"]
      [type-box
          "Chat"
          "Start a conversation on a subject
          where everyone has the chance to gain points." 
          "fa-comment-dots"]
    ]])

(defn type-box
  "A box containing a post type"
  [title sub icon]
  [:div.column.is-one-third.has-text-centered
    [:a.box
        { :style {:height "230px"}
          :on-click (n/dispatch-notification
            (str "Cannot create new " title)
            "Not yet implemented."
            "is-danger")}
      [:div.level
        [:div.level-item
          [:span.icon.is-large
              {:style { :height "50px"
                        :width "50px"}}
            [:i.fa-3x.fas
              {:class icon}]]]]
      [:article
        [:h1.title title]
        [:h2.subtitle sub]]]])
