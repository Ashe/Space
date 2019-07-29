(ns space.site.cljs.design.core
  (:require [space.site.cljs.design.navbar :as navbar]
            [space.site.cljs.design.footer :as footer]
            [space.site.cljs.design.forum :as forum]))

;; Forward declarations
(declare not-found tags members admin)

;; Choose which component function to use depending on route
(defmulti get-page-content identity)
(defmethod get-page-content :home [] forum/forum)
(defmethod get-page-content :default [] not-found)
(defmethod get-page-content :tags [] tags)
(defmethod get-page-content :members [] members)
(defmethod get-page-content :admin [] admin)

;; Retrieve components from their respective namespaces 
(defn navbar [] [navbar/navbar])
(defn forum [] [forum/forum])
(defn footer [] [footer/footer])

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
