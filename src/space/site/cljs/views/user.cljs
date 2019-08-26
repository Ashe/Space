(ns space.site.cljs.views.user
  (:require [re-frame.core :as rf]
            [space.site.cljs.views.common.tags :as tags]
            [space.site.cljs.views.common.posts :as post]
            [space.site.cljs.views.common.user :as usr]
            [space.site.cljs.events.post :as foo]
            [space.site.cljs.events.user :as u]))

;; Forward declarations
(declare show-user user-not-found)

(defn user
  "Show a specific user's page"
  []
  (fn [{:keys [route-key path-params query-params]}]
    (u/dispatch-fetch-user (:user-id path-params))
    (if-let [user @(rf/subscribe [:viewed-user])]
      [show-user user]
      [user-not-found])))

(defn- show-user
  "Show information on a user"
  [user]
  [:div.container.is-widescreen 

    ;; Breadcrumb
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li>a {:href "/users/"} "Users"]
        [:li.is-active>a (:usernick user)]]]

    [:div.columns.is-vcentered

      ;; Profile picture 
      [:div.column.is-narrow
        [:div.box
          [:figure.has-text-centered
            [:span.image.is-inline-block
                {:style
                  { :max-width "128px"
                    :max-height "128px"}}
              [:img {:src (:user-image user) }]]]]]
      
      ;; Name, username, social media
      [:div.column
        [:article.message.is-primary
          [:div.message-body
            [:div.columns.is-vcentered
              [:div.column
                (println user)
                [usr/show-user-name 
                    user
                    :h1.title
                    :h2.subtitle]]
              [:div.column
                [:div.tags
                  (map tags/make-tag (range 3))]]]
            [:p "Social media links?"]]]]]
      
    ;; Bio
    (let [bio (:user-bio user)]
      (when (pos? (count bio))
        `([:div.level
            [:div.level-item
              [:h3.title.is-5 "About"]]]
          [:article.message.is-info
            [:div.message-body
              [:p ~bio]]])))

    ;; Tags
    [:div.level
      [:div.level-item
        [:h3.title.is-5 "Tags"]]]
    [:article.message.is-warning
      [:div.message-body
        [:div.tags
          (map tags/make-tag 
            (shuffle (range 3)))]]]

    ;; Posts
    (let [posts @(rf/subscribe [:posts])]
      (when (pos? (count posts))
        [:div
          [:div.level
            [:div.level-item
              [:h3.title.is-5 "Posts"]]]
           [:article.message.is-link
             [:div.message-body
                (doall (map #(post/make-post % {:hide-names true}) posts))]]]))])

(defn- user-not-found
  "Show a 'user not found' screen"
  []
  [:div "User not found :( "])
