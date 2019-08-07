(ns space.site.cljs.views.create.post
  (:require [space.site.cljs.events.notifications :as n]))

(declare form info-panel)

(defn create-post
  "Shows an overview of post expectations and the form"
  []
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
    [:div.container.is-fluid
      [:div.columns
        [:div.column.is-half.has-text-success
          [:h3.is-size-5.has-text-weight-bold "Do:"]
          [:ol
            [:li "Include insightful or positive ideas in your post"]
            [:li "Ensure that you have done research where you can"]
            [:li "Be clear and take care with grammar and spelling"]
          ]]
        [:div.column.is-half.has-text-danger
          [:h3.is-size-5.has-text-weight-bold "Don't:"]
          [:ol
            [:li "Post anything illegal or against forum rules"]
            [:li "Single out or rant at other users (ignore or report them)"]
            [:li "Knowingly lie or mislead other users unless it's clearly in jest"]
        ]]]]])

(defn form
  "Form for creating a new post"
  []
  [:div

    ;; Title
    [:div.field
      [:label.label "Post title"]
      [:div.control.has-icons-left.has-icons-right
        [:input.input.is-danger
          {:type "text"
           :placeholder "I've been thinking about.."}]
        [:span.icon.is-small.is-left
          [:i.fas.fa-pencil]]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]]

    ;; Tags
    ;; @TODO: Implement the ability to grab tags
    [:div.field
      [:label.label "Tags"]
      [:div.control.has-icons-right
        [:textarea.textarea.is-danger
          {:type "text"
           :placeholder "List related tags here"}]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]]

    ;; Body
    [:div.field
      [:label.label "Content"]
      [:div.control.has-icons-right
        [:textarea.textarea.is-danger
          {:type "text"
           :placeholder "What have you been thinking about?"}]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]]
    
    ])
