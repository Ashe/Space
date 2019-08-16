(ns space.site.cljs.views.post
  (:require [re-frame.core :as rf]
            [space.common.core :as cmn]
            [space.site.cljs.views.common :as cmnv]
            [space.site.cljs.events.post :as p]
            [space.site.cljs.events.notifications :as n]))

(declare make-tag)

(defn post
  "Display the page for a specific forum post"
  []
  (fn [{:keys [route-key path-params query-params]}]
    (let [post-id (cmn/str->num (:post-number path-params))]
      (p/dispatch-fetch-post post-id)
      (when-let [[p] @(rf/subscribe [:post])]
        [:div.container
          [:article.box
            [:div.columns
              
              [:div.column.is-narrow

                ;; User's picture
                (when-let [user-img-src (:user-image p)]
                  [:a {:href (str "/user/" (:user-handle p))}
                    [:figure.has-text-centered
                      [:span.image.is-128x128.is-inline-block
                        [:img {:src user-img-src}]]]])

                ;; Link to user
                [:p (cmnv/create-user-link p true)]

                ;; Post date
                [:p.is-size-7 (:post-date p)]]

              [:div.column

                ;; Post title
                [:h1.title (:post-title p)]

                ;; Post image
                (when-let [post-img-src (:post-image p)]
                  [:a {:href post-img-src}
                    [:figure.has-text-centered
                      [:span.image.is-inline-block
                        [:img {:src post-img-src}]]]])

                ;; Post tags
                [:div.tags
                  (map make-tag (:tag-ids p))]

                ;; Body
                [:p (:post-summary p)]]]]]))))

;; @TODO: Make this customisable
;; @TODO: Merge with views/forum.cljs
(defn- make-tag
  "Make a tag from a tag's ID"
  [id]
  (let [label (case id 
                  0 "Clojure"
                  1 "Reagent"
                  2 "Re-frame"
                  nil)
        colour (case id
                  0 "is-info"
                  1 "is-success"
                  2 "is-danger"
                  nil)]
    (when (and label colour) 
      [:a.tag.is-info 
          { :key (str "tag-" id)
            :class colour
            :href (str "/tag/" label)}
        label])))
