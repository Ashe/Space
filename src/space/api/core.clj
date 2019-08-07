(ns space.api.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [compojure.core :as c]
            [compojure.route :as route]
            [space.api.db.core :as db]
            [space.common.core :as cmn]))

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
  (c/GET  "/" [] "<h1>Hello World :)</h1>")
  (c/GET  "/ping" [] (json/write-str {:response "pong"}))
  (c/GET  "/forum/get-page-count" [] (db/get-forum-page-count))
  (c/GET  "/forum/page-:page" [page] (db/get-forum-page (cmn/str->num page)))
  (c/POST "/forum/submit" post (db/submit-forum-post post))
  (route/not-found "<h1>Page not found :(</h1>"))

;; Wraps around the router to allow cross origin
(def api-handler
  (cors/wrap-cors router
    :access-control-allow-origin [#"http://localhost:8080"]
    :access-control-allow-methods [:get :put :post :delete]))
