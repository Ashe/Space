(ns ^:figwheel-hooks space.site.cljs.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [re-frame-routing.core :as rfr]
            [ajax.core :as ajax]
            [clojure.string :as str]))

;; app.core

(def routes ["/" {"" :home
                  "home" :home}])

(rfr/register-subscriptions)
(rfr/register-events {:routes routes})

;; Container views (usally in seperate views)

(defn home
  [{:keys [route-key path-params query-params]}]
  [:div "Home"])

(defn not-found
  [{:keys [route-key path-params query-params]}]
  [:div "Page not found"])

(defn- wrap-container
  [container-view]
  (let [route-key (rf/subscribe [:router/route])
        path-params (rf/subscribe [:router/route-params])
        query-params (rf/subscribe [:router/route-query])]
    (fn []
      [container-view {:route-key @route-key
                       :path-params @path-params
                       :query-params @query-params}])))

;; app.router.core

(defmulti containers identity)

(defmethod containers 
  :home [] [(wrap-container home)])

;; CATCH-ALL ROUTE
(defmethod containers
  :default [] [not-found])

(defn router []
  (let [route-key (rf/subscribe [:router/route])]
    (fn [] 
      (js/console.log @route-key)
      [containers @route-key])))


;; A detailed walk-through of this source code is provided in the docs:
;; https://github.com/Day8/re-frame/blob/master/docs/CodeWalkthrough.md

;; -- Domino 1 - Event Dispatch -----------------------------------------------

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))


;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-fx            ;; sets up initial application state
  :initialize               ;; usage:  (dispatch [:initialize])
  (fn [{:keys [db]} _]      ;; the two parameters are not important here, so use _
    {:db {:time (js/Date.)  ;; What it returns becomes the new application state
     :time-color "#f88"}
     :pushy-init true}))    ;; so the application state will initially be a map with two keys


(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
  :time-color-change            ;; dispatched when the user enters a new colour into the UI text field
  (fn [db [_ new-color-value]]  ;; -db event handlers given 2 parameters:  current application state and event (a vector)
    (assoc db :time-color new-color-value)))   ;; compute and return the new application state


(rf/reg-event-db                 ;; usage:  (dispatch [:timer a-js-Date])
  :timer                         ;; every second an event of this kind will be dispatched
  (fn [db [_ new-time]]          ;; note how the 2nd parameter is destructured to obtain the data value
    (assoc db :time new-time)))  ;; compute and return the new application state

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


;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
  :time
  (fn [db _]     ;; db is current app state. 2nd unused param is query vector
    (:time db))) ;; return a query computation over the application state

(rf/reg-sub
  :time-color
  (fn [db _]
    (:time-color db)))


;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock
  []
  [:h1.title.is-1.has-text-centered {
      :style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn color-input
  []
  [:div.field
    [:label.label "Time color: "]
    [:div.control.has-icons-left
      [:input.input {
          :type "text"
          :value @(rf/subscribe [:time-color])
          :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]
      [:span.icon.is-small.is-left
          [:i.fas.fa-palette]]]])

(def times-clicked (reagent/atom 0))

(defn get-greeting
  "Get the greeting of the page"
  [n]
  (let [evers (repeat n "ever ")]
    (str "My first " (apply str evers) " react component!")))

(defn my-component []
  (let [n-evers @times-clicked]
    [:div
      [:p.is-size-4.has-text-centered (get-greeting n-evers)]
      [:br]
      [:div.field
        [:div.columns
          [:div.column
            [:button.button.is-link.is-fullwidth {
                :type "button"
                :on-click #(swap! times-clicked inc)}
              (str "Clicked " n-evers " times!")]]
          [:div.column
            [:button.button.is-link.is-fullwidth {
                :type "button"
                :on-click #(reset! times-clicked 0)}
              "Reset"]]]]
      [:div.columns
        [:div.column.has-text-centered
          [:div.field
            [:button.button.is-link.is-large {
                :type "button"
                :on-click #(rf/dispatch [:handler-with-http])}
              "Get stuff"]]]]]))

(defn ui
  []
  [:section.hero.is-fullheight.is-primary.is-bold
    [:div.hero-body
      [:div.box
          {:style {:margin :auto}}
        [:h1.subtitle.is-3.has-text-primary.has-text-centered
          "Hello world, it is now.."]
        [clock]
        [color-input]
        [my-component]
        [router]]]])

;; -- Entry Point -------------------------------------------------------------

(defn run-app
  "Use this function to (re)start the site"
  []
  (rf/dispatch-sync [:initialize])  ;; puts a value into application state
  (reagent/render [ui]              ;; mount the application's ui into '<div id="app" />'
                  (js/document.getElementById "app")))

;; This is called at the start of the site
(defn ^:export run []
  (run-app))

;; This is called every time you make a code change
(defn ^:after-load reload []
  (run-app))
