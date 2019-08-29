(ns space.api.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as rjson]
            [ring.middleware.cors :as cors]
            [buddy.auth.middleware :as auth]
            [compojure.core :as c]
            [compojure.route :as route]
            [space.api.response :as r]
            [space.api.security.core :as s]
            [space.api.db.core :as db]
            [space.api.users.core :as users]
            [space.api.forum.core :as forum]
            [space.api.db.forum.core :as temp]
            [space.api.db.users.sign-in :as sign-in]))

;; Forward declarations
(declare start-server api-handler)

(defn -main
  "Prepare to start the server"
  [& [db-host db-port]]
  (db/setup-db db-host db-port)
  (start-server))

(defn start-server
  "Start API server"
  []
  (println "Starting API server now..")
  (jetty/run-jetty api-handler {:port 3000}))

;; Describes how to respond to different URLs with Compojure
(c/defroutes router
  (c/GET  "/" [] db/send-space-info)
  (c/GET  "/forum/get-page-count" [] forum/get-forum-page-count)
  (c/GET  "/forum/page-:page{[0-9]+}" [page] forum/get-forum-page)
  (c/POST "/forum/submit" [] temp/submit-forum-post)
  (c/GET  "/post/:post{[0-9]+}" [post] forum/get-forum-post)
  (c/GET  "/user/:username" [username] users/get-user-data)
  (c/POST "/sign-in" [] sign-in/attempt-sign-in)
  (route/not-found 
      (r/bad-request {:message "API User Error: Invalid route."})))

;; Wraps middleware around router
(def api-handler
  (as-> router $
    (auth/wrap-authorization $ s/auth-backend)
    (auth/wrap-authentication $ s/auth-backend)
    (cors/wrap-cors $
      :access-control-allow-origin [#"http://localhost:8080"]
      :access-control-allow-methods [:get :put :post :delete])
    (rjson/wrap-json-response $ {:pretty true})
    (rjson/wrap-json-body $
      { :keywords? true 
        :bigdecimals? true})))

