(ns space.site.cljs.views.create.discussion
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [space.site.cljs.views.common.forms :as f]
            [space.site.cljs.events.post :as p]))

;; Forward declare
(declare form info-panel md-editor)

;; Constraints for post
(def title-min 10)
(def title-max 100)
(def summary-max 100)

;; Atoms for form validation
(def title (r/atom ""))
(def tags (r/atom []))
(def summary (r/atom ""))
(def content (r/atom ""))
(def post-image (r/atom ""))
(def is-anonymous (r/atom false))
(def has-agreed (r/atom false))
(defn- ready-to-submit? [] (and
    @has-agreed 
    (or (zero? (count @post-image)) (f/valid-url? @post-image))
    (>= (count @title) title-min)
    (<= (count @title) title-max)
    (<= (count @summary) summary-max)))

(defn create-discussion
  "Shows an overview of discussion expectations and the form"
  []

  ;; Clear values
  (reset! title "")
  (reset! tags [])
  (reset! summary "")
  (reset! content "")
  (reset! post-image "")
  (reset! is-anonymous false)
  (reset! has-agreed false)

  ;; Return the page
  [:div.container

    ;; Breadcrumb
    [:nav.breadcrumb
      [:ul
        [:li>a {:href "/"} "Space"]
        [:li>a {:href "/new/"} "Create"]
        [:li.is-active>a "Discussion"]]]

    [:div.columns
      [:article.column.is-hidden-tablet
        [info-panel]]
      [:article.column.is-7
        [form]]
      [:article.column.is-hidden-mobile
        [info-panel]]]])

(defn- info-panel
  "Information about discussions"
  []
  [:div.container.is-fluid
    [:div.columns.is-vcentered.is-centered
      [:div.column.is-narrow
        [:div.level-item
            {:style {:padding "5px"}}
          [:span.icon.is-large
            [:i.fa-3x.fas.fa-comments-alt]]]]
      [:div.column
        [:h1.title.is-hidden-tablet.has-text-centered "Start Discussion"]
        [:h1.title.is-hidden-mobile "Start Discussion"]]]
    [:h2.subtitle
      "Post something for people to discuss 
      where anyone can earn points towards your chosen
      tags for positive or interesting contributions."]
    [:div.message.is-info
      [:div.message-header
        [:p "Points:"]]
      [:div.message-body
        [:li "You earn from users visiting the post"]
        [:li "Anyone earns from praises by other users"]
        [:li "If the space features your post, points
             awarded from this post will be multiplied"]
        [:li "You can feature someone else's contribution
             and reward them with points based on the posts'
             accumilated total"]]]
    [:div.message.is-success
      [:div.message-header
        [:p "Do:"]]
      [:div.message-body
        [:li "Discuss insightful, or positive ideas"]
        [:li "Criticise and debate constructively"]
        [:li "Ensure you have done your research"]
        [:li "Check clarity, grammar and spelling"]]]
    [:div.message.is-danger
      [:div.message-header
        [:p "Don't:"]]
      [:div.message-body
        [:li "Beg for praises or features"]
        [:li "Share illegal or rule-breaking content"]
        [:li "Attack others (ignore or report them)"]
        [:li "Knowingly lie or mislead other users"]]]])

(defn form
  "Form for creating a new post"
  []
  [:div

    ;; Title
    [f/make-text-input title 
        :input.input "text"
        "Title*" "What is this discussion about?" "fa-pencil"
        "Thanks!" "Please enter a descriptive title" 
        title-min title-max]

    ;; Tags
    (let [space-tags (:tags @(rf/subscribe [:space-info]))]
      [f/make-tag-input tags 
          "Tags*" (map :label (vals space-tags))
          "Help other users find your post using tags" "fa-tags"
          '("Select " [:a {:href "/tags/"} "tags"] " that your post
            belongs to and earn points")])

    ;; Summary
    [f/make-text-input summary 
        :textarea.textarea "text"
        "Summary" "What makes this discussion interesting?" "fa-pencil"
        "Thanks!" "Please describe why someone should visit your post"
        0 summary-max]

    ;; Post image
    [:div.columns.is-vcentered
      [:div.column
        [f/make-url-input post-image 
            :input.input
            "Image" "URL to your picture"
            "Optional - your profile picture will be used otherwise" 
            "Thanks!" "Please enter a valid URL"]]

    ;; Post's picture
    (when (and (pos? (count @post-image)) (f/valid-url? @post-image))
      [:div.column.is-narrow
      [:figure
        [:span.image.is-inline-block
            {:style
              { :max-width "128px"
                :max-height "128px"}}
          [:img {:src @post-image}]]]])]

    ;; Content
    [f/make-md-input content
        "Content" "fa-pencil" 
        "Write your post here"]

    ;; Ask the user if they want their post to remain anonymous
    (when @(rf/subscribe [:user])
      (f/make-checkbox is-anonymous "Please make my post anonymous."))

    ;; Agree to forum rules
    (f/make-checkbox has-agreed 
        '("I have read and understood the "
          [:a {:href "/info/#guidelines"} "Community Guidelines"]"."))

      ;; Submit
      [:div.field.is-grouped
        [:div.control
          [:button.button.is-link 
              { :disabled (not (ready-to-submit?))
                :on-click (p/dispatch-submit-post 
                    @title @content @summary @post-image @is-anonymous)}
            "Post"]]
        [:div.control
          [:a.button.is-text 
              {:href "/"}
            "Back to forum"]]]])

