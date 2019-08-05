(ns space.api.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [compojure.core :as c]
            [compojure.route :as route]
            [space.api.db.core :as db]))

;; Forward declarations
(declare start-server api-handler validate)

(defn -main
  "Prepare to start the server"
  [& [db-host]]
  (db/setup-db db-host)
  (start-server))

(defn start-server
  "Start API server"
  []
  (println "Starting API server now..")
  (jetty/run-jetty api-handler {:port 3000}))

;; Describes how to respond to different URLs
(c/defroutes router
  (c/GET "/" [] "<h1>Hello World :)</h1>")
  (c/GET "/ping" [] (json/write-str {:response "pong"}))
  (c/GET "/forum" [] (db/get-forum-page 0))
  (c/GET "/forum/page-:page" [page] (validate page db/get-forum-page))
  (route/not-found "<h1>Page not found :(</h1>"))

;; Wraps around the router to allow cross origin
(def api-handler
  (cors/wrap-cors router
    :access-control-allow-origin [#"http://localhost:8080"]
    :access-control-allow-methods [:get :put :post :delete]))

(defn- validate
  "Ensure user input is safe"
  [string on-success]
  (let [maybe-number (read-string string)]
    (when (number? maybe-number)
      (on-success maybe-number))))

