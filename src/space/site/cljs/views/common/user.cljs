(ns space.site.cljs.views.common.user
  (:require [re-frame.core :as rf]
            [space.site.cljs.events.notifications :as n]))

(declare show-user-name get-icon show-icon)

(defn create-user-link
  "Creates a link to a user's profile"
  ([u] create-user-link u [])
  ([u f]
  (let [usr @(rf/subscribe [:user])
        same? (and
                (not (nil? usr))
                (= (:username usr) (:username u)))
        you-tag (when same? [:small " (you)"])]
  (cond 

    ;; Show anonymous if it's an anonymous post
    (:is-anonymous u)
      [:a 
        (if-let [username (:username u)]
          {:href (str "/users/" username)}
          {:on-click 
            (n/dispatch-notification
                "Cannot open profile"
                "This user has chosen to remain anonymous for this post 
                    but will still earn points."
                "is-info"
                "fa-user-secret")})
        [show-icon "fa-user-secret"]
        [:strong "Anonymous"]
        (when (and (not same?) (:username u))
          [(if (:seperate-names f) :p :span)
              (str " (@"(:username u) ") ")])
        you-tag]

    ;; Show user if provided
    (pos? (:user-id u)) 
      [:a 
          {:href (str "/users/" (:username u))}
        [show-user-name u 
            :strong 
            (if (:seperate-names f) :p :span)]]

    ;; Show guest if otherwise
    :else 
      [:a 
          {:on-click 
            (n/dispatch-notification
                "Cannot open profile"
                "This post was made by a guest with no profile."
                "is-info"
                "fa-user-slash")}
        [show-icon "fa-user-slash"]
        [:strong "Guest"]]))))

(defn show-user-name
  "Shows the username, nickname and icons"
  [u nick-type username-type]
  (let [usr @(rf/subscribe [:user])
        same? (and
                (not (nil? usr))
                (= (:username usr) (:username u)))
        you-tag (when same? [:small " (you)"])]
    [:span
      [nick-type
        [get-icon u]
        (:user-nick u)]
      [username-type (str " @" (:username u))
        you-tag]]))

(defn get-icon
  "Gets the icon for displayed user"
  [u]
  (when-let [icon
      (cond
        (:is-admin u) "fa-shield-check")]
    [show-icon icon]))

(defn show-icon
  "Show all user-related icons in the same way"
  [icon]
  [:span.icon 
      {:style 
        { :width "inherit"
          :height "inherit"
          :padding-right "5px"}}
    [:i.fas {:class icon}]])
