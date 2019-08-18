(ns space.site.cljs.events.sign-in
  (:require [re-frame.core :as rf]))

;; Retrieve the sign-in attempt's response
(rf/reg-event-fx
  :good-sign-in-response
  (fn [cofx [_ response]]
    (let [token (:token response)
          user  (:user response)]
      (if (and token user)
        { :db (assoc (:db cofx) 
              :token token 
              :user user)
          :nav-to "/"
          :dispatch
          [ :new-notification
            [ (str "Hello, " (:usernick user) "!")
              "Welcome to Space."
              "is-success"
              "fa-user-astronaut"]]}
        (do
          (println "Site Error: (event :good-sign-in-response)")
          {:db (assoc (:db cofx) :token nil :user nil)})))))

;; Retrieve the sign-in attempt's response
(rf/reg-event-fx
  :bad-sign-in-response
  (fn [cofx [_ response]]
    { :db (assoc (:db cofx) 
          :token nil 
          :username nil
          :usernick nil)
      :dispatch
      [ :new-notification
        [ "Sign-in failed"
          (:message (:response response))
          "is-danger"
          "fa-user-times"]]}))

(defn dispatch-sign-in-request
  "Prepare to send a POST request to sign the user in"
  [username password]
  (fn []
    (rf/dispatch [:http-post
        [ "sign-in"
          { :username username
            :password password }
          :good-sign-in-response
          :bad-sign-in-response]])))
