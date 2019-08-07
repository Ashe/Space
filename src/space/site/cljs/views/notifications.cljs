(ns space.site.cljs.views.notifications
  (:require [re-frame.core :as rf]))

(declare make-notification)

;;@TODO: Change max messages depending on window size
(defn notification-panel
  "Component to render all notifications"
  []
  (let [stack @(rf/subscribe [:notifications])
        max-msgs 6
        remaining-msgs (- (count stack) max-msgs)]
    [:div#notification-panel
        {:style { :position "fixed"
                  :right "20px"
                  :bottom "20px"
                  :max-width "350px"}}
      [:div
        (when (> remaining-msgs 0)
            [:div.box.has-text-centered
                {:key (str "Notification-count")}
              [:small [:strong (str "+ " remaining-msgs)] (str " more notification"
                  (when (> remaining-msgs 1) "s"))]])
        (reverse
          (take max-msgs
            (map make-notification (iterate inc 0) stack)))]]))

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
      [:div.columns.is-vcentered
        [:div.column.is-narrow
          (when icon [:span.icon.is-large
            [:i.fa-3x.fas {:class icon}]])]
        [:div.column
          [:strong title] [:br]
          msg]]]))
