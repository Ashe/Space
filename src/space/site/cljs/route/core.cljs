(ns space.site.cljs.route.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [space.site.cljs.design.core :as design]))

;; Forward declarations
(declare routes)

;; Import routing events
(rfr/register-events {:routes routes})

;; Determine what URLs match to what view
(def routes 
  ["/" {"" :home
        "home" :home
        "tags" :tags
        "members" :members
        "admin" :admin}])

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
      [:secion.section
        [(design/get-page-content route-key) route-data]]
      [design/footer]]))
