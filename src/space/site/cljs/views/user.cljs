(ns space.site.cljs.views.user
  (:require [re-frame.core :as rf]
            [space.site.cljs.views.common.tags :as tags]
            [space.site.cljs.views.common.posts :as post]
            [space.site.cljs.events.post :as p]))

(defn user
  "Show a specific user's page"
  [{:keys [route-key path-params query-params]}]
  [:div.container.is-widescreen 

    ;; Breadcrumb
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li>a {:href "/users/"} "Users"]
        [:li.is-active>a "Space Team"]]]

    [:div.columns.is-vcentered

      ;; Profile picture 
      [:div.column.is-narrow
        [:div.box
          [:figure.has-text-centered
            [:span.image.is-inline-block
                {:style
                  { :max-width "128px"
                    :max-height "128px"}}
              [:img 
                {:src "https://cdn.pixabay.com/photo/2018/10/16/09/55/astronaut-3751046_960_720.png"
                 }]]]]]
      
      ;; Name, username, social media
      [:div.column
        [:article.message.is-primary
          [:div.message-body
            [:div.columns.is-vcentered
              [:div.column
                [:h1.title "Usernick"]
                [:h2.subtitle "@username"]]
              [:div.column
                [:div.tags
                  (map tags/make-tag (range 3))]]]
            [:p "Social media links?"]]]]]
      
    ;; Bio
    [:div.level
      [:div.level-item
        [:h3.title.is-5 "About me"]]]
    [:article.message.is-info
      [:div.message-body
        [:p (repeat 20 "Lorum ipsim ")]]]

    ;; Tags
    [:div.level
      [:div.level-item
        [:h3.title.is-5 "Tags"]]]
    [:article.message.is-warning
      [:div.message-body
        [:div.tags
          (map tags/make-tag 
            (shuffle (reduce concat (repeat 10 (range 3)))))]]]

    ;; Posts
    [:div.level
      [:div.level-item
        [:h3.title.is-5 "Posts"]]]
     [:article.message.is-link
       [:div.message-body
        (let [posts @(rf/subscribe [:posts])]
          (doall (map #(post/make-post % {:hide-names true}) posts)))]]
      
      ])
