(ns space.site.cljs.views.create.post
  (:require [reagent.core :as r]
            [space.site.cljs.events.notifications :as n]))

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

;; Atoms for form validation
(def title (r/atom ""))
(def has-agreed (r/atom false))
(defn ready-to-submit [] (and
    @has-agreed 
    (> (count @title) 10)
    (< (count @title) 100)))

(defn form
  "Form for creating a new post"
  []
  [:div

    ;; Title
    (let [length (count (or @title ""))
          colour (cond
                    (zero? length) ""
                    (or (< length 10) (> length 100)) "is-danger"
                    :else "is-success")
          icon (if (and (>= length 10) (<= length 100))
                    "fa-check" "fa-exclamation-triangle")]
      [:div.field
        [:label.label "Post title"]
        [:div.control.has-icons-right
          [:input.input
            { :type "text"
              :placeholder "I've been thinking about.."
              :on-change #(reset! title (.-value (.-target %)))
              :class colour}]
          [:span.icon.is-small.is-right
            [:i.fas {:class icon}]]]
          (cond
            (< length 10) [:p.help
                {:class colour}
              "Please enter a valid title (" (str (- 10 length)) " characters left)"]
            (< length 100) [:p.help.is-success
              "Thanks! " (str (- 100 length)) " characters left"]
            (> length 100) [:p.help.is-danger
              "Please enter a valid title (" (str (- length 100)) " characters too many)"])])

    ;; Tags
    ;; @TODO: Implement the ability to grab tags
    [:div.field
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
    [:div.field
      [:label.label "Content"]
      [:div.control.has-icons-right
        [:textarea.textarea.is-danger
          {:type "text"
           :placeholder "What have you been thinking about?"}]
        [:span.icon.is-small.is-right
          [:i.fas.fa-exclamation-triangle]]]
      [:p.help.is-danger
        "Please write a post with at least 20 characters"]]

    ;; Agree to forum rules
    [:div.field
      [:div.control
        [:label.checkbox
          [:input 
              { :type "checkbox"
                :value @has-agreed
                :on-change #(reset! has-agreed (.-checked (.-target %)))}]
            " I have read and understood the "
            [:a {:href "/info/#guidelines"} "Community Guidelines "]
            "expected by this post"]]]

      ;; Submit
      ;;@TODO: Make this work
      [:div.field.is-grouped
        [:div.control
          [:button.button.is-link 
              {:disabled (not (ready-to-submit))}
            "Post"]]
        [:div.control
          [:a.button.is-text 
              {:href "/"}
            "Back to forum"]]]
    ])
