(ns space.api.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as rjson]
            [ring.middleware.cors :as cors]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as auth]
            [compojure.core :as c]
            [compojure.route :as route]
            [space.api.db.core :as db]
            [space.common.core :as cmn]))

;; Forward declarations
(declare start-server api-handler validate)

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
  (c/GET  "/" [] "<h1>Hello World :)</h1>")
  (c/GET  "/ping" [] (json/write-str {:response "pong"}))
  (c/GET  "/forum/get-page-count" [] (db/get-forum-page-count))
  (c/GET  "/forum/page-:page{[0-9]+}" [page] (db/get-forum-page (cmn/str->num page)))
  (c/POST "/forum/submit" [] db/submit-forum-post)
  (c/GET  "/post/:post{[0-9]+}" [post] (db/get-forum-post (cmn/str->num post)))
  (route/not-found "<h1>Page not found :(</h1>"))

(def secret "mysecret")
(def backend (backends/jws {:secret secret}))

;; Wraps middleware around router
(def api-handler
  (-> router
    (auth/wrap-authentication backend)
    (cors/wrap-cors 
      :access-control-allow-origin [#"http://localhost:8080"]
      :access-control-allow-methods [:get :put :post :delete])
    (rjson/wrap-json-body 
      { :keywords? true 
        :bigdecimals? true})))
