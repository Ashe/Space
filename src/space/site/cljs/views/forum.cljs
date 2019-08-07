(ns space.site.cljs.views.forum
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
    (f/dispatch-fetch-page-count)
    (let [page-count @(rf/subscribe [:page-count])
          page-number
              (min page-count (max 1 
              (cmn/str->num (:page-number path-params))))]
      (f/dispatch-fetch-posts (dec page-number))
      (let [posts @(rf/subscribe [:posts])]
        [:div.container.is-widescreen
          [:div.container.is-fluid
            [selection-bar]
            (map make-post posts)
            [pagination page-number page-count]]
          [:div.div.level
              {:style {:margin-top "10px"}}
            [:div.level-item
              [:a.button.is-primary.is-size-5
                  {:href "/new/"}
                [:span.icon
                  [:i.fas.fa-edit]]
                [:span "New Post"]]]]]))))

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
      [:div.field.has-addons
          {:style {:width "100%"}}
        [:div.control.has-icons-left
            {:style {:width "100%"}}
          [:input.input.is-small
              {:type "text"
               :placeholder "Search for posts, users, tags.."}]
          [:span.icon.is-small.is-left
            [:i.fas.fa-search]]]
      [:div.control
        [:a.button.is-info.is-small
          "Search"]]]]
    [:div.level-right
      [:div.level-item
        [:a.button.is-primary.is-small
            {:href "/new/"}
          [:span.icon
            [:i.fas.fa-edit]]
          [:span
            "New Post"]]]]])

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
                  {:href (str "/post/" (:post-number p))}
               [:strong.is-size-4 (:post-title p)]] [:br]
              [:a 
                  {:href (str "/user/" (:user-handle p))}
                [:span.icon
                    (when (not (:is-admin-post p)) {:style {:display "none"}})
                  [:i.fas.fa-shield-check]]
                [:strong (:username p)] (str " @" (:user-handle p))]
              [:small (str " " (:post-date p))] [:br]
              (:post-summary p)]
            [:div.tags
              (map make-tag (:tag-ids p))]
            ]]]]])

(defn- pagination
  "Shows the current page number"
  [page pg-count]
  (let [attr (fn [p] 
          { :aria-label (str "Goto page " p)
            :style (when 
                (and (not= page p)
                  (or (< p 1) (> p pg-count)))
                {:display "none"})
            :class (when (= p page) ["is-link"])
            :href (if (<= p 1) "/" (str "/forum/page-" p))})]
    [:nav.pagination.is-centered
        { :role "navigation"
          :aria-label "pagination"}
      [:a.pagination-previous.button.is-small (attr 1) "First"]
      [:a.pagination-previous.button.is-small (attr (dec page)) "Previous"]
      [:a.pagination-next.button.is-small (attr (inc page)) "Next"]
      [:a.pagination-next.button.is-small (attr pg-count) "Last"]
      [:ul.pagination-list
        [:li>a.pagination-link.button.is-small (attr (- page 2)) (- page 2)]
        [:li>a.pagination-link.button.is-small (attr (dec page)) (dec page)]
        [:li>a.pagination-link.button.is-small (attr page) page]
        [:li>a.pagination-link.button.is-small (attr (inc page)) (inc page)]
        [:li>a.pagination-link.button.is-small (attr (+ page 2)) (+ page 2)]
      ]]))

;; @TODO: Make this customisable
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
          {:class colour
           :href (str "/tag/" label)}
        label])))
