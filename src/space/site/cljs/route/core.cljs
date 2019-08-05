(ns space.site.cljs.route.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [space.site.cljs.elements.navbar :as navbar]
            [space.site.cljs.elements.notifications :as notifications]
            [space.site.cljs.elements.footer :as footer]
            [space.site.cljs.elements.forum :as forum]))

;; Forward declarations
(declare not-found tags members admin get-page-content)

;; Determine what URLs match to what view
(def routes 
  ["/" {"" :forum
        "forum" :forum
        "tags" :tags
        "members" :members
        "admin" :admin}])

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
      [:section.section
        [(get-page-content route-key) route-data]]
      [footer/footer]
      [notifications/notification-panel]]))


;; Choose which component function to use depending on route
(defmulti get-page-content identity)
(defmethod get-page-content :forum [] forum/forum)
(defmethod get-page-content :tags [] tags)
(defmethod get-page-content :members [] members)
(defmethod get-page-content :admin [] admin)
(defmethod get-page-content :default [] not-found)

;; @TODO: Expand on this
(defn- not-found
  "404 Page component"
  [{:keys [route-key path-params query-params]}]
  [:div "Page not found"])

;; @TODO: Expand on this
(defn- tags
  "Display tags used in this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Tags"])

;; @TODO: Expand on this
(defn- members
  "Show members who belong to this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Members"])

;; @TODO: Expand on this
(defn- admin
  "Show tools for moderation and configuration of Space"
  [{:keys [route-key path-params query-params]}]
  [:div "Admin"])
