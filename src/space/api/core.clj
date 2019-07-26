(ns space.api.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]))

(defn- str-to 
  "Returns a string counting up to a number"
  [num]
  (apply str (interpose ", " (range 1 (inc num)))))

(defn- str-from 
  "Returns a string counting down from a number"
  [num]
  (apply str (interpose ", " (reverse (range 1 (inc num))))))

(defroutes router
  (GET "/" [] "<h1>Hello World :)</h1>")
  (GET "/count-up/:to" [to] (str-to (Integer. to)))
  (GET "/count-down/:from" [from] (str-from (Integer. from)))
  (route/not-found "<h1>Page not found :(</h1>"))

(def api-handler
  (cors/wrap-cors router
    :access-control-allow-origin [#"http://localhost:8080"]
    :access-control-allow-methods [:get :put :post :delete]))

(defn -main
  [& _]
  (println "Starting API server now..")
  (jetty/run-jetty api-handler {:port 3000}))
