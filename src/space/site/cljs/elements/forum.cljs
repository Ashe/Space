(ns space.site.cljs.elements.forum
  (:require [space.site.cljs.elements.notifications :refer [notify]]))

;; Forward declarations
(declare post selection-bar pagination)

;; Make all elements spaced evenly
(def forum-spacing "10px 0px")

(defn forum
  "Draw forum posts"
  [{:keys [route-key path-params query-params]}]
  [:div.container.is-widescreen
    [:div.container.is-fluid
      [selection-bar]
      (map post (range 3))
      [pagination]]])

(defn selection-bar
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

(defn post
  "An overview of a post"
  [post-id]
  [:div
      { :id (str "post-" post-id)
        :key post-id
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
                {:on-click (notify
                    "Cannot open forum post"
                    "Not yet implemented!"
                    "is-danger")}
               [:strong.is-size-4 "A post about Clojure"]] [:br]
              [:a [:strong "Example User "] "@foo"] [:small " 1m ago"] [:br]

              "Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
              Proin ornare magna eros, eu pellentesque tortor vestibulum ut. 
              Maecenas non massa sem. Etiam finibus odio quis feugiat facilisis."]
            [:div.tags
              [:a.tag.is-info "Clojure"]
              [:a.tag.is-success "Reagent"]
              [:a.tag.is-warning "Re-Frame"]]]]]]])

;;@TODO: Make pagination change depending on current page
(defn pagination
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

