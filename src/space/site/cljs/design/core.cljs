(ns space.site.cljs.design.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as str]))

(defn get-time
  "Returns the current time as string"
  []
  (-> @(rf/subscribe [:time])
      .toTimeString
      (str/split " ")
      first))

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
  [:div.columns
    [:div.column.has-text-centered
      [:div.field
        [:button.button.is-link.is-large {
            :type "button"
            :on-click #(rf/dispatch [:handler-with-http])}
          "Get stuff"]]]])

;; @TODO: Change appearance depending on current page
(defn navbar
  "Navbar of site"
  []
  [:div
    [:section.hero.is-primary.is-small

      ;; Top bar
      [:div.hero-head
        [:nav.navbar
          [:div.container
            [:div.navbar-brand
              [:span.navbar-item
                [:a.button.is-primary.is-large
                    {:href "/"}
                  [:h1.title 
                    [:span.icon
                      [:i.fas.fa-rocket]]
                    " Space"]]]
              [:span.navbar-burger.burger
                  {:data-target "navbarMenuHeroA"}
                [:span]
                [:span]
                [:span]]]
            [:div.navbar-menu
                {:id "navbarMenuHeroA"}
              [:div.navbar-end
                [:span.navbar-item
                  [:a.button.is-primary.is-inverted
                    [:span.icon
                      [:i.fa.fa-user]]
                    [:span "Sign in"]]]]]]]
        
      ;; Hero footer
      [:div.hero-foot
        [:nav.tabs
          [:div.container
            [:ul
              [:li.is-active [:a {:href "/"} "Forum"]]
              [:li [:a {:href "/tags"} "Tags"]]
              [:li [:a {:href "/members"} "Members"]]
              [:li [:a {:href "/admin"} "Admin"]]]]]]]]])

(defn footer
  "Footer of website linking to repository"
  []
  [:footer.footer
    [:div.container.is-fluid
      [:div.columns
        [:div.column.is-1]
        [:div.column.is-10
          [:div.content

            ;; Logo
            [:h1.title 
              [:span.icon
                [:i.fas.fa-rocket]]
              " Space"]]
          [:div.columns
            [:div.column

              ;; Space information and GitHub link
              [:div.content
                [:p "Space is an open-source forum with a modern take on discussions
                    and collaboration designed from the ground up to be forked and
                    customised to suit your community's needs."]]
              [:a.button.is-primary
                [:span.icon
                  [:i.fab.fa-github]]
                [:span "Fork on GitHub"]]]
            [:div.column

              ;; Forum links
              [:div.content
                [:div.content.is-marginless 
                  [:a "Dashboard"]]
                [:div.content.is-marginless 
                  [:a "What is a Space?"]]
                [:div.content.is-marginless 
                  [:a "Why open source?"]]]

              ;; Link to author
              [:div.content.is-marginless
                [:strong "Made by "
                  [:a 
                    {:href "https://aas.sh"}
                    "Ashley Smith"]
                  " with "
                  [:a 
                    {:href "https://clojure.org"}
                    "Clojure"]
                  " and "
                  [:span.icon [:icon.fa.fa-heart]]]]]]]]]])
