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
    {:db {:connection-status true
          :notifications []
          :time (js/Date.)
          :time-color "#f88"}
     :pushy-init true
     :http-xhrio (http-get "count-up/10" [:good-http-result] [:bad-http-result])}))

;; usage:  (dispatch [:time-color-change 34562])
;; dispatched when the user enters a new colour into the UI text field
;; -db event handlers given 2 parameters:  current application state and event (a vector)
;; compute and return the new application state
(rf/reg-event-db                                
  :time-color-change                            
  (fn [db [_ new-color-value]]                  
    (assoc db :time-color new-color-value)))

(rf/reg-event-fx
  :good-http-result
  (fn [cofx [_ result]]
    (println (str "Successful HTTP-GET: " result))
    { :db (assoc (:db cofx) :success-http-result result)
      :dispatch [:set-connection-status true]}))

(rf/reg-event-fx
  :bad-http-result
  (fn [cofx [_ result]]
    (println (str "Failed HTTP-GET:" result))
    { :db (assoc (:db cofx) :failure-http-result result)
      :dispatch [:set-connection-status false]}))

(rf/reg-event-fx
  :set-connection-status
  (fn [cofx [_ status]]
    { :db (assoc (:db cofx) 
                 :connection-status status)
      :dispatch [:new-notification 
                  ["Disconnected from server" "Please try again later." "is-danger"]]}))

(rf/reg-event-fx
  :set-connection-status
  (fn [cofx [_ status]]
    (let [current-status (get-in cofx [:db :connection-status])]
      (cond-> {:db (assoc (:db cofx) :connection-status status)}
        (not= status current-status)
        (assoc :dispatch 
          (if status
            [:new-notification 
              [ "Reconnected to server :)" 
                "" 
                "is-success"]]
            [:new-notification 
              [ "Disconnected from server :(" 
                "Please try again later." 
                "is-danger"]]))))))

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
