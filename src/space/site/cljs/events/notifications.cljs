(ns space.site.cljs.events.notifications
  (:require [re-frame.core :as rf]))

(defn dispatch-notification
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