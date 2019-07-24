(ns server.space.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]))

(defn app-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello from Ring"})

(defn -main
  [& _]
  (jetty/run-jetty app-handler {:port 8080}))
