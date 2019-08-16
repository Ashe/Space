(ns space.site.cljs.views.navbar
  (:require [reagent.core :as r]
            [space.site.cljs.events.time :as time]
            [space.site.cljs.events.notifications :as n]))

;; Forward declare
(declare tab)

;; Atom for showing burger menu on mobile
(def show-burger-nav (r/atom false))

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
                  { :data-target "navbarMenuHeroA"
                    :class (when @show-burger-nav "is-active")
                    :on-click #(swap! show-burger-nav not)}
                [:span]
                [:span]
                [:span]]]
            [:div#navbarMenuHeroA.navbar-menu
                {:class (when @show-burger-nav "is-active")}
              [:div.navbar-end
                [:span.navbar-item
                  (time/get-current-time)]
                [:span.navbar-item
                    {:style {:padding-left "0px"}}
                  [:a.button.is-primary.is-inverted

                      {:on-click (n/dispatch-notification
                        (str "Cannot sign in")
                        "Not yet implemented."
                        "is-danger"
                        "fa-exclamation-triangle")}
                    [:span.icon
                      [:i.fa.fa-user]]
                    [:span "Sign in"]]]]]]]

      ;; Hero footer
      [:div.hero-foot
        [:nav.tabs.is-boxed
          [:div.container
            [:ul
              [tab "Forum" [:forum :post :new] "/" route-key]
              [tab "Tags" [:tag :tags] "/tags/" route-key]
              [tab "Members" [:members :user] "/members/" route-key]
              [tab "Info" [:info] "/info/" route-key]
              [tab "Admin" [:admin] "/admin/" route-key]
            ]]]]]]])

(defn- tab
  "Returns a tab that is active when given a matching route"
  [text routes destination page]
  [:li 
      {:class [(when (some #{page} routes) "is-active")]}
    [:a {:href destination}
      text]])


