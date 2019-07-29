(ns space.site.cljs.elements.navbar
  (:require [space.site.cljs.events.time :as time]))

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
        [:nav.tabs
          [:div.container
            [:ul
              [:li.is-active [:a {:href "/"} "Forum"]]
              [:li [:a {:href "/tags"} "Tags"]]
              [:li [:a {:href "/members"} "Members"]]
              [:li [:a {:href "/admin"} "Admin"]]]]]]]]])

