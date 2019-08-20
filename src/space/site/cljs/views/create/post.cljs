(ns space.site.cljs.views.create.post
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

(defn create-post
  "Shows an overview of post expectations and the form"
  []

  ;; Clear values
  (reset! title "")
  (reset! summary "")
  (reset! content "")
  (reset! post-image "")
  (reset! is-anonymous false)
  (reset! has-agreed false)

  ;; Return the page
  [:div.container
    [:div.columns
      [:article.column.is-hidden-tablet
        [info-panel]]
      [:article.column.is-7
        [form]]
      [:article.column.is-hidden-mobile
        [info-panel]]]])

(defn- info-panel
  "Information about posts"
  []
  [:div.box
    [:div.columns.is-vcentered.is-centered
      [:div.column.is-narrow
        [:div.level-item
            {:style {:padding "5px"}}
          [:span.icon.is-large
            [:i.fa-3x.fas.fa-pencil]]]]
      [:div.column
        [:h1.title.is-hidden-tablet.has-text-centered "Make a new post"]
        [:h1.title.is-hidden-mobile "Make a new post"]]]
    [:h2.subtitle 
      "Post something for others to read 
      and earn points positive or interesting posts.
      Use this opportunity to demonstrate passion,
      skill or knowledge to gain points in your chosen
      tags while exchanging ideas."]
    [:div
      [:div.content.has-text-success
        [:h3.is-size-5.has-text-weight-bold.has-text-success "Do:"]
        [:ol
          [:li "Include insightful or positive ideas in your post"]
          [:li "Ensure that you have done research where you can"]
          [:li "Be clear and take care with grammar and spelling"]]]
      [:div.content.has-text-danger
        [:h3.is-size-5.has-text-weight-bold.has-text-danger "Don't:"]
        [:ol
          [:li "Post anything illegal or against forum rules"]
          [:li "Single out or rant at other users (ignore or report them)"]
          [:li "Knowingly lie or mislead other users unless it's clearly in jest"]]]]])

(defn form
  "Form for creating a new post"
  []
  [:div

    ;; Title
    (f/make-text-input title 
        :input.input "text"
        "Title*" "What is your post about?" "fa-pencil"
        "Thanks!" "Please enter a descriptive title" 
        title-min title-max)

    ;; Summary
    (f/make-text-input summary 
        :textarea.textarea "text"
        "Summary" "What makes your post interesting?" "fa-pencil"
        "Thanks!" "Please describe why someone should visit your post"
        0 summary-max)

    ;; Post image
    [:div.columns.is-vcentered
      [:div.column
        (f/make-url-input post-image 
            :input.input
            "Post Image" "URL to your picture"
            "Optional - your profile picture will be used otherwise" 
            "Thanks!" "Please enter a valid URL")]

    ;; Post's picture
    (when (and (pos? (count @post-image)) (f/valid-url? @post-image))
      [:div.column.is-narrow
      [:figure
        [:span.image.is-inline-block
            {:style
              { :max-width "128px"
                :max-height "128px"}}
          [:img {:src @post-image}]]]])]

    ;; Tags
    ;; @TODO: Implement the ability to grab tags
    [:div.field.is-hidden
      [:label.label "Tags"]
      [:div.control.has-icons-right
        [:textarea.textarea.is-danger
            {:type "text"
             :placeholder "List related tags here"}]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]
      [:p.help.is-danger
        "Please provide at least one tag to give context to your post"]]

    ;; Content
    [f/make-md-input content
        "Post Content" "fa-pencil" 
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

