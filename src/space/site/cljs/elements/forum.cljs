(ns space.site.cljs.elements.forum
  (:require [re-frame.core :as rf]
            [space.common.core :as cmn]
            [space.site.cljs.events.forum :as f]
            [space.site.cljs.events.notifications :as n]))

;; Forward declarations
(declare selection-bar pagination make-post make-tag)

;; Make all elements spaced evenly
(def forum-spacing "10px 0px")

(defn forum
  "Draw forum posts"
  []
  (fn [{:keys [route-key path-params query-params]}]
    (let [page-number 
              (max 1 (cmn/str->num (:page-number path-params)))]
      (f/dispatch-fetch-posts (dec page-number))
      (let [posts @(rf/subscribe [:posts])]
        [:div.container.is-widescreen
          [:div.container.is-fluid
            [selection-bar]
            (map make-post posts)
            [pagination page-number]]]))))

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
      { :id (str "post-" (:postid p))
        :key (:postid p)
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
                  { :href (str "/post/" (:postid p))}
               [:strong.is-size-4 (:posttitle p)]] [:br]
              [:a 
                  {:href (str "/user/" (:userid p))}
                [:span.icon
                    (when (not (:isadmin p)) {:style {:display "none"}})
                  [:i.fas.fa-shield-check]]
                [:strong (:username p)] (str " @" (:userhandle p))]
              [:small (str " " (:postdate p))] [:br]
              (:postcontent p)]
            [:div.tags
              (map make-tag (:tag-ids p))]
              ]]]]])

;;@TODO: Make pagination change depending on current page
(defn- pagination
  "Shows the current page number"
  [page]
  (let [attr (fn [p] 
          { :aria-label (str "Goto page " p)
            :style (when (< p 1) {:display "none"})
            :href (if (<= p 1) "/" (str "/forum/page-" p))})]
    [:nav.pagination.is-centered
        { :role "navigation"
          :aria-label "pagination"}
      [:a.pagination-previous (attr (dec page)) "Previous"]
      [:a.pagination-next (attr (inc page)) "Next"]
      [:ul.pagination-list
        [:li>a.pagination-link (attr (- page 2)) (- page 2)]
        [:li>a.pagination-link (attr (dec page)) (dec page)]
        [:li>a.pagination-link.is-current (attr page) page]
        [:li>a.pagination-link (attr (inc page)) (inc page)]
        [:li>a.pagination-link (attr (+ page 2)) (+ page 2)]]]))

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
