(ns space.site.cljs.events.time
  (:require [re-frame.core :as rf]
            [clojure.string :as str]))

(defn dispatch-timer-event
  "Update the time"
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; usage:  (dispatch [:timer a-js-Date])
;; every second an event of this kind will be dispatched
;; note how the 2nd parameter is destructured to obtain the data value
;; compute and return the new application state
(rf/reg-event-db
  :timer
  (fn [db [_ new-time]]
    (assoc db :time new-time)))

;; db is current app state. 2nd unused param is query vector
;; return a query computation over the application state
(rf/reg-sub
  :time
  (fn [db _]     
    (:time db))) 

(defn get-current-time
  "Returns the current time as string"
  []
  (when-let [t @(rf/subscribe [:time])]
    (-> t
        .toTimeString
        (str/split " ")
        first)))
