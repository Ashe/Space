(ns space.site.cljs.views.create.core
  (:require [re-frame.core :as rf]
            [space.site.cljs.events.notifications :as n]
            [space.site.cljs.views.create.discussion :as d]))

(declare show-post-types type-tile)

(defn create-new
  "Show different post types to create"
  []
  (when (nil? @(rf/subscribe [:user]))
    ((n/dispatch-notification
        "Continuing as Guest"
        `("You should "
            [:a {:href "/sign-in/"} "sign in"]
          " before going further to earn points!")
        "is-info"
        "fa-user-times")))
  (fn [{:keys [_ path-params _]}]
    (case (:post-type path-params)
      "discussion" [d/create-discussion]
      [show-post-types])))

(defn- show-post-types
  "Show all enabled post types"
  []
  [:div.container.is-widescreen
   
    ;; Breadcrumb
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li.is-active>a "Create"]]]

    [:div.tile.is-ancestor
      [:div.tile.is-parent.is-8
        [type-tile
            "Discussion" 
            '("Post something for people to read and discuss 
            where everyone has the opportunity to earn points 
            while exchanging their ideas." [:br] [:br] 
            "This is the main way of interacting with other 
            users and can be used for any scenario.")
            "fa-comments-alt"
            "discussion"]]
      [:div.tile.is-parent.is-vertical
        [type-tile
            "Question"
            "Ask a question and reward everyone who tried 
            to help solve your problems with points on
            difficulty, attention and age."
            "fa-question"]
        [type-tile
            "Chat"
            "Create a chat room where the listeners,
            participants and moderators can be chosen by name
            or level requirement."
            "fa-comment-dots"]]
    ]])

(defn- type-tile
  "A box containing a post type"
  [title sub icon link]
  [:a.tile.is-child.box
      { :href (when link (str "/new/" link))
        :class (when-not link ["notification" "is-warning"])
        :style {:display "flex"}
        :on-click (when (not link) 
          (n/dispatch-notification
              (str "Cannot create new " title)
              "Not yet implemented."
              "is-danger"
              "fa-exclamation-triangle"))}
    [:section.section.is-paddingless.has-text-centered
        {:style { :margin "auto"
                  :max-width "325px"}}
      [:span.icon.is-large
          {:style { :height "50px"
                    :width "50px"
                    :margin-bottom "15px"}}
        [:i.fa-3x.fas
          {:class icon}]]
      [:h1.title title]
      [:h2.subtitle.has-text-justified sub]]])
