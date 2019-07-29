(ns space.site.cljs.events.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [ajax.core :as ajax]))

;; Import subscriptions
(rfr/register-subscriptions)

;; Initialise state with default values
(rf/reg-event-fx
  :initialize
  (fn [{:keys [db]} _]
    {:db 
      {:time (js/Date.)
       :time-color "#f88"}
       :pushy-init true}))

;; -- Domino 2 - Event Handlers -----------------------------------------------
;;@TODO: Filter out unneccessary events
 
(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
  :time-color-change            ;; dispatched when the user enters a new colour into the UI text field
  (fn [db [_ new-color-value]]  ;; -db event handlers given 2 parameters:  current application state and event (a vector)
    (assoc db :time-color new-color-value)))   ;; compute and return the new application state

(rf/reg-event-fx                          ;; note the trailing -fx
  :handler-with-http                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    { :db   (assoc db :show-twirly true)  ;; causes the twirly-waiting-dialog to show??
      :http-xhrio { :method          :get
                    :uri             "http://localhost:3000/count-up/10"
                    :timeout         8000                                           ;; optional see API docs
                    :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                    :on-success      [:success-http-result]
                    :on-failure      [:bad-http-result]}}))

(rf/reg-event-db
  :success-http-result
  (fn [db [_ result]]
    (println (str "Success: " result))
    (assoc db :success-http-result result)))

(rf/reg-event-db
  :bad-http-result
  (fn [db [_ result]]
    ;; result is a map containing details of the failure
    (println (str "Failure" result))
    (assoc db :failure-http-result result)))

