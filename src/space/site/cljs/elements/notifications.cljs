(ns space.site.cljs.elements.notifications
  (:require [re-frame.core :as rf]))

(declare make-notification)

(defn notify
  "Easy way of sending a notification"
  [title msg colour]
  #(rf/dispatch [:new-notification [title msg colour]]))

;; Add a notification to stack
(rf/reg-event-db
  :new-notification
  (fn [db [_ n]]
    (update db :notifications conj n)))

;; Remove notification from stack
(rf/reg-event-db
  :remove-notification
  (fn [db [_ i]]
    (update db :notifications 
      (fn [db] (concat (take i db) (drop (inc i) db))))))

;; Query ongoing notifications
(rf/reg-sub
  :notifications
  (fn [db _]     
    (:notifications db))) 

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

(defn make-notification
  "An individual notification"
  [i [title msg col]]
  [:div.notification
      { :key (str "Notification-" i)
        :class [col]}
    [:button.delete 
      {:on-click 
        #(rf/dispatch [:remove-notification i])}]
    [:strong title] [:br]
    msg])
