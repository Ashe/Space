(ns space.site.cljs.route.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [space.site.cljs.views.navbar :as navbar]
            [space.site.cljs.views.notifications :as notifications]
            [space.site.cljs.views.sign-in :as sign-in]
            [space.site.cljs.views.footer :as footer]
            [space.site.cljs.views.forum :as forum]
            [space.site.cljs.views.post :as post]
            [space.site.cljs.views.user :as user]
            [space.site.cljs.views.create.core :as create]))

;; Forward declarations
;;@TODO: Place these functions in their own element files
(declare not-found create-new tags tag users info admin get-page-content)

;; Determine what URLs match to what view
(def routes 
  ["/" 
      {""                 :posts
      "posts/"            {"" :posts
          ["page-"        :page-number] :posts
          [:post-number]  :post}
      "new/"              {"" :new
                          [:post-type] :new}
      "tags/"             {"" :tags
          ["page-"        :page-number] :tags
          [:tag-id]       :tag}
      "users/"            {"" :users
          ["page-"        :page-number] :users
          [:user-id]      :user}
      "sign-in/"          :sign-in
      "info/"             {"" :info}
      "admin/"            :admin}])

;; Import routing events
(rfr/register-events {:routes routes})

(defn routed-page
  "Component containing the page after routing"
  []
  (let [route-key @(rf/subscribe [:router/route])
        route-data {
            :route-key route-key
            :path-params @(rf/subscribe [:router/route-params])
            :query-params @(rf/subscribe [:router/route-query])}]
    [:div
      [navbar/navbar route-key]
      [notifications/mobile-alerts]
      [:section.section
        [(get-page-content route-key) route-data]]
      [notifications/notification-panel]
      [footer/footer]]))

;; Choose which component function to use depending on route
(defmulti get-page-content identity)
(defmethod get-page-content :posts [] forum/forum)
(defmethod get-page-content :post [] post/post)
(defmethod get-page-content :new [] create/create-new)
(defmethod get-page-content :tags [] tags)
(defmethod get-page-content :tag [] tag)
(defmethod get-page-content :users [] users)
(defmethod get-page-content :user [] user/user)
(defmethod get-page-content :sign-in [] sign-in/sign-in)
(defmethod get-page-content :info [] info)
(defmethod get-page-content :admin [] admin)
(defmethod get-page-content :default [] not-found)

;; @TODO: Expand on this
(defn- not-found
  "404 Page component"
  [{:keys [route-key path-params query-params]}]
  [:div "Page not found"])

;; @TODO: Expand on this
(defn- create-new
  "Create a new post"
  [{:keys [route-key path-params query-params]}]
  [:div "Create something new!"])

;; @TODO: Expand on this
(defn- tags
  "Display tags used in this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Tags"])

;; @TODO: Expand on this
(defn- tag
  "Display posts and users with this tag"
  [{:keys [route-key path-params query-params]}]
  [:div (str "Tag: " (:tag-id path-params))])

;; @TODO: Expand on this
(defn- users
  "Show users who belong to this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Users"])

;; @TODO: Expand on this
(defn- info
  "Show tools for moderation and configuration of Space"
  [{:keys [route-key path-params query-params]}]
  [:div "Info"])

;; @TODO: Expand on this
(defn- admin
  "Show tools for moderation and configuration of Space"
  [{:keys [route-key path-params query-params]}]
  [:div "Admin"])
