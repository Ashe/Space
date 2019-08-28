(ns space.site.cljs.views.common.tags
  (:require [clojure.string :as str]
            [re-frame.core :as rf]))

;; @TODO: Make this customisable
(defn make-tag
  "Make a tag from a tag's ID"
  [id & [level]]
  (let [info @(rf/subscribe [:space-info])
        tag ((keyword (str id)) (:tags info)) 
        colour (case id
                  0 "is-info"
                  1 "is-success"
                  2 "is-danger"
                  nil)]
    (when tag
      [:div.control 
          {:key (str "tag-" id)}
        [:a 
            { :href (str "/tags/" "foo")
              :style {:text-decoration "none"}}
          [:div.tags.has-addons
            [:span.tag.is-primary
                {:style
                  { :color "#ffffff"
                    :background-color "#00d1b2"}}
              (str/capitalize (:label tag))]
            [:span.tag.is-dark 
              (str (or level 0))]]]])))
