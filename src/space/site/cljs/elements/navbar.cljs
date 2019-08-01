(ns space.site.cljs.elements.navbar
  (:require [space.site.cljs.events.time :as time]))

(declare tab foo)

;; @TODO: Change appearance depending on current page
(defn navbar
  "Navbar of site"
  [route-key]
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
            [:div#navbarMenuHeroA.navbar-menu
              [:div.navbar-end
                [:span.navbar-item
                  (time/get-current-time)]
                [:span.navbar-item
                  [:a.button.is-primary.is-inverted
                    [:span.icon
                      [:i.fa.fa-user]]
                    [:span "Sign in"]]]]]]]
        
      ;; Hero footer
      [:div.hero-foot
        [:nav.tabs.is-boxed
          [:div.container
            [:ul
              [tab "Forum" :home "/" route-key]
              [tab "Tags" :tags "/tags" route-key]
              [tab "Members" :members "/members" route-key]
              [tab "Admin" :admin "/admin" route-key]]]]]]]])

(defn- tab
  "Returns a tab that is active when given a matching route"
  [text route destination page]
  [:li {:class [(when (= page route) "is-active")]}
    [:a {:href destination}
      text]])


