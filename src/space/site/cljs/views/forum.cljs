(ns space.site.cljs.views.forum
  (:require [re-frame.core :as rf]
            [space.common.core :as cmn]
            [space.site.cljs.views.common.posts :as p]
            [space.site.cljs.events.forum :as f]))

;; Forward declarations
(declare selection-bar pagination)

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

          ;; Breadcrumb
          [:nav.breadcrumb
            [:ul
              [:li>a {:href "/"} "Space"]
              [:li.is-active>a "Page 1"]]]

          ;; Selection bar
          [selection-bar]

          ;; Posts
          (doall (map p/make-post posts))

          ;; Pagination
          [pagination 
              (max 1 page-number) 
              (max 1 page-count)]

          ;; New-post button
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

(defn- pagination
  "Shows the current page number"
  [page pg-count]

  ;; Get pagination-button attributes
  (let [attr (fn [p & [k disable]] 
    (let [invalid (and (not= page p)
        (or (< p 1) (> p pg-count)))]
      { :aria-label (str "Goto page " p)
        :key (or k (str "goto-page-" p 
                        "-invalid-" invalid
                        "-disabled" disable))
        :style 
          (when (and invalid (not disable))
            {:display "none"})
        :disabled (and invalid disable)
        :class [(when (= p page) "is-link")]
        :href (if (<= p 1) "/" (str "/posts/page-" (min pg-count p)))}))]

    [:nav.pagination.is-centered
        { :role "navigation"
          :aria-label "pagination"}

      ;; Constant buttons
      [:a.pagination-previous.button.is-small (attr 1 "first "true) "First"]
      [:a.pagination-previous.button.is-small (attr (dec page) "prev" true) "Prev"]
      [:a.pagination-next.button.is-small (attr (inc page) "next" true) "Next"]
      [:a.pagination-next.button.is-small (attr pg-count "last" true) "Last"]

      ;; Page number buttons
      [:ul.pagination-list
        (let [extras 2
              pages (inc (* 2 extras))
              start (max 1 (min (- pg-count pages -1) (- page extras)))]
          (map
              (fn [p] [:li>a.pagination-link.button.is-small (attr p) p])
              (take pages (iterate inc start))))]
      ]))
