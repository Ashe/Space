(ns space.site.cljs.views.footer
  (:require [re-frame.core :as rf]))

(defn footer
  "Footer of website linking to repository"
  []
  [:footer.footer
    [:div.container.is-fluid
      [:div.container
          {:style {:max-width "700px"}}
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
                {:href "https://github.com/ashe/space"}
              [:span.icon
                [:i.fab.fa-github]]
              [:span "Fork on GitHub"]]]
          [:div.column

            ;; Forum links
            ;; @TODO: Link these to somewhere
            [:div.content
              [:div.content.is-marginless
                (if-let [user @(rf/subscribe [:user])]
                  [:a {:href (str "/users/" (:username user))}
                    "Dashboard"]
                  [:a {:href "/sign-in/"} "Sign-in"])]
              [:div.content.is-marginless
                [:a "What is Space?"]]
              [:div.content.is-marginless
                [:a "Why open source?"]]]

            [:div.content.is-marginless
              [:strong "Made with "
                [:a
                  {:href "https://clojure.org"}
                  "Clojure"]
                " and "
                [:span.icon [:i.fa.fa-heart]]]]]]]]])
