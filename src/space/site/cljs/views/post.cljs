(ns space.site.cljs.views.post
  (:require [re-frame.core :as rf]
            [space.common.core :as cmn]
            [space.site.cljs.events.post :as p]
            [space.site.cljs.events.notifications :as n]))

(declare make-tag)

(defn post
  "Display the page for a specific forum post"
  []
  (fn [{:keys [route-key path-params query-params]}]
    (let [post-id (cmn/str->num (:post-number path-params))]
      (p/dispatch-fetch-post post-id)
      (let [post-data @(rf/subscribe [:post])]
        (println "POST: " post-data)
        [:div.container
          [:article.box
            [:div.columns.is-vcentered
              [:div.column.is-narrow

                ;; User's picture
                [:figure.has-text-centered
                  [:span.image.is-128x128.is-inline-block
                    [:img
                      {:src 
                        "https://bulma.io/images/placeholders/128x128.png"}]]]

                [:a
                  [:strong "Space Team"] [:br]
                  [:small "@space"]]
                [:p.is-size-7
                  "2019-08-15"]]

              [:div.column

                ;; Post title
                [:h1.title "Post title"]

                ;; Post tags
                [:div.tags
                  (map make-tag (range 3))]

                ;; Body
                [:p (str post-data)]]]]]))))

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
