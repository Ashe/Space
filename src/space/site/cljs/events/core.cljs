(ns space.site.cljs.events.core
  (:require [re-frame.core :as rf]
            [re-frame-routing.core :as rfr]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

;; Forward declarations
(declare make-http-get-request make-http-post-request)

;; Import subscriptions
(rfr/register-subscriptions)

;; Initialise state with default values
(rf/reg-event-fx
  :initialize
  (fn [{:keys [db]} _]
    {:db {:space-info nil
          :connection-status true
          :user nil
          :viewed-user nil
          :notifications []
          :page-count 0
          :posts []
          :post nil
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

;; Make a HTTP-POST request
(rf/reg-event-fx
  :http-post
  (fn [_ [_ [uri data success fail]]]
    { :dispatch [:attempt-ping false]
      :http-xhrio (make-http-post-request uri (clj->js data) success fail)}))

;; On ping/reconnect, synchronise data to server
(rf/reg-event-fx
  :receive-server-info
  (fn [cofx [_ result]]
    { :db (assoc (:db cofx) 
          :success-http-result result
          :space-info (:space-info result))
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
                "You may need to refresh the Space to see what's new." 
                "is-success"
                "fa-heart"]]
            [:new-notification 
              [ "Disconnected from server :(" 
                "Please try again later." 
                "is-danger"
                "fa-heart-broken"]]))))))

;; Attempt to connect to server if disconnected OR forced
(rf/reg-event-fx
  :attempt-ping
  (fn [cofx [_ forced]]
    (let [connected (get-in cofx [:db :connection-status])]
      (when (or forced (not connected))
        (println "Attempting to connect to server..")
        { :http-xhrio 
            (make-http-get-request "" 
                :receive-server-info 
                :bad-http-result)}))))

(defn dispatch-ping-event []
  (rf/dispatch [:attempt-ping false]))

;; Every 10 seconds, if the server is down, attempt to reconnect
(defonce do-pinging (js/setInterval dispatch-ping-event 10000))

(defn- make-http-get-request
  "Creates a HTTP-GET request"
  [uri on-success on-fail]
  (let [user @(rf/subscribe [:user])
        token (:token user)]
    { :method           :get
      :uri              (str "http://localhost:3000/" uri)
      :timeout          8000
      :response-format  (ajax/json-response-format {:keywords? true})
      :headers          (when token [:Authorization (str "Token " token)])
      :on-success       [on-success]
      :on-failure       [on-fail]}))

(defn- make-http-post-request
  "Creates a HTTP-POST request"
  [uri data on-success on-fail]
  (let [user @(rf/subscribe [:user])
        token (:token user)]
    { :method           :post
      :uri              (str "http://localhost:3000/" uri)
      :params           data 
      :timeout          8000
      :format           (ajax/json-request-format)
      :response-format  (ajax/json-response-format {:keywords? true})
      :headers          (when token [:Authorization (str "Token " token)])
      :on-success       [on-success]
      :on-failure       [on-fail]}))

;; Common Events / Subscriptions -----------------------------------

;; Access server info
(rf/reg-sub
  :space-info
  (fn [db _]
    (:space-info db)))

;; Query the user's info (including their token)
(rf/reg-sub
  :user
  (fn [db _]
    (:user db)))

;; Allow querying of number of pages for current page
(rf/reg-sub
  :page-count
  (fn [db _]
    (:page-count db))) 

