(ns space.site.cljs.views.create.post
  (:require [reagent.core :as r]
            [space.site.cljs.events.post :as p]))

(declare form info-panel make-text-input make-checkbox)

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

;; Constraints for post
(def title-min 10)
(def title-max 100)
(def content-min 25)
(def content-max 0)

;; Atoms for form validation
(def title (r/atom ""))
(def content (r/atom ""))
(def is-anonymous (r/atom false))
(def has-agreed (r/atom false))
(defn ready-to-submit [] (and
    @has-agreed 
    (>= (count @title) title-min)
    (<= (count @title) title-max)
    (>= (count @content) content-min)))

(defn form
  "Form for creating a new post"
  []
  [:div

    ;; Title
    (make-text-input title 
        :input.input 
        "Title" "What is your post about?"
        "Thanks!" "Please enter a descriptive title" 
        title-min title-max)

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

    ;; Body
    (make-text-input content 
        :textarea.textarea 
        "Content" "What do you want to talk about?"
        "Thanks!" "Please write your post" 
        content-min content-max)

    ;; Ask the user if they want their post to remain anonymous
    (make-checkbox is-anonymous "Please make my post anonymous.")

    ;; Agree to forum rules
    (make-checkbox has-agreed 
        '("I have read and understood the "
          [:a {:href "/info/#guidelines"} "Community Guidelines"]"."))

      ;; Submit
      [:div.field.is-grouped
        [:div.control
          [:button.button.is-link 
              { :disabled (not (ready-to-submit))
                :on-click #(p/dispatch-submit-post @title @content @is-anonymous)}
            "Post"]]
        [:div.control
          [:a.button.is-text 
              {:href "/"}
            "Back to forum"]]]])

(defn- make-text-input
  "Makes an input with a given atom, tag and set of messages"
  [input-atom input-type label placeholder help-okay help-invalid min-length max-length]
  (let [length (count (or @input-atom ""))
        colour (cond
                  (zero? length) ""
                  (or 
                    (and (> min-length 0) (< length min-length))
                    (and (> max-length 0) (> length max-length))) 
                    "is-danger"
                  :else "is-success")
        icon (if (and 
                   (or (<= min-length 0) (>= length min-length)) 
                   (or (<= max-length 0) (<= length max-length)))
                "fa-check" "fa-exclamation-triangle")]
  [:div.field
    [:label.label label]
    [:div.control.has-icons-right
      [input-type
        { :type "text"
          :placeholder placeholder
          :on-change #(reset! input-atom (.-value (.-target %)))
          :class colour}]
      [:span.icon.is-small.is-right
        [:i.fas {:class icon}]]]
      (cond
        (< length min-length) 
          [:p.help
              {:class colour}
            help-invalid " (" (str (- min-length length)) " characters to go)"]
        (or (<= max-length 0) (< length max-length))
          [:p.help.is-success
            help-okay (when (pos? max-length) (str " (" (str (- max-length length)) " characters left)"))]
        (> max-length 0)
          [:p.help.is-danger
            help-invalid " (" (str (- length max-length)) " characters too many)"])]))

(defn- make-checkbox
  "Makes a checkbox"
  [checkbox-atom label]
  [:div.field
    [:div.control
      [:label.checkbox
        [:input 
            { :type "checkbox"
              :value @checkbox-atom
              :on-change #(reset! checkbox-atom (.-checked (.-target %)))
              :style {:margin-right "8px"}}]
        label]]])
