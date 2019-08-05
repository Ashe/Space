(ns space.site.cljs.elements.forum
  (:require [re-frame.core :as rf]
            [space.site.cljs.events.forum :as f]
            [space.site.cljs.events.notifications :as n]))

;; Forward declarations
(declare selection-bar pagination make-post make-tag)

;; Make all elements spaced evenly
(def forum-spacing "10px 0px")

;; @TODO: Fetch the appropriate page of posts
(defn forum
  "Draw forum posts"
  [{:keys [route-key path-params query-params]}]
  (f/dispatch-fetch-posts 0)
  (fn []
    (let [[posts] @(rf/subscribe [:posts])]
      [:div.container.is-widescreen
        [:div.container.is-fluid
          [selection-bar]
          (map make-post posts)
          [pagination]]])))

(defn- selection-bar
  "Sort, filter and search bar"
  []
  [:div.level.is-mobile.is-size-7
    [:div.level-left
        {:style {:margin-right "1em"}}
      [:div.level-item
        [:p "Sort by:"]]
      [:div.level-item
        [:div.select.is-small
          [:select
            [:option "Value"]
            [:option "Newest"]]]]]
    [:div.level-item
      [:input.input.is-small
        {:type "text"
         :placeholder "Search for posts, users, tags.."}]]
    [:div.level-right
      [:a.button.is-small.is-primary
        "Search"]]])

;; @TODO: Place a post's structure in CLJC so that
;; both client AND server can sync up correctly 
(defn- make-post
  "An overview of a post"
  [p]
  [:div
      { :id (str "post-" (:post-number p))
        :key (:post-number p)
        :style {:margin forum-spacing}}
    [:div.box
      [:article.columns.is-vcentered
        [:div.column.is-narrow
            [:figure.has-text-centered
              [:span.image.is-64x64.is-inline-block
                [:img {:src "https://bulma.io/images/placeholders/128x128.png"}]]]]
        [:div.column
          [:div.content
            [:p 
              [:a 
                {:on-click (n/dispatch-notification
                    "Cannot open forum post"
                    "Not yet implemented!"
                    "is-danger")}
               [:strong.is-size-4 (:post-title p)]] [:br]
              [:a [:strong (:poster-name p)] (str " " (:poster-alias p))] 
              [:small (str " " (:post-date p))] [:br]
              (:post-summary p)]
            [:div.tags
              (map make-tag (:tag-ids p))]
              ]]]]])

;;@TODO: Make pagination change depending on current page
(defn- pagination
  "Shows the current page number"
  [page]
  [:nav.pagination.is-centered
      { :role "navigation"
        :aria-label "pagination"}
    [:a.pagination-previous "Previous"]
    [:a.pagination-next "Next"]
    [:ul.pagination-list
      [:li>a.pagination-link 
          {:aria-label "Goto page 1"}
        "1"]]])

;; @TODO: Make this customisable
(defn- make-tag
  "Make a tag from a tag's ID"
  [id]
  (let [label (case id 
                  0 "Clojure"
                  1 "Reagent"
                  2 "Re-frame")
        colour (case id
                  0 "is-info"
                  1 "is-success"
                  2 "is-danger")]
    [:a.tag.is-info 
        {:class colour}
      label]))
