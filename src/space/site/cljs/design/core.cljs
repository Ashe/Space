(ns space.site.cljs.design.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [space.site.cljs.router :as router]
            [clojure.string :as str]))

(defn clock
  "Colourful clock component"
  []
  [:h1.title.is-1.has-text-centered {
      :style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn colour-input
  "Colour input for timer"
  []
  [:div.field
    [:label.label "Time color: "]
    [:div.control.has-icons-left
      [:input.input {
          :type "text"
          :value @(rf/subscribe [:time-color])
          :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]
      [:span.icon.is-small.is-left
          [:i.fas.fa-palette]]]])

(def times-clicked (reagent/atom 0))

;; @TODO: Replace this with something useful
(defn get-greeting
  "Get the greeting of the page"
  [n]
  (let [evers (repeat n "ever ")]
    (str "My first " (apply str evers) " react component!")))

;; @TODO: Delete this component and make a site
(defn my-component 
  "First custom component"
  []
  (let [n-evers @times-clicked]
    [:div
      [:p.is-size-4.has-text-centered (get-greeting n-evers)]
      [:br]
      [:div.field
        [:div.columns
          [:div.column
            [:button.button.is-link.is-fullwidth {
                :type "button"
                :on-click #(swap! times-clicked inc)}
              (str "Clicked " n-evers " times!")]]
          [:div.column
            [:button.button.is-link.is-fullwidth {
                :type "button"
                :on-click #(reset! times-clicked 0)}
              "Reset"]]]]
      [:div.columns
        [:div.column.has-text-centered
          [:div.field
            [:button.button.is-link.is-large {
                :type "button"
                :on-click #(rf/dispatch [:handler-with-http])}
              "Get stuff"]]]]]))

(defn main-site
  "Render the entire site"
  []
  [:section.hero.is-fullheight.is-primary.is-bold
    [:div.hero-body
      [:div.box
          {:style {:margin :auto}}
        [:h1.subtitle.is-3.has-text-primary.has-text-centered
          "Hello world, it is now.."]
        [clock]
        [colour-input]
        [my-component]
        [router/router]]]])
