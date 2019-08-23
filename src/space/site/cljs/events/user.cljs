(ns space.site.cljs.events.user
  (:require [re-frame.core :as rf]))

;; Collect data on current user
(rf/reg-event-db
  :fetch-viewed-user
  (fn [db [_ response]]
    (assoc db 
        :viewed-user (:viewed-user response)
        :posts (:posts response))))

;; Collect data on current user
(rf/reg-event-db
  :failed-fetch-viewed-user
  (fn [db [_ response]]
    (assoc db :viewed-user nil :posts nil)))

;; Query the viewed-user's info
(rf/reg-sub
  :viewed-user
  (fn [db _]
    (:viewed-user db)))

(defn dispatch-fetch-user
  "Dispatch a request to collect user data"
  [username]
  (rf/dispatch [:http-get 
      [ (str "user/" username) 
        :fetch-viewed-user 
        :failed-fetch-viewed-user]]))
