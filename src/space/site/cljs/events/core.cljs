(ns space.site.cljs.events.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [day8.re-frame.http-fx :as http]
            [ajax.core :as ajax]))

(declare make-http-get-request)

;; Import subscriptions
(rfr/register-subscriptions)

;; Initialise state with default values
(rf/reg-event-fx
  :initialize
  (fn [{:keys [db]} _]
    {:db {:connection-status true
          :notifications []
          :page-count 0
          :posts []
          :time (js/Date.)
          :time-color "#f88"}
     :pushy-init true
     :dispatch [:attempt-ping true]}))

;; Make a HTTP-GET request
(rf/reg-event-fx
  :http-get
  (fn [_ [_ [uri success fail]]]
    { :dispatch [:attempt-ping false]
      :http-xhrio (make-http-get-request uri success fail)}))

;; Good http calls set server status to true
(rf/reg-event-fx
  :good-http-result
  (fn [cofx [_ result]]
    { :db (assoc (:db cofx) :success-http-result result)
      :dispatch [:set-connection-status true]}))

;; Bad http calls set server status to false
(rf/reg-event-fx
  :bad-http-result
  (fn [cofx [_ result]]
    (println "Disconnected from server. \nMessage: " result)
    { :db (assoc (:db cofx) :failure-http-result result)
      :dispatch [:set-connection-status false]}))

;; Fires a notification when server status changes
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
                "You may need to refresh the page." 
                "is-success"]]
            [:new-notification 
              [ "Disconnected from server :(" 
                "Please try again later." 
                "is-danger"]]))))))


;; Attempt to connect to server if disconnected OR forced
(rf/reg-event-fx
  :attempt-ping
  (fn [cofx [_ forced]]
    (let [connected (get-in cofx [:db :connection-status])]
      (when (or forced (not connected))
        (println "Attempting to connect to server..")
        { :http-xhrio 
            (make-http-get-request "ping" :good-http-result :bad-http-result)}))))

(defn dispatch-ping-event []
  (rf/dispatch [:attempt-ping false]))

;; Every 10 seconds, if the server is down, attempt to reconnect
(defonce do-pinging (js/setInterval dispatch-ping-event 10000))

(defn- make-http-get-request
  "Creates a HTTP request"
  [uri on-success on-fail]
  { :method          :get
    :uri             (str "http://localhost:3000/" uri)
    ;; optional see API docs
    :timeout         8000
    ;; IMPORTANT!: You must provide this.
    :response-format (ajax/json-response-format {:keywords? true})
    :on-success      [on-success]
    :on-failure      [on-fail]})



;; Common Events / Subscriptions ---------------------------------------------------

;; Allow querying of number of pages for current page
(rf/reg-sub
  :page-count
  (fn [db _]
    (:page-count db))) 
