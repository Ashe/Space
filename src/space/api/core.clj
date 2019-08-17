(ns space.api.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clj-time.core :as time]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as rjson]
            [ring.middleware.cors :as cors]
            [buddy.auth.backends.token :as jws]
            [buddy.auth.middleware :as auth]
            [buddy.sign.jwt :as jwt]
            [compojure.core :as c]
            [compojure.route :as route]
            [space.api.db.core :as db]
            [space.common.core :as cmn]
            [space.api.response :as r]))

;; Forward declarations
(declare start-server api-handler sign-in-handler)

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
  (c/GET  "/" [] (r/ok {:message "This is the Space API server."}))
  (c/GET  "/forum/get-page-count" [] db/get-forum-page-count)
  (c/GET  "/forum/page-:page{[0-9]+}" [page] (db/get-forum-page (cmn/str->num page)))
  (c/POST "/forum/submit" [] db/submit-forum-post)
  (c/GET  "/post/:post{[0-9]+}" [post] (db/get-forum-post (cmn/str->num post)))
  (c/POST "/sign-in" [] sign-in-handler)
  (route/not-found (r/bad-request {:message "API User Error: Invalid route."})))

(def users {:space "nebula"})
(def secret "mysupersecret")
(def auth-backend (jws/jws-backend {:secret secret :options {:alg :hs512}}))

;; Wraps middleware around router
(def api-handler
  (as-> router $
    (auth/wrap-authorization $ auth-backend)
    (auth/wrap-authentication $ auth-backend)
    (cors/wrap-cors $
      :access-control-allow-origin [#"http://localhost:8080"]
      :access-control-allow-methods [:get :put :post :delete])
    (rjson/wrap-json-response $ {:pretty true})
    (rjson/wrap-json-body $
      { :keywords? true 
        :bigdecimals? true})))

(defn sign-in-handler
  "Authorise a user"
  [request]
  (let [body (:body request)
        username (:username body)
        password (:password body)
        valid? (some-> users
            (get (keyword username))
            (= password))]
    (if valid?
      (let [claims 
              { :user (keyword username)
                :exp (time/plus (time/now) (time/seconds 3600))}
            token (jwt/sign claims secret {:alg :hs512})]
      (r/ok {:token token}))
      (r/bad-request {:message "Unrecognised credentials."}))))
    
