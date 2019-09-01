(ns space.site.cljs.views.common.tags
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [space.site.cljs.events.notifications :as n]))

(defn make-tag
  "Make a tag from a tag's ID"
  [tag-data]
  (let [info @(rf/subscribe [:space-info])
        user @(rf/subscribe [:user])
        tag 
            (merge 
              tag-data 
              ((keyword (str (:id tag-data))) (:tags info)))]
    (when tag
      [:div.control 
          {:key (str "tag-" (:id tag))}
        [:div.tags.has-addons
          [:span.tag.is-primary
            [:a 
                { :href (str "/tags/" (:label tag))
                  :style 
                  { :color "#ffffff"
                    :background-color "#00d1b2"}}
              (str/capitalize (:label tag))]]
          (when (contains? tag :points)
            (seq [[:span.tag.is-dark 
              (str (:points tag))]
            (when user
              [:span.tag.is-primary
                [:a 
                    { :style {:color "#ffffff"}
                      :on-click
                        (n/dispatch-notification
                            "Cannot praise post"
                            "Feature not yet implemented"
                            "is-danger"
                            "fa-do-not-enter")}
                  [:i.fas.fa-chevron-up]]])]))
               ]])))
