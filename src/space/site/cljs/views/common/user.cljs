(ns space.site.cljs.views.common.user
  (:require [re-frame.core :as rf]
            [space.site.cljs.events.notifications :as n]))

(defn create-user-link
  "Creates a link to a user's profile"
  [p & [break-line]]
  (let [usr @(rf/subscribe [:user])
        same? (and
                (not (nil? usr))
                (= (:username usr) (:username p)))
        you-tag (when same? [:small " (you)"])]
  (cond 

    ;; Show anonymous if it's an anonymous post
    (:is-anonymous p)
      [:a 
          {:on-click 
            (n/dispatch-notification
                "Cannot open profile"
                "This user has chosen to remain anonymous for this post 
                    but will still earn points."
                "is-info"
                "fa-user-secret")}
        [:span.icon
          [:i.fas.fa-user-secret]]
        [:strong "Anonymous"]
        you-tag]

    ;; Show user if provided
    (pos? (:user-id p)) 
      [:a 
          {:href (str "/user/" (:username p))}
        [:span.icon
            (when (not (:is-admin p)) {:style {:display "none"}})
          [:i.fas.fa-shield-check]]
        [:strong (:usernick p)] 
        (when break-line [:br])
        (str " @" (:username p))
        you-tag]

    ;; Show guest if otherwise
    :else 
      [:a 
          {:on-click 
            (n/dispatch-notification
                "Cannot open profile"
                "This post was made by a guest with no profile."
                "is-info"
                "fa-user-slash")}
        [:span.icon
          [:i.fas.fa-user-slash]]
        [:strong "Guest"]])))
