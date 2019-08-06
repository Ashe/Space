(ns space.common.core
  (:require #?( :clj  [clojure.core :as c]
                :cljs [cljs.reader :as c])))

(defn str->num
  "Converts a string into number safely, or 0 if not"
  [string]
  (let [maybe-number (c/read-string string)]
    (if (number? maybe-number)
      maybe-number
      0)))

