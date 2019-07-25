(ns server.space.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/plain;=us-ascii"}
   :body (str request)})

(defn- str-to 
  "Returns a string counting up to a number"
  [num]
  (apply str (interpose ", " (range 1 (inc num)))))

(defn- str-from 
  "Returns a string counting down from a number"
  [num]
  (apply str (interpose ", " (reverse (range 1 (inc num))))))

(defroutes app
  (GET "/" [] "<h1>Hello World :)</h1>")
  (GET "/count-up/:to" [to] (str-to (Integer. to)))
  (GET "/count-down/:from" [from] (str-from (Integer. from)))
  (route/not-found "<h1>Page not found :(</h1>"))

(def handler
  (cors/wrap-cors app  
      :access-control-allow-origin [#"http://localhost:9500"]
      :access-control-allow-methods [:get :put :post :delete]))

(defn -main
  [& _]
  (println "Starting server now..")
  (jetty/run-jetty handler {:port 3000}))
