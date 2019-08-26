(ns space.site.cljs.views.notifications
  (:require [re-frame.core :as rf]))

(declare make-notification tray tray-mobile)

;;@TODO: Change max messages depending on window size
(defn notification-panel
  "Component to render all notifications"
  []
  (let [stack @(rf/subscribe [:notifications])
        max-msgs 6
        remaining-msgs (- (count stack) max-msgs)
        notifications (take max-msgs (map make-notification (iterate inc 0) stack))]
    [:div#notifications
      (tray notifications remaining-msgs)
      (tray-mobile notifications remaining-msgs)]))

(defn mobile-alerts
  "Tell mobile users to scroll down when they have notifications"
  []
  (let [stack @(rf/subscribe [:notifications])]
    [:div.has-text-centered
      (when (pos? (count stack))
        [:a.button.is-hidden-tablet.is-link
            { :href "#notifications"
              :style {:width "100%"}}
          "Scroll down to see new notifications"])]))

(defn- tray
  "A notification tray for non-mobile displays"
  [notifications remaining-msgs]
  [:div.is-hidden-mobile
      {:style { :position "fixed"
                :right "20px"
                :bottom "20px"
                :max-width "350px"
                :z-index 1}}
    (when (> remaining-msgs 0)
        [:div.box.has-text-centered
            {:key (str "Notification-count")}
          [:small [:strong (str "+ " remaining-msgs)] (str " more notification"
              (when (> remaining-msgs 1) "s"))]])
    (reverse notifications)])

(defn- tray-mobile
  "A notification tray for mobile displays"
  [notifications remaining-msgs]
  [:div.container.is-fluid.is-hidden-tablet
      {:style {:padding "10px 20px"}}
    notifications
    (when (> remaining-msgs 0)
        [:div.box.has-text-centered
            {:key (str "Notification-count")}
          [:small [:strong (str "+ " remaining-msgs)] (str " more notification"
              (when (> remaining-msgs 1) "s"))]])])

(defn- make-notification
  "An individual notification"
  [i [title msg col icon]]
  (let [colour (or col "is-info")]
    [:div.notification
        { :key (str "Notification-" i)
          :class [colour]}
      [:button.delete 
        {:on-click 
          #(rf/dispatch [:remove-notification i])}]
      [:div.columns.is-vcentered.is-mobile
        [:div.column.is-narrow
          (when icon [:span.icon.is-large
            [:i.fa-3x.fas {:class icon}]])]
        [:div.column
          [:strong title] [:br]
          msg]]]))
