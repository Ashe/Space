(ns space.api.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [compojure.core :as c]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [space.api.db.core :as db]))

;; Forward declarations
(declare api-handler get-forum-page forum-post)

(defn -main
  "Prepare to start the server"
  [& _]
  (db/setup-db)
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
  (c/GET "/forum" [] (get-forum-page 0))
  (c/GET "/forum/page-:page" [page] (get-forum-page page))
  (route/not-found "<h1>Page not found :(</h1>"))

;; Wraps around the router to allow cross origin
(def api-handler
  (cors/wrap-cors router
    :access-control-allow-origin [#"http://localhost:8080"]
    :access-control-allow-methods [:get :put :post :delete]))

;; How many posts from the database to send per front-end page
(def posts-per-page 10)

(defn- get-forum-page
  "Gets a given page of the forum"
  [page-num]
  (json/write-str [(take posts-per-page (map forum-post (iterate inc 0)))]))

;; @TODO: Merge this with the front end to have one common definition
(defn- forum-post
  [post-num]
  { :post-number post-num
    :poster-id 0
    :poster-name "Example User"
    :poster-alias "foo"
    :post-date "1m ago"
    :post-summary 
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
        Proin ornare magna eros, eu pellentesque tortor vestibulum ut. 
        Maecenas non massa sem. Etiam finibus odio quis feugiat facilisis."
    :tag-ids [0 1 2]})
