(ns space.site.cljs.views.common.tags
  (:require [clojure.string :as str]
            [re-frame.core :as rf]))

;; @TODO: Make this customisable
(defn make-tag
  "Make a tag from a tag's ID"
  [tag-data]
  (let [info @(rf/subscribe [:space-info])
        tag 
            (merge 
              tag-data 
              ((keyword (str (:id tag-data))) (:tags info)))]
    (println "TAG: " tag)
    (when tag
      [:div.control 
          {:key (str "tag-" (:id tag))}
        [:a 
            { :href (str "/tags/" (:label tag))
              :style {:text-decoration "none"}}
          [:div.tags.has-addons
            [:span.tag.is-primary
                {:style
                  { :color "#ffffff"
                    :background-color "#00d1b2"}}
              (str/capitalize (:label tag))]
            (when (contains? tag :points)
              [:span.tag.is-dark 
                (str (:points tag))])]]])))
