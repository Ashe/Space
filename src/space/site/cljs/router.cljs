(ns space.site.cljs.router
  (:require [re-frame.core :as rf]))

; Determine what URLs match to what view
(def routes 
  ["/" {"" :home
        "home" :home}])

;; @TODO: Expand on this
(defn home
  "Home screen component"
  [{:keys [route-key path-params query-params]}]
  [:div "Home"])

;; @TODO: Expand on this
(defn not-found
  "404 Page component"
  [{:keys [route-key path-params query-params]}]
  [:div "Page not found"])

;; Choose which component function to use depending on route
(defmulti get-view identity)
(defmethod get-view :home [] home)
(defmethod get-view :default [] not-found)

(defn router 
  "Returns the appropriate view for the current route"
  []
  (let [route-key-ref (rf/subscribe [:router/route])
        path-params (rf/subscribe [:router/route-params])
        query-params (rf/subscribe [:router/route-query])
        route-key @route-key-ref]
    [(get-view route-key) {
        :route-key route-key
        :path-params @path-params
        :query-params @query-params}]))
