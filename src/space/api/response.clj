(ns space.api.response)

;; Wrappers for different responses by the API
(defn ok [d] {:status 200 :body d})
(defn bad-request [d] {:status 400 :body d})

