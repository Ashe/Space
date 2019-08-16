(ns space.site.cljs.views.sign-in
  (:require [reagent.core :as r]
            [space.site.cljs.views.common.forms :as f]
            [space.site.cljs.events.notifications :as n]))

(def username (r/atom ""))
(def password (r/atom ""))
(defn- ready-to-submit? [] (and
  (pos? (count @username))
  (pos? (count @password))))

(defn sign-in
  "Shows the sign-in screen"
  [{:keys [route-key path-params query-params]}]
  [:div.container
    [:div.columns.is-vcentered.is-centered
      [:article.column.is-narrow
        [:div.box
          [:div.columns
            [:div.column
              [:h1.title.is-size-4 "Sign in"]
              [:h2.subtitle.is-size-6 "For existing members"]]
            [:div.column.is-narrow.has-text-centered
              [:span.icon.is-large
                [:i.fad.fa-3x.fa-users]]]]
          
          ;; Username
          (f/make-text-input username
              :input.input "text"
              "Username" "astronaut" "fa-user")

          ;; Password
          (f/make-text-input password
              :input.input "password"
              "Password" "pluto-is-not-a-planet" "fa-key")

      ;; Sign in
      [:div.field.is-grouped
        [:div.control
          [:button.button.is-link 
              { :disabled (not (ready-to-submit?))
                :on-click (n/dispatch-notification
                    "Cannot sign in"
                    "Not yet implemented."
                    "is-danger"
                    "fa-user-times")}
            "Sign in"]]
        [:div.control
          [:a.button.is-text 
              {:on-click (n/dispatch-notification
                  "Cannot retrieve password."
                  "Not yet implemented."
                  "is-danger"
                  "fa-do-not-enter")}
            "Forgot password?"]]]
          ]]

      [:article.column.is-narrow
        [:div.box
          [:div.columns
            [:div.column
              [:h1.title.is-size-4 "Sign up"]
              [:h2.subtitle.is-size-6 "For newcomers"]]
            [:div.column.is-narrow.has-text-centered
              [:span.icon.is-large
                [:i.fad.fa-3x.fa-users-medical]]]]
          [:p 
              {:style {:max-width "350px"}}
            "Space is more fun with more users and ideas.
            Not only can members participate in discussions easier,
            they are also rewarded by doing so. Accumulate experience
            points in different fields so that others can see your
            merits."]
          
          [:br]
          [:p
            [:a.button.is-success 
                {:on-click (n/dispatch-notification
                    "Cannot sign up"
                    "Not yet implemented."
                    "is-danger"
                    "fa-do-not-enter")}
              "Click here to learn more"]]
          ]]
      
      ]])
