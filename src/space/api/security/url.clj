(ns space.api.security.url)

;; Use Apache Commons' URL validator
(import 'org.apache.commons.validator.UrlValidator)

(defn valid-url? 
  "Validate a URL"
  [url-str]
  (let [validator (UrlValidator.)]
    (.isValid validator url-str)))
