(ns space.site.cljs.views.post
  (:require [re-frame.core :as rf]
            [space.common.core :as cmn]
            [space.site.cljs.events.post :as p]
            [space.site.cljs.events.notifications :as n]))

(defn post
  "Display the page for a specific forum post"
  []
  (fn [{:keys [route-key path-params query-params]}]
    (let [post-id (cmn/str->num (:post-number path-params))]
      (p/dispatch-fetch-post post-id)
      (let [post-data @(rf/subscribe [:post])]
        (println "POST: " post-data)
        [:div
          [:p (str post-data)]]))))
