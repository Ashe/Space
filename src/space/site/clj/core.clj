(ns space.site.clj.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.cors :as cors]
            [ring.util.response :as response]
            [ring.middleware.content-type :as mcontent]
            [ring.middleware.file :as mfile]
            [ring.middleware.not-modified :as mnmod]))

(defonce resource-dir "resources/public")

(defn router [req]
  (let [is-file (some-> req
                        :uri
                        (clojure.string/split #"/")
                        last
                        (clojure.string/includes? "."))]
    (if is-file
      (if-let [res (mfile/file-request req resource-dir)]
        (-> res
            (mnmod/not-modified-response req)
            (mcontent/content-type-response req))
        (response/not-found "Not Found"))
      (assoc-in (response/file-response resource-dir)
                [:headers "Content-Type"]
                "text/html;charset=utf8"))))

(def site-handler router)

(defn -main
  [& _]
  (println "Starting site server now..")
  (jetty/run-jetty site-handler {:port 8080}))


