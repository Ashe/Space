(ns space.site.cljs.events.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

(declare http-get)

;; Import subscriptions
(rfr/register-subscriptions)

;; Initialise state with default values
(rf/reg-event-fx
  :initialize
  (fn [{:keys [db]} _]
    {:db {:time (js/Date.)
          :time-color "#f88"
          :notifications []}
     :pushy-init true
     :http-xhrio (http-get "count-up/10" [:success-http-result] [:bad-http-result])}))

;; usage:  (dispatch [:time-color-change 34562])
;; dispatched when the user enters a new colour into the UI text field
;; -db event handlers given 2 parameters:  current application state and event (a vector)
;; compute and return the new application state
(rf/reg-event-db                                
  :time-color-change                            
  (fn [db [_ new-color-value]]                  
    (assoc db :time-color new-color-value)))

(rf/reg-event-db
  :success-http-result
  (fn [db [_ result]]
    (println (str "Success: " result))
    (assoc db :success-http-result result)))

(rf/reg-event-fx
  :bad-http-result
  (fn [cofx [_ result]]
    (println (str "Failure" result))
    { :db (assoc (:db cofx) :failure-http-result result)
      :dispatch [:new-notification 
                    ["Disconnected from server" "Please try again later." "is-danger"]]}))

(defn http-get
  "Creates a HTTP request"
  [uri on-success on-fail]
  { :method          :get
    :uri             (str "http://localhost:3000/" uri)
    ;; optional see API docs
    :timeout         8000
    ;; IMPORTANT!: You must provide this.
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      on-success
    :on-failure      on-fail})
