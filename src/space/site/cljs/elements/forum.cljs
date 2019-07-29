(ns space.site.cljs.elements.forum)

;; Forward declarations
(declare selection-bar post)

(defn forum
  "Draw forum posts"
  [{:keys [route-key path-params query-params]}]
  [:div.container.is-widescreen
    [:div.container.is-fluid
      [selection-bar]
      (repeat 3 (post))]])

;; Make all elements spaced evenly
(def forum-spacing "10px 0px")

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
  []
  [:a.is-block
      {:style {:margin forum-spacing}}
    [:div.box
      [:article.media
        [:figure.media-left
          [:p.image.is-64x64
            [:img {:src "https://bulma.io/images/placeholders/128x128.png"}]]]
        [:div.media-content
          [:div.content
            [:p 
              [:strong.is-size-4 "A post about Clojure"] [:br]
              [:a [:strong "Example User "] "@foo"] [:small " 1m ago"] [:br]

              "Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
              Proin ornare magna eros, eu pellentesque tortor vestibulum ut. 
              Maecenas non massa sem. Etiam finibus odio quis feugiat facilisis."]
            [:div.tags
              [:a.tag.is-info "Clojure"]
              [:a.tag.is-success "Reagent"]
              [:a.tag.is-warning "Re-Frame"]]]]
        [:div.media-right
          [:button.delete]]]]])
