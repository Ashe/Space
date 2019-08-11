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
            [pagination 
                (max 1 page-number) 
                (max 1 page-count)]]
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

            ;; Post image
            [:figure.has-text-centered
              [:span.image.is-64x64.is-inline-block
                [:img {:src "https://bulma.io/images/placeholders/128x128.png"}]]]]
        [:div.column
          [:div.content
            [:p 

              ;; Post title
              [:a 
                  {:href (str "/post/" (:post-number p))}
               [:strong.is-size-4 (:post-title p)]] [:br]
              
              ;; User name and handle
              (cond 

                ;; Show user if provided
                (pos? (:user-id p)) 
                  [:a 
                      {:href (str "/user/" (:user-handle p))}
                    [:span.icon
                        (when (not (:is-admin-post p)) {:style {:display "none"}})
                      [:i.fas.fa-shield-check]]
                    [:strong (:username p)] (str " @" (:user-handle p))]

                ;; Show anonymous if it's an anonymous post
                (:is-anonymous p)
                  [:a 
                      {:on-click 
                        (n/dispatch-notification
                            "Cannot open profile"
                            "This user has chosen to remain anonymous for this post 
                                but will still earn points."
                            "is-info"
                            "fa-user-secret")}
                    [:span.icon
                      [:i.fas.fa-user-secret]]
                    [:strong "Anonymous"]]

                ;; Show guest if otherwise
                :else 
                  [:a 
                      {:on-click 
                        (n/dispatch-notification
                            "Cannot open profile"
                            "This post was made by a guest with no profile."
                            "is-info"
                            "fa-user-slash")}
                    [:span.icon
                      [:i.fas.fa-user-slash]]
                    [:strong "Guest"]])

              ;; Post date
              [:small 
                  {:style {:margin-left "5px"}}
                (str " " (:post-date p))] [:br]

              ;; Post summary
              (:post-summary p)]

            ;; Post tags
            [:div.tags
              (map make-tag (:tag-ids p))]
            ]]]]])

(defn- pagination
  "Shows the current page number"
  [page pg-count]

  ;; Get pagination-button attributes
  (let [attr (fn [p & [disable]] 
    (let [invalid (and (not= page p)
        (or (< p 1) (> p pg-count)))]
      { :aria-label (str "Goto page " p)
        :style 
          (when (and invalid (not disable))
            {:display "none"})
        :disabled (and invalid disable)
        :class [(when (= p page) "is-link")]
        :href (if (<= p 1) "/" (str "/forum/page-" p))}))]

    [:nav.pagination.is-centered
        { :role "navigation"
          :aria-label "pagination"}

      ;; Constant buttons
      [:a.pagination-previous.button.is-small (attr 1 true) "First"]
      [:a.pagination-previous.button.is-small (attr (dec page) true) "Prev"]
      [:a.pagination-next.button.is-small (attr (inc page) true) "Next"]
      [:a.pagination-next.button.is-small (attr pg-count true) "Last"]

      ;; Page number buttons
      [:ul.pagination-list
        (let [extras 2
              pages (inc (* 2 extras))
              start (max 1 (min (- pg-count pages -1) (- page extras)))]
          (println "PAGE COUNT: " page "/" pg-count)
          (println "START " start)
          (map
              (fn [p] [:li>a.pagination-link.button.is-small (attr p) p])
              (take pages (iterate inc start))))]
      ]))

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
          { :key (str "tag-" id)
            :class colour
            :href (str "/tag/" label)}
        label])))
