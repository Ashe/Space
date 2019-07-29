(ns space.site.cljs.route.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [space.site.cljs.design.core :as design]))

;; Determine what URLs match to what view
(def routes 
  ["/" {"" :home
        "home" :home
        "tags" :tags
        "members" :members
        "admin" :admin}])

;; Import routing events
(rfr/register-events {:routes routes})

;; @TODO: Expand on this
(defn home
  "Home screen component"
  [{:keys [route-key path-params query-params]}]
  [:div "Home and main forum"])

;; @TODO: Expand on this
(defn not-found
  "404 Page component"
  [{:keys [route-key path-params query-params]}]
  [:div "Page not found"])

;; @TODO: Expand on this
(defn tags
  "Display tags used in this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Tags"])

;; @TODO: Expand on this
(defn members
  "Show members who belong to this forum"
  [{:keys [route-key path-params query-params]}]
  [:div "Members"])

;; @TODO: Expand on this
(defn admin
  "Show tools for moderation and configuration of Space"
  [{:keys [route-key path-params query-params]}]
  [:div "Admin"])

;; Choose which component function to use depending on route
(defmulti get-view identity)
(defmethod get-view :home [] home)
(defmethod get-view :default [] not-found)
(defmethod get-view :tags [] tags)
(defmethod get-view :members [] members)
(defmethod get-view :admin [] admin)

(defn routed-page
  "Component containing the page after routing"
  []
  (let [route-key-ref (rf/subscribe [:router/route])
        path-params (rf/subscribe [:router/route-params])
        query-params (rf/subscribe [:router/route-query])
        route-key @route-key-ref
        route-data {
            :route-key route-key
            :path-params @path-params
            :query-params @query-params}]
    [:div
      [design/navbar]
      [(get-view route-key) route-data]
      [design/footer]]))
